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

import ghidra.dbg.DebuggerTargetObjectIface;
import ghidra.dbg.target.schema.TargetAttributeType;

/**
 * An object which has an execution life cycle
 */
@DebuggerTargetObjectIface("ExecutionStateful")
public interface TargetExecutionStateful<T extends TargetExecutionStateful<T>>
		extends TypedTargetObject<T> {
	enum Private {
		;
		private abstract class Cls implements TargetExecutionStateful<Cls> {
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	Class<Private.Cls> tclass = (Class) TargetExecutionStateful.class;

	String STATE_ATTRIBUTE_NAME = PREFIX_INVISIBLE + "state";

	/**
	 * The execution state of a debug target object
	 */
	public enum TargetExecutionState {
		/**
		 * The object has been created, but it not yet alive
		 * 
		 * This may apply, e.g., to a GDB "Inferior" which has no yet been used to launch or attach
		 * to a process.
		 */
		INACTIVE {
			@Override
			public boolean isAlive() {
				return false;
			}

			@Override
			public boolean isRunning() {
				return false;
			}

			@Override
			public boolean isStopped() {
				return false;
			}
		},

		/**
		 * The object is alive, but its execution state is unspecified
		 */
		ALIVE {
			@Override
			public boolean isAlive() {
				return true;
			}

			@Override
			public boolean isRunning() {
				return false;
			}

			@Override
			public boolean isStopped() {
				return false;
			}
		},

		/**
		 * The object is alive, but not executing
		 */
		STOPPED {
			@Override
			public boolean isAlive() {
				return true;
			}

			@Override
			public boolean isRunning() {
				return false;
			}

			@Override
			public boolean isStopped() {
				return true;
			}
		},

		/**
		 * The object is alive and executing
		 * 
		 * <p>
		 * "Running" is loosely defined. For example, with respect to a thread, it may indicate the
		 * thread is currently executing, waiting on an event, or scheduled for execution. It does
		 * not necessarily mean it is executing on a CPU at this exact moment.
		 */
		RUNNING {
			@Override
			public boolean isAlive() {
				return true;
			}

			@Override
			public boolean isRunning() {
				return true;
			}

			@Override
			public boolean isStopped() {
				return false;
			}
		},

		/**
		 * The object is no longer alive
		 * 
		 * <p>
		 * The object still exists but no longer represents something alive. This could be used for
		 * stale handles to objects which may still be queried (e.g., for a process exit code), or
		 * e.g., a GDB "Inferior" which could be re-used to launch or attach to another process.
		 */
		TERMINATED {
			@Override
			public boolean isAlive() {
				return false;
			}

			@Override
			public boolean isRunning() {
				return false;
			}

			@Override
			public boolean isStopped() {
				return false;
			}
		};

		/**
		 * Check if this state implies the object is alive
		 * 
		 * @return true if alive
		 */
		public abstract boolean isAlive();

		/**
		 * Check if this state implies the object is running
		 * 
		 * @return true if running
		 */
		public abstract boolean isRunning();

		/**
		 * Check if this state implies the object is stopped
		 * 
		 * @return true if stopped
		 */
		public abstract boolean isStopped();
	}

	/**
	 * Get the current execution state of this object
	 * 
	 * @return the state
	 */
	@TargetAttributeType(name = STATE_ATTRIBUTE_NAME, required = true, hidden = true)
	public default TargetExecutionState getExecutionState() {
		return getTypedAttributeNowByName(STATE_ATTRIBUTE_NAME, TargetExecutionState.class,
			TargetExecutionState.STOPPED);
	}

	public interface TargetExecutionStateListener extends TargetObjectListener {
		/**
		 * The object has entered a different execution state
		 * 
		 * @param object the object
		 * @param state the new state
		 */
		default void executionStateChanged(TargetExecutionStateful<?> object,
				TargetExecutionState state) {
		}
	}
}
