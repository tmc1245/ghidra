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
package ghidra.dbg;

/**
 * A listener for events related to the debugger model, usually a connection
 * 
 * <p>
 * TODO: Most (non-client) models do not implement this. Even the client ones do not implement
 * {@link #modelStateChanged()}
 */
public interface DebuggerModelListener {

	/**
	 * The model has been successfully opened
	 * 
	 * <p>
	 * For example, the connection to a debugger daemon has been established and negotiated.
	 */
	default public void modelOpened() {
	}

	/**
	 * The model was closed
	 * 
	 * <p>
	 * For example, the remote closed the connection, or the connection was lost. Whatever the case,
	 * the model is invalid after this callback.
	 * 
	 * @param reason the reason for the model to close
	 */
	default public void modelClosed(DebuggerModelClosedReason reason) {
	}

	/**
	 * The model's state has changed, prompting an update to its description
	 */
	default public void modelStateChanged() {
	}
}
