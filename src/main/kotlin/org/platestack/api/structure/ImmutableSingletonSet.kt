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

internal class ImmutableSingletonSet<E>(element: E):
        AbstractImmutableSet<E>(),
        Set<E> by Collections.singleton(element),
        Serializable { companion object { private const val serialVersionUID = 1L } }


/*
internal class ImmutableSingletonSet<E>(val element: E): AbstractImmutableSet<E>() {
    override val size = 1
    override fun contains(element: E) = element == this.element
    override fun containsAll(elements: Collection<E>) = elements.all { it in this }
    override fun isEmpty() = false
    override fun iterator() = SingleIterator(element)
}
*/
