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

import java.util.*

internal class ImmutableMirrorNavigableMap<K,V>(backend: NavigableMap<K,V>)
    : AbstractImmutableNavigableMap<K,V>(), NavigableMap<K,V> by Collections.unmodifiableNavigableMap(backend) {
    companion object {
        private const val serialVersionUID = 1L
    }

    override val descending by lazy { ImmutableMirrorNavigableMap(descendingMap()) }
    override val navigableKeys by lazy { ImmutableMirrorNavigableSet(navigableKeySet()) }
    override val descendingKeys by lazy { ImmutableMirrorNavigableSet(descendingKeySet()) }

    override fun sub(fromKey: K, fromInclusive: Boolean, toKey: K, toInclusive: Boolean): ImmutableNavigableMap<K, V> {
        return ImmutableMirrorNavigableMap(subMap(fromKey, fromInclusive, toKey, toInclusive))
    }

    override fun head(toKey: K, inclusive: Boolean): ImmutableNavigableMap<K, V> {
        return ImmutableMirrorNavigableMap(headMap(toKey, inclusive))
    }

    override fun tail(fromKey: K, inclusive: Boolean): ImmutableNavigableMap<K, V> {
        return ImmutableMirrorNavigableMap(tailMap(fromKey, inclusive))
    }

    override fun sub(fromKey: K, toKey: K): ImmutableSortedMap<K, V> {
        return ImmutableMirrorSortedMap(subMap(fromKey, toKey))
    }

    override fun head(toKey: K): ImmutableSortedMap<K, V> {
        return ImmutableMirrorSortedMap(headMap(toKey))
    }

    override fun tail(fromKey: K): ImmutableSortedMap<K, V> {
        return ImmutableMirrorSortedMap(tailMap(fromKey))
    }
}
