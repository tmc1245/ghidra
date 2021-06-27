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
package ghidra.dbg.util;

import java.util.Collection;
import java.util.Map;

import ghidra.dbg.attributes.TargetObjectRef;
import ghidra.dbg.target.TargetObject;
import ghidra.dbg.target.TargetObject.TargetObjectListener;
import ghidra.dbg.util.ElementsChangedListener.ElementsChangedInvocation;

public class ElementsChangedListener extends AbstractInvocationListener<ElementsChangedInvocation>
		implements TargetObjectListener {
	public static class ElementsChangedInvocation {
		public final TargetObject parent;
		public final Collection<String> removed;
		public final Map<String, ? extends TargetObjectRef> added;

		public ElementsChangedInvocation(TargetObject parent, Collection<String> removed,
				Map<String, ? extends TargetObjectRef> added) {
			this.parent = parent;
			this.removed = removed;
			this.added = added;
		}
	}

	@Override
	public void elementsChanged(TargetObject parent, Collection<String> removed,
			Map<String, ? extends TargetObjectRef> added) {
		record(new ElementsChangedInvocation(parent, removed, added));
	}
}
