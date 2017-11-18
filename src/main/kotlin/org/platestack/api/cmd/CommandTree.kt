/*
 *  Copyright (C) 2017 José Roberto de Araújo Júnior
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.platestack.api.cmd

import org.platestack.api.message.Message
import org.platestack.structure.immutable.ImmutableList
import org.platestack.structure.immutable.toImmutableList
import java.util.*

internal data class CommandLookup(val path: ImmutableList<String>, val args: ImmutableList<String>, val result: Any)

internal class CommandGroup {
    val description: Message? = null
    private val registry = mutableMapOf<String, Any>()

    private operator fun set(key: String, value: Any) {
        if(key in registry)
            error("The command key $key is already defined")
        registry[key.toLowerCase()] = value
    }

    internal operator fun set(key: String, group: CommandGroup) {
        set(key, group as Any)
    }

    internal operator fun set(key: String, command: CommandDefinition) {
        set(key, command as Any)
    }

    internal operator fun get(path: List<String>, root: List<String> = emptyList()) = lookup(root.toMutableList(), ArrayDeque(path))

    private tailrec fun lookup(path: MutableList<String>, args: Queue<String>) : CommandLookup? {
        val key = args.peek() ?: return null
        val value = registry[key.toLowerCase()] ?: return null
        when(value) {
            is CommandGroup -> {
                path += args.remove()
                return lookup(path, args)
            }
            else -> return CommandLookup(path.toImmutableList(), args.toImmutableList(), value)
        }
    }
}

internal class CommandTree {
    private val root = CommandGroup()
}