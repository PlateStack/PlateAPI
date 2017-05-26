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

package org.platestack.api.structure

import java.io.Serializable
import java.util.*

internal class ImmutableSingletonMap<K, V>(key: K, value: V):
        AbstractImmutableMap<K, V>(),
        Map<K, V> by Collections.singletonMap(key, value),
        Serializable { companion object { private const val serialVersionUID = 1L } }


/*
internal class ImmutableSingletonMap<K, V>(private val key: K, private val value: V): AbstractImmutableMap<K, V>() {
    private val entry = object : Map.Entry<K, V>{
        override val key get() = this@ImmutableSingletonMap.key
        override val value get() = this@ImmutableSingletonMap.value
    }

    override val entries: Set<Map.Entry<K, V>> = setOf(entry)
    override val keys = setOf(key)
    override val size = 1
    override val values = listOf(value)

    override fun containsKey(key: K) = key == this.key
    override fun containsValue(value: V) = value == this.value
    override fun get(key: K) = if(key in this) value else null
    override fun isEmpty() = false
}
*/