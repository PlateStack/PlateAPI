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
@file:JvmName("ImmutableCollections")

package org.platestack.api.structure

import java.util.*

private fun <E> treeSet(comparator: Comparator<in E>?, vararg elements: E) = TreeSet(comparator).apply { addAll(elements) }
private fun <E> treeSet(comparator: Comparator<in E>?, elements: Iterable<E>) = TreeSet(comparator).apply { addAll(elements) }

/////////////////////////////////////////////////////////////

/**
 * Returns a shared empty list. The returned list is serializable.
 */
fun <E> immutableListOf(): ImmutableList<E> = ImmutableEmptyList

/**
 * Returns a singleton list. The returned list is serializable.
 */
fun <E> immutableListOf(element: E): ImmutableList<E> = ImmutableSingletonList(element)

/**
 * Returns a list of given elements. The returned list is serializable.
 */
fun <E> immutableListOf(vararg elements: E): ImmutableList<E> = ImmutableMirrorList(listOf(*elements))

/////////////////////////////////////////////////////////////

/**
 * Returns a shared empty set. The returned set is serializable.
 */
fun <E> immutableSetOf(): ImmutableSet<E> = ImmutableEmptySet

/**
 * Returns a singleton set. The returned set is serializable.
 */
fun <E> immutableSetOf(element: E): ImmutableSet<E> = ImmutableSingletonSet(element)

/**
 * Returns a set of given elements. Elements of the set are iterated in the order they were specified.
 * The returned set is serializable.
 */
fun <E> immutableSetOf(vararg elements: E): ImmutableSet<E> = ImmutableMirrorSet(setOf(*elements))

/////////////////////////////////////////////////////////////

/**
 * Returns a shared empty set. The returned set is serializable.
 */
fun <E> immutableHashSetOf(): ImmutableSet<E> = immutableSetOf()

/**
 * Returns a singleton set. The returned set is serializable.
 */
fun <E> immutableHashSetOf(element: E): ImmutableSet<E> = immutableSetOf(element)

/**
 * Returns an immutable hash set of given elements. The returned set is serializable.
 */
fun <E> immutableHashSetOf(vararg elements: E): ImmutableSet<E> = ImmutableMirrorSet(hashSetOf(*elements))

/////////////////////////////////////////////////////////////

/**
 * Returns an immutable sorted set of given elements. All elements inserted into a sorted set must implement the Comparable interface.
 *
 * The returned set is serializable.
 */
fun <E> immutableSortedSetOf(vararg elements: E): ImmutableSortedSet<E> = ImmutableMirrorSortedSet(sortedSetOf(*elements))

/**
 * Returns an immutable sorted set of given elements. All elements are will be sorted by the given comparator.
 *
 * The returned set is serializable.
 */
fun <E> immutableSortedSetOf(comparator: Comparator<in E>?, vararg elements: E): ImmutableSortedSet<E> = ImmutableMirrorSortedSet(treeSet(comparator, *elements))

/////////////////////////////////////////////////////////////

/**
 * Returns an immutable navigable set of given elements. All elements inserted into a sorted set must implement the Comparable interface.
 *
 * The returned set is serializable.
 */
fun <E> immutableNavigableSetOf(vararg elements: E): ImmutableNavigableSet<E> = ImmutableMirrorNavigableSet(sortedSetOf(*elements))

/**
 * Returns an immutable navigable set of given elements. All elements are will be sorted by the given comparator.
 *
 * The returned set is serializable.
 */
fun <E> immutableNavigableSetOf(comparator: Comparator<in E>?, vararg elements: E): ImmutableNavigableSet<E> = ImmutableMirrorNavigableSet(treeSet(comparator, *elements))


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/////////////////////////////////////////////////////////////
// List
/////////////////////////////////////////////////////////////

/**
 * Returns a [immutable list][ImmutableList] containing all the elements. The returned list is serializable.
 */
fun <E> Collection<E>.toImmutableList(): ImmutableList<E> =
        if(this is ImmutableList<E>)
            this
        else when(size) {
            0 -> immutableListOf()
            1 -> immutableListOf(first())
            else -> ImmutableMirrorList(toList())
        }

/**
 * Returns a list containing all the elements. The returned list is serializable.
 */
fun <E> Iterable<E>.toImmutableList(): ImmutableList<E> = when(this) {
    is Collection<E> -> this.toImmutableList()
    else -> ImmutableMirrorList(toList())
}

/**
 * Returns a list containing all the elements. The returned list is serializable.
 *
 * This operation is *terminal*
 */
fun <E> Sequence<E>.toImmutableList() = toList().toImmutableList()

/**
 * Returns a list containing all the elements. The returned list is serializable.
 */
fun <E> Array<E>.toImmutableList() = toList().toImmutableList()

/////////////////////////////////////////////////////////////
// Set
/////////////////////////////////////////////////////////////

/**
 * Returns a Set of all elements. The returned set is serializable.
 *
 * The returned set preserves the element iteration order of the original collection.
 */
fun <E> Collection<E>.toImmutableSet(): ImmutableSet<E> =
        if(this is ImmutableSet<E>)
            this
        else when(size) {
            0 -> immutableSetOf()
            1 -> immutableSetOf(first())
            else -> ImmutableMirrorSet(toSet())
        }

/**
 * Returns a Set of all elements. The returned set is serializable.
 *
 * The returned set preserves the element iteration order of the original collection.
 */
