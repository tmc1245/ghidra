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
package agent.dbgeng.model.impl;

import java.util.List;
import java.util.Map;

import agent.dbgeng.model.iface2.DbgModelTargetAvailable;
import agent.dbgeng.model.iface2.DbgModelTargetAvailableContainer;
import ghidra.dbg.target.schema.*;
import ghidra.dbg.util.PathUtils;

@TargetObjectSchemaInfo(name = "Available", elements = { //
	@TargetElementType(type = Void.class) //
}, attributes = { //
	@TargetAttributeType(type = Void.class) //
})
public class DbgModelTargetAvailableImpl extends DbgModelTargetObjectImpl
		implements DbgModelTargetAvailable {

	protected static final String PID_ATTRIBUTE_NAME = PREFIX_INVISIBLE + "pid";
	// TODO: DESCRIPTION, TYPE, USER?

	protected static String indexAttachable(int pid) {
		return Integer.toHexString(pid);
	}

	protected static String keyAttachable(int pid) {
		return PathUtils.makeKey(indexAttachable(pid));
	}

	protected final int pid;

	public DbgModelTargetAvailableImpl(DbgModelTargetAvailableContainer parent, int pid,
			String name) {
		super(parent.getModel(), parent, keyAttachable(pid), name);
		this.pid = pid;

		this.changeAttributes(List.of(), List.of(), Map.of(//
			PID_ATTRIBUTE_NAME, (long) pid, //
			DISPLAY_ATTRIBUTE_NAME, keyAttachable(pid) + " : " + name.trim(),
			UPDATE_MODE_ATTRIBUTE_NAME, TargetUpdateMode.FIXED //
		), "Initialized");
	}

	public DbgModelTargetAvailableImpl(DbgModelTargetAvailableContainer parent, int pid) {
		super(parent.getModel(), parent, keyAttachable(pid), "Attachable");
		this.pid = pid;

		this.changeAttributes(List.of(), List.of(), Map.of(//
			PID_ATTRIBUTE_NAME, (long) pid, //
			DISPLAY_ATTRIBUTE_NAME, keyAttachable(pid), //
			UPDATE_MODE_ATTRIBUTE_NAME, TargetUpdateMode.FIXED //
		), "Initialized");
	}

	@TargetAttributeType(name = PID_ATTRIBUTE_NAME, hidden = true)
	@Override
	public long getPid() {
		return pid;
	}

}
