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

internal class ImmutableMirrorSortedSet<E>(backend: SortedSet<E>)
    : AbstractImmutableSortedSet<E>(), SortedSet<E> by Collections.unmodifiableSortedSet(backend) {
    companion object {
        private const val serialVersionUID = 1L
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
}
