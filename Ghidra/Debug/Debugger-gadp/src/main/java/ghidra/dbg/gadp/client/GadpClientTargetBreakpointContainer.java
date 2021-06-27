/* ###
 * IP: GHIDRA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ghidra.dbg.gadp.client;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import ghidra.dbg.attributes.TargetObjectRef;
import ghidra.dbg.attributes.TypedTargetObjectRef;
import ghidra.dbg.gadp.client.annot.GadpEventHandler;
import ghidra.dbg.gadp.protocol.Gadp;
import ghidra.dbg.gadp.protocol.Gadp.Path;
import ghidra.dbg.gadp.util.GadpValueUtils;
import ghidra.dbg.target.*;
import ghidra.dbg.target.TargetBreakpointSpec.TargetBreakpointAction;
import ghidra.dbg.target.TargetBreakpointSpec.TargetBreakpointKind;
import ghidra.program.model.address.AddressRange;
import ghidra.util.datastruct.ListenerSet;

public interface GadpClientTargetBreakpointContainer extends GadpClientTargetObject,
		TargetBreakpointContainer<GadpClientTargetBreakpointContainer> {

	@Override
	default CompletableFuture<Void> placeBreakpoint(AddressRange range,
			Set<TargetBreakpointKind> kinds) {
		getDelegate().assertValid();
		return getModel()
				.sendChecked(
					Gadp.BreakCreateRequest.newBuilder()
							.setPath(GadpValueUtils.makePath(getPath()))
							.setAddress(GadpValueUtils.makeRange(range))
							.setKinds(GadpValueUtils.makeBreakKindSet(kinds)),
					Gadp.BreakCreateReply.getDefaultInstance())
				.thenApply(rep -> null);
	}

	@Override
	default CompletableFuture<Void> placeBreakpoint(String expression,
			Set<TargetBreakpointKind> kinds) {
		getDelegate().assertValid();
		return getModel()
				.sendChecked(
					Gadp.BreakCreateRequest.newBuilder()
							.setPath(GadpValueUtils.makePath(getPath()))
							.setExpression(expression)
							.setKinds(GadpValueUtils.makeBreakKindSet(kinds)),
					Gadp.BreakCreateReply.getDefaultInstance())
				.thenApply(rep -> null);
	}

	@GadpEventHandler(Gadp.EventNotification.EvtCase.BREAK_HIT_EVENT)
	default void handleBreakHitEvent(Gadp.EventNotification notification) {
		Gadp.BreakHitEvent evt = notification.getBreakHitEvent();
		TargetObjectRef trapped = getModel().getProxyOrStub(evt.getTrapped().getEList());
		Path framePath = evt.getFrame();
		TypedTargetObjectRef<? extends TargetStackFrame<?>> frame =
			framePath == null || framePath.getECount() == 0 ? null
					: getModel().getProxyOrStub(framePath.getEList()).as(TargetStackFrame.tclass);
		Path specPath = evt.getSpec();
		TypedTargetObjectRef<? extends TargetBreakpointSpec<?>> spec = specPath == null ? null
				: getModel().getProxyOrStub(specPath.getEList()).as(TargetBreakpointSpec.tclass);
		Path bptPath = evt.getEffective();
		TypedTargetObjectRef<? extends TargetBreakpointLocation<?>> breakpoint = bptPath == null
				? null
				: getModel().getProxyOrStub(bptPath.getEList()).as(TargetBreakpointLocation.tclass);
		getDelegate().listeners.fire(TargetBreakpointListener.class)
				.breakpointHit(this, trapped, frame, spec, breakpoint);
		if (spec instanceof GadpClientTargetBreakpointSpec) {
			// If I don't have a cached proxy, then I don't have any listeners
			GadpClientTargetBreakpointSpec specObj = (GadpClientTargetBreakpointSpec) spec;
			ListenerSet<TargetBreakpointAction> actions = specObj.getDelegate().getActions(false);
			if (actions != null) {
				actions.fire.breakpointHit(specObj, trapped, frame, breakpoint);
			}
		}
	}
}
