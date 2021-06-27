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
package agent.dbgeng.model.iface2;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import agent.dbgeng.manager.DbgEventsListenerAdapter;
import agent.dbgeng.manager.DbgStackFrame;
import agent.dbgeng.manager.impl.DbgManagerImpl;
import agent.dbgeng.manager.impl.DbgThreadImpl;
import agent.dbgeng.model.iface1.DbgModelSelectableObject;
import ghidra.dbg.attributes.TargetObjectRef;
import ghidra.dbg.target.TargetObject;
import ghidra.dbg.target.TargetStackFrame;
import ghidra.program.model.address.Address;
import ghidra.program.model.address.AddressSpace;

public interface DbgModelTargetStackFrame extends //
		//DbgModelTargetObject,
		TargetStackFrame<DbgModelTargetStackFrame>, //
		DbgEventsListenerAdapter, //
		DbgModelSelectableObject {

	public static final String FUNC_ATTRIBUTE_NAME = "function";
	public static final String FUNC_TABLE_ENTRY_ATTRIBUTE_NAME = "Table Entry";
	public static final String FRAME_OFFSET_ATTRIBUTE_NAME = "Frame Offset";
	public static final String INST_OFFSET_ATTRIBUTE_NAME = "Inst. Offset";
	public static final String RETURN_OFFSET_ATTRIBUTE_NAME = "Return Offset";
	public static final String STACK_OFFSET_ATTRIBUTE_NAME = "Stack Offset";
	public static final String VIRTUAL_ATTRIBUTE_NAME = "Virtual";
	public static final String PARAM0_ATTRIBUTE_NAME = "Param[0]";
	public static final String PARAM1_ATTRIBUTE_NAME = "Param[1]";
	public static final String PARAM2_ATTRIBUTE_NAME = "Param[2]";
	public static final String PARAM3_ATTRIBUTE_NAME = "Param[3]";
	public static final String FROM_ATTRIBUTE_NAME = PREFIX_INVISIBLE + "from"; // TODO

	@Override
	public default CompletableFuture<Void> select() {
		DbgManagerImpl manager = getManager();
		DbgThreadImpl thread = manager.getCurrentThread();
		return manager.selectThread(thread);
	}

	@Override
	public default CompletableFuture<Void> init(Map<String, Object> map) {
		AddressSpace space = getModel().getAddressSpace("ram");
		return requestNativeAttributes().thenCompose(attrs -> {
			if (attrs == null) {
				return CompletableFuture.completedFuture(null);
			}
			TargetObject attributes = (TargetObject) attrs.get("Attributes");
			if (attributes == null) {
				return CompletableFuture.completedFuture(null);
			}
			return attributes.fetchAttributes(true);
		}).thenCompose(subattrs -> {
			if (subattrs == null) {
				return CompletableFuture.completedFuture(null);
			}
			TargetObject frameNumber = (TargetObject) subattrs.get("FrameNumber");
			return frameNumber.fetchAttribute(VALUE_ATTRIBUTE_NAME).thenCompose(noval -> {
				String nostr = noval.toString();
				TargetObject instructionOffset = (TargetObject) subattrs.get("InstructionOffset");
				return instructionOffset.fetchAttribute(VALUE_ATTRIBUTE_NAME).thenAccept(pcval -> {
					String oldval = (String) getCachedAttribute(DISPLAY_ATTRIBUTE_NAME);
					String pcstr = pcval.toString();
					long pc = Long.parseUnsignedLong(pcstr, 16);
					map.put(PC_ATTRIBUTE_NAME, space.getAddress(pc));
					String display = String.format("#%s 0x%s", nostr, pcstr);
					map.put(DISPLAY_ATTRIBUTE_NAME, display);
					setModified(map, !display.equals(oldval));
				});
			});
		});
	}

	public void setFrame(DbgStackFrame frame);

	public TargetObjectRef getThread();

	public Address getPC();

	public DbgModelTargetProcess getProcess();

}