fun <E> Iterable<E>.toImmutableSet(): ImmutableSet<E> = when(this) {
    is Collection<E> -> this.toImmutableSet()
    else -> ImmutableMirrorSet(toSet())
}

/**
 * Returns a Set of all elements. The returned set is serializable.
 *
 * The returned set preserves the element iteration order of the original collection.
 *
 * This operation is *terminal*
 */
fun <E> Sequence<E>.toImmutableSet(): ImmutableSet<E> = toSet().toImmutableSet()

/**
 * Returns a Set of all elements. The returned set is serializable.
 *
 * The returned set preserves the element iteration order of the original collection.
 */
fun <E> Array<E>.toImmutableSet(): ImmutableSet<E> = toSet().toImmutableSet()

/////////////////////////////////////////////////////////////
// HashSet
/////////////////////////////////////////////////////////////

/**
 * Returns an immutable hash set of all elements. The returned set is serializable.
 */
fun <E> Collection<E>.toImmutableHashSet(): ImmutableSet<E> =
        when(size) {
            0 -> immutableHashSetOf()
            1 -> immutableHashSetOf(first())
            else -> ImmutableMirrorSet(toHashSet())
        }

/**
 * Returns an immutable hash set of all elements. The returned set is serializable.
 */
fun <E> Iterable<E>.toImmutableHashSet(): ImmutableSet<E> = when(this) {
    is Collection<E> -> this.toImmutableHashSet()
    else -> ImmutableMirrorSet(toHashSet())
}

/**
 * Returns an immutable hash set of all elements. The returned set is serializable.
 *
 * This operation is *terminal*
 */
fun <E> Sequence<E>.toImmutableHashSet(): ImmutableSet<E> = toHashSet().toImmutableSet()

/**
 * Returns an immutable hash set of all elements. The returned set is serializable.
 */
fun <E> Array<E>.toImmutableHashSet(): ImmutableSet<E> = toHashSet().toImmutableSet()

/////////////////////////////////////////////////////////////
// SortedSet
/////////////////////////////////////////////////////////////

/**
 * Returns an immutable sorted set of all elements. The returned set is serializable.
 *
 * The elements will be sorted by the given comparator.
 */
fun <E> Iterable<E>.toImmutableSortedSet(comparator: Comparator<in E>? = null): ImmutableSortedSet<E>
        = ImmutableMirrorSortedSet(toCollection(TreeSet(comparator)))

/**
 * Returns an immutable sorted set of all elements. The returned set is serializable.
 *
 * The elements will be sorted by the given comparator.
 */
fun <E> SortedSet<E>.toImmutableSortedSet(): ImmutableSortedSet<E>
        = toImmutableSortedSet(comparator())

/**
 * Returns an immutable sorted set of all elements. The returned set is serializable.
 *
 * The elements will be sorted by the given comparator.
 *
 * This operation is *terminal*
 */
fun <E> Sequence<E>.toImmutableSortedSet(comparator: Comparator<in E>? = null): ImmutableSortedSet<E>
        = toCollection(TreeSet(comparator)).toImmutableSortedSet()

/**
 * Returns an immutable sorted set of all elements. The returned set is serializable.
 *
 * The elements will be sorted by the given comparator.
 */
fun <E> Array<E>.toImmutableSortedSet(comparator: Comparator<in E>? = null): ImmutableSortedSet<E>
        = toCollection(TreeSet(comparator)).toImmutableSortedSet()

/////////////////////////////////////////////////////////////
// NavigableSet
/////////////////////////////////////////////////////////////

/**
 * Returns an immutable navigable set of all elements. The returned set is serializable.
 *
 * The elements will be sorted by the given comparator.
 */
fun <E> Iterable<E>.toImmutableNavigableSet(comparator: Comparator<in E>? = null): ImmutableNavigableSet<E>
        = ImmutableMirrorNavigableSet(treeSet(comparator, this))

/**
 * Returns an immutable navigable set of all elements. The returned set is serializable.
 *
 * The elements will be sorted by the given comparator.
 */
fun <E> SortedSet<E>.toImmutableNavigableSet(): ImmutableNavigableSet<E> = toImmutableNavigableSet(comparator())

/**
 * Returns an immutable navigable set of all elements. The returned set is serializable.
 *
 * The elements will be sorted by the given comparator.
 */
fun <E> ImmutableSortedSet<E>.toImmutableNavigableSet(): ImmutableNavigableSet<E> = when(this) {
    is ImmutableNavigableSet<E> -> this
    else -> ImmutableMirrorNavigableSet(TreeSet(comparator()).also { it.addAll(this) })
}

/**
 * Returns an immutable navigable set of all elements. The returned set is serializable.
 *
 * The elements will be sorted by the given comparator.
 *
 * This operation is *terminal*
 */
fun <E> Sequence<E>.toImmutableNavigableSet(comparator: Comparator<in E>? = null): ImmutableNavigableSet<E>
        = toCollection(TreeSet(comparator)).toImmutableNavigableSet()

/**
 * Returns an immutable navigable set of all elements. The returned set is serializable.
 *
 * The elements will be sorted by the given comparator.
 */
fun <E> Array<E>.toImmutableNavigableSet(comparator: Comparator<in E>? = null): ImmutableNavigableSet<E>
        = toCollection(TreeSet(comparator)).toImmutableNavigableSet()
