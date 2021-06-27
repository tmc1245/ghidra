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
package ghidra.dbg.sctl.client;

import ghidra.dbg.attributes.TargetDataType;
import ghidra.dbg.util.PathUtils;

public class SctlTargetCompositeField extends SctlTargetDataTypeMember {

	protected static String indexField(long position) {
		return PathUtils.makeIndex(position);
	}

	protected static String keyField(long position) {
		return PathUtils.makeKey(indexField(position));
	}

	public SctlTargetCompositeField(SctlTargetNamedDataType<?, ?> type, int position, long offset,
			String memberName, TargetDataType dataType) {
		super(type, keyField(position), position, offset, memberName, dataType, "TypeField");
	}

	@Override
	public String toString() {
		return "<" + getClass().getSimpleName() + " " + getName() + " " + parent.getIndex() + "." +
			getMemberName() + " @+" + getOffset() + ">";
	}
}
