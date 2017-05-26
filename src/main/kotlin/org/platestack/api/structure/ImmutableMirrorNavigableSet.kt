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

internal class ImmutableMirrorNavigableSet<E>(backend: NavigableSet<E>)
    : AbstractImmutableNavigableSet<E>(), NavigableSet<E> by Collections.unmodifiableNavigableSet(backend) {
    companion object {
        private const val serialVersionUID = 1L
    }

    override fun sub(fromElement: E, fromInclusive: Boolean, toElement: E, toInclusive: Boolean): ImmutableNavigableSet<E> {
        return ImmutableMirrorNavigableSet(subSet(fromElement, fromInclusive, toElement, toInclusive))
    }

    override fun head(toElement: E, inclusive: Boolean): ImmutableNavigableSet<E> {
        return ImmutableMirrorNavigableSet(headSet(toElement, inclusive))
    }

    override fun tail(fromElement: E, inclusive: Boolean): ImmutableNavigableSet<E> {
        return ImmutableMirrorNavigableSet(tailSet(fromElement, inclusive))
    }

    override fun sub(fromElement: E, toElement: E): ImmutableSortedSet<E> {
        return ImmutableMirrorSortedSet(subSet(fromElement, toElement))
    }

    override fun head(toElement: E): ImmutableSortedSet<E> {
        return ImmutableMirrorSortedSet(headSet(toElement))
    }

    override fun tail(fromElement: E): ImmutableSortedSet<E> {
        return ImmutableMirrorSortedSet(tailSet(fromElement))
    }

    override val descending by lazy { ImmutableMirrorNavigableSet<E>(descendingSet()) }
    override fun descendingIterator() = descending.iterator()
}
