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
package agent.gdb.model.impl;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import agent.gdb.manager.GdbInferior;
import ghidra.async.AsyncUtils;
import ghidra.dbg.util.ShellUtils;

public enum GdbModelImplUtils {
	;
	public static CompletableFuture<Void> launch(GdbModelImpl impl, GdbInferior inferior,
			List<String> args) {
		return inferior.fileExecAndSymbols(args.get(0)).thenCompose(__ -> {
			return inferior.setVar("args", ShellUtils.generateLine(args.subList(1, args.size())));
		}).thenCompose(__ -> {
			if (impl.noStarti) {
				return inferior.console("start");
			}
			return inferior.console("starti").thenApply(___ -> true).exceptionally(e -> {
				impl.noStarti = true;
				return false;
			}).thenCompose(success -> {
				if (success) {
					return AsyncUtils.NIL;
				}
				return inferior.console("start");
			});
		});
	}

	public static <V> V noDupMerge(V first, V second) {
		throw new AssertionError();
	}
}
