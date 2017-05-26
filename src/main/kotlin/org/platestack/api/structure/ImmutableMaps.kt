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
@file:JvmName("ImmutableMaps")

package org.platestack.api.structure

import java.util.*

private fun <K,V> treeMapOf(comparator: Comparator<in K>?, vararg pairs: Pair<K,V>) = TreeMap<K,V>(comparator).apply { putAll(pairs) }
private fun <K,V> treeMapOf(comparator: Comparator<in K>?, pairs: Iterable<Pair<K,V>>) = TreeMap<K,V>(comparator).apply { putAll(pairs) }
private fun <K,V> treeMapOf(comparator: Comparator<in K>?, pairs: Map<K,V>) = TreeMap<K,V>(comparator).apply { putAll(pairs) }

/////////////////////////////////////////////////////////////

/**
 * Returns an empty shared map. The returned map is serializable.
 */
fun <K,V> immutableMapOf() = @Suppress("UNCHECKED_CAST") (ImmutableEmptyMap as ImmutableMap<K, V>)

/**
 * Returns a singleton map. The returned map is serializable.
 */
fun <K,V> immutableMapOf(pair: Pair<K,V>): ImmutableMap<K,V> = ImmutableSingletonMap(pair.first, pair.second)

/**
 * Returns a map with the specified contents, given as a list of pairs where the first value is the key and the second is the value. If multiple pairs have the same key, the resulting map will contain the value from the last of those pairs.
 *
 * Entries of the map are iterated in the order they were specified. The returned map is serializable
 */
fun <K,V> immutableMapOf(vararg pairs: Pair<K,V>): ImmutableMap<K,V> = ImmutableMirrorMap(mapOf(*pairs))

/////////////////////////////////////////////////////////////

/**
 * Returns an empty shared map. The returned map is serializable.
 */
fun <K,V> immutableHashMapOf() = immutableMapOf<K,V>()

/**
 * Returns a singleton map. The returned map is serializable.
 */
fun <K,V> immutableHashMapOf(pair: Pair<K,V>) = immutableMapOf(pair)

/**
 * Returns a hash map with the specified contents, given as a list of pairs where the first component is the key and the second is the value. The returned map is serializable.
 */
fun <K,V> immutableHashMapOf(vararg pairs: Pair<K,V>): ImmutableMap<K,V> = ImmutableMirrorMap(hashMapOf(*pairs))

/////////////////////////////////////////////////////////////

/**
 * Returns a sorted map with the specified contents, given as a list of pairs where the first value is the key and the second is the value.
 *
 * The returned map is serializable
 */
fun <K: Comparable<K>,V> immutableSortedMapOf(vararg pairs: Pair<K,V>):ImmutableSortedMap<K,V> = ImmutableMirrorSortedMap(sortedMapOf(*pairs))

/**
 * Returns a sorted map with the specified contents, given as a list of pairs where the first value is the key and the second is the value.
 *
 * The elements are sorted by the given comparator
 *
 * The returned map is serializable
 */
fun <K,V> immutableSortedMapOf(comparator: Comparator<in K>?, vararg pairs: Pair<K,V>):ImmutableSortedMap<K,V> = ImmutableMirrorSortedMap(treeMapOf(comparator, *pairs))

/////////////////////////////////////////////////////////////

/**
 * Returns a navigable map with the specified contents, given as a list of pairs where the first value is the key and the second is the value.
 *
 * The returned map is serializable
 */
fun <K: Comparable<K>, V> immutableNavigableMapOf(vararg pairs: Pair<K,V>): ImmutableNavigableMap<K,V> = ImmutableMirrorNavigableMap<K,V>(treeMapOf(null, *pairs))

/**
 * Returns a navigable map with the specified contents, given as a list of pairs where the first value is the key and the second is the value.
 *
 * The elements are sorted by the given comparator
 *
 * The returned map is serializable
 */
fun <K: Comparable<K>, V> immutableNavigableMapOf(comparator: Comparator<in K>?, vararg pairs: Pair<K,V>): ImmutableNavigableMap<K,V> = ImmutableMirrorNavigableMap<K,V>(treeMapOf(comparator, *pairs))

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/////////////////////////////////////////////////////////////
// Map
/////////////////////////////////////////////////////////////

/**
 * Returns an immutable map containing all key-value pairs from the original map.
 *
 * The returned map preserves the entry iteration order of the original map.
 *
 * The returned map is serializable
 */
fun <K,V> Map<K,V>.toImmutableMap(): ImmutableMap<K, V> =
        if(this is ImmutableMap<K, V>)
            this
        else when(size) {
            0 -> immutableMapOf()
            1 -> immutableMapOf(iterator().next().toPair())
            else -> ImmutableMirrorMap(toMap())
        }

/**
 * Returns an immutable map containing all key-value pairs from the original source.
 *
 * The returned map preserves the entry iteration order.
 *
 * The returned map is serializable
 */
fun <K,V> Iterable<Pair<K,V>>.toImmutableMap() = toMap().toImmutableMap()

/**
 * Returns an immutable map containing all key-value pairs from the original source.
 *
 * The returned map preserves the entry iteration order.
 *
 * The returned map is serializable
 */
fun <K,V> Array<Pair<K,V>>.toImmutableMap() = toMap().toImmutableMap()

/**
 * Returns an immutable map containing all key-value pairs from the original sequence.
 *
 * The returned map preserves the entry sequence order.
 *
 * The returned map is serializable
 *
 * This operation is *terminal*
 */
