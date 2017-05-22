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

@file:JvmName("RemappedMapUtil")

package org.platestack.api.structure

@JvmSynthetic fun <K, V, P> Map<K, P>.remap(function: (Map.Entry<K, P>) -> V) = RemappedMap(this, function)

class RemappedMap<K, out V, out P>(private val parentMap: Map<K, P>, private val function: (Map.Entry<K, P>) -> V) : AbstractMap<K, V>() {
    override val entries: Set<Map.Entry<K, V>> = object : AbstractSet<Map.Entry<K, V>>() {
        private val parentEntries = parentMap.entries

        override val size = parentEntries.size
        override fun iterator(): Iterator<Map.Entry<K, V>> {
            return object : Iterator<Map.Entry<K, V>> {
                private val parentIterator = parentEntries.iterator()

                override fun hasNext() = parentIterator.hasNext()
                override fun next(): Map.Entry<K, V> {
                    val parentNext = parentIterator.next()
                    return java.util.AbstractMap.SimpleImmutableEntry(parentNext.key, function(parentNext))
                }
            }
        }
    }
}