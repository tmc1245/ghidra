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
package ghidra.dbg.target;

import java.util.concurrent.CompletableFuture;

import ghidra.dbg.attributes.TypedTargetObjectRef;

/**
 * A common interface for all {@link TargetObject} interfaces, as well as implementations of
 * {@link TargetObject}, so that {@link #fetch()} simply returns this object.
 * 
 * @param <T> the type of this object. It should be a type variable extending this object's type if
 *            the class is meant to be further extended.
 */
public interface TypedTargetObject<T extends TypedTargetObject<T>>
		extends TargetObject, TypedTargetObjectRef<T> {
	@Override
	@SuppressWarnings("unchecked")
	default CompletableFuture<? extends T> fetch() {
		return (CompletableFuture<? extends T>) CompletableFuture.completedFuture(this);
	}
}