fun <K,V> Sequence<Pair<K,V>>.toImmutableMap() = toMap().toImmutableMap()

/////////////////////////////////////////////////////////////
// HashMap
/////////////////////////////////////////////////////////////

/**
 * Returns an immutable hash map containing all key-value pairs from the original map.
 *
 * The returned map is serializable
 */
fun <K,V> Map<K,V>.toImmutableHashMap(): ImmutableMap<K, V> =
        when(size) {
            0 -> immutableHashMapOf()
            1 -> immutableHashMapOf(iterator().next().toPair())
            else -> ImmutableMirrorMap(toMap(hashMapOf()))
        }

/**
 * Returns an immutable hash map containing all key-value pairs from the original source.
 *
 * The returned map is serializable
 */
fun <K,V> Iterable<Pair<K,V>>.toImmutableHashMap() = toMap().toImmutableMap()

/**
 * Returns an immutable hash map containing all key-value pairs from the original source.
 *
 * The returned map is serializable
 */
fun <K,V> Array<Pair<K,V>>.toImmutableHashMap() = toMap().toImmutableMap()

/**
 * Returns an immutable hash map containing all key-value pairs from the original sequence.
 *
 * The returned map is serializable
 */
fun <K,V> Sequence<Pair<K,V>>.toImmutableHashMap() = toMap().toImmutableMap()

/////////////////////////////////////////////////////////////
// SortedMap
/////////////////////////////////////////////////////////////

/**
 * Returns an immutable sorted map containing all key-value pairs from the original map.
 *
 * The returned map will have the same comparator as the original map.
 *
 * The returned map is serializable
 */
fun <K,V> SortedMap<K,V>.toImmutableSortedMap(): ImmutableSortedMap<K,V>
        = ImmutableMirrorSortedMap(TreeMap(this))

/**
 * Returns an immutable sorted map containing all key-value pairs from the original map.
 *
 * The returned map will be sorted by the given comparator.
 * The natural ordering will be use if the given comparator is null.
 *
 * The returned map is serializable
 */
fun <K,V> Map<K,V>.toImmutableSortedMap(comparator: Comparator<in K>?): ImmutableSortedMap<K,V>
        = treeMapOf(comparator, this).toImmutableSortedMap()

/**
 * Returns an immutable sorted map containing all key-value pairs from the original source.
 *
 * The returned map will be sorted by the given comparator.
 * The natural ordering will be use if the given comparator is null.
 *
 * The returned map is serializable
 */
fun <K,V> Iterable<Pair<K,V>>.toImmutableSortedMap(comparator: Comparator<in K>? = null) = treeMapOf(comparator, this).toImmutableMap()

/**
 * Returns an immutable sorted map containing all key-value pairs from the original source.
 *
 * The returned map will be sorted by the given comparator.
 * The natural ordering will be use if the given comparator is null.
 *
 * The returned map is serializable
 */
fun <K,V> Array<Pair<K,V>>.toImmutableSortedMap(comparator: Comparator<in K>? = null) = treeMapOf(comparator, *this).toImmutableMap()

/**
 * Returns an immutable sorted map containing all key-value pairs from the original source.
 *
 * The returned map will be sorted by the given comparator.
 * The natural ordering will be use if the given comparator is null.
 *
 * The returned map is serializable
 */
fun <K,V> Sequence<Pair<K,V>>.toImmutableSortedMap(comparator: Comparator<in K>? = null) = treeMapOf(comparator, asIterable()).toImmutableMap()

/////////////////////////////////////////////////////////////
// NavigableMap
/////////////////////////////////////////////////////////////

/**
 * Returns an immutable sorted map containing all key-value pairs from the original map.
 *
 * The returned map will have the same comparator as the original map.
 *
 * The returned map is serializable
 */
fun <K,V> NavigableMap<K,V>.toImmutableNavigableMap(): ImmutableNavigableMap<K,V>
        = ImmutableMirrorNavigableMap(TreeMap(this))

/**
 * Returns an immutable sorted map containing all key-value pairs from the original map.
 *
 * The returned map will have the same comparator as the original map.
 *
 * The returned map is serializable
 */
fun <K,V> SortedMap<K,V>.toImmutableNavigableMap(): ImmutableNavigableMap<K,V>
        = ImmutableMirrorNavigableMap(TreeMap(this))

/**
 * Returns an immutable sorted map containing all key-value pairs from the original source.
 *
 * The returned map will be sorted by the given comparator.
 * The natural ordering will be use if the given comparator is null.
 *
 * The returned map is serializable
 */
fun <K,V> Iterable<Pair<K,V>>.toImmutableNavigableMap(comparator: Comparator<in K>? = null) = treeMapOf(comparator, this).toImmutableNavigableMap()

/**
 * Returns an immutable sorted map containing all key-value pairs from the original source.
 *
 * The returned map will be sorted by the given comparator.
 * The natural ordering will be use if the given comparator is null.
 *
 * The returned map is serializable
 */
fun <K,V> Array<Pair<K,V>>.toImmutableNavigableMap(comparator: Comparator<in K>? = null) = treeMapOf(comparator, *this).toImmutableNavigableMap()

/**
 * Returns an immutable sorted map containing all key-value pairs from the original source.
 *
 * The returned map will be sorted by the given comparator.
 * The natural ordering will be use if the given comparator is null.
 *
 * The returned map is serializable
 */
fun <K,V> Sequence<Pair<K,V>>.toImmutableNavigableMap(comparator: Comparator<in K>? = null) = treeMapOf(comparator, asIterable()).toImmutableNavigableMap()
