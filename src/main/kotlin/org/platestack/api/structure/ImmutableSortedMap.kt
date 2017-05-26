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
import kotlin.NoSuchElementException

interface ImmutableSortedMap<K, V>: ImmutableMap<K, V> {
    /**
     * Returns the comparator used to order the keys in this map, or
     * `null` if this map uses the [ natural ordering][Comparable] of its keys.
     *
     * @return the comparator used to order the keys in this map,
     *         or `null` if this map uses the natural ordering
     *         of its keys
     */
    fun comparator(): Comparator<in K>?

    /**
     * Returns a view of the portion of this map whose keys range from
     * `fromKey`, inclusive, to `toKey`, exclusive.  (If
     * `fromKey` and `toKey` are equal, the returned map
     * is empty.)  The returned map is backed by this map, so changes
     * in the returned map are reflected in this map, and vice-versa.
     * The returned map supports all optional map operations that this
     * map supports.
     *
     *
     * The returned map will throw an `IllegalArgumentException`
     * on an attempt to insert a key outside its range.
     *
     * @param fromKey low endpoint (inclusive) of the keys in the returned map
     *
     * @param toKey high endpoint (exclusive) of the keys in the returned map
     *
     * @return a view of the portion of this map whose keys range from
     *         `fromKey`, inclusive, to `toKey`, exclusive
     *
     * @throws ClassCastException if `fromKey` and `toKey`
     *         cannot be compared to one another using this map's comparator
     *         (or, if the map has no comparator, using natural ordering).
     *         Implementations may, but are not required to, throw this
     *         exception if `fromKey` or `toKey`
     *         cannot be compared to keys currently in the map.
     *
     * @throws NullPointerException if `fromKey` or `toKey`
     *         is null and this map does not permit null keys
     *
     * @throws IllegalArgumentException if `fromKey` is greater than
     *         `toKey`; or if this map itself has a restricted
     *         range, and `fromKey` or `toKey` lies
     *         outside the bounds of the range
     */
    @Throws(ClassCastException::class, NullPointerException::class, IllegalArgumentException::class)
    fun sub(fromKey: K, toKey: K): ImmutableSortedMap<K, V>

    /**
     * Returns a view of the portion of this map whose keys are
     * strictly less than `toKey`.  The returned map is backed
     * by this map, so changes in the returned map are reflected in
     * this map, and vice-versa.  The returned map supports all
     * optional map operations that this map supports.
     *
     *
     * The returned map will throw an `IllegalArgumentException`
     * on an attempt to insert a key outside its range.
     *
     * @param toKey high endpoint (exclusive) of the keys in the returned map
     *
     * @return a view of the portion of this map whose keys are strictly
     *         less than `toKey`
     *
     * @throws ClassCastException if `toKey` is not compatible
     *         with this map's comparator (or, if the map has no comparator,
     *         if `toKey` does not implement [Comparable]).
     *         Implementations may, but are not required to, throw this
     *         exception if `toKey` cannot be compared to keys
     *         currently in the map.
     *
     * @throws NullPointerException if `toKey` is null and
     *         this map does not permit null keys
     *
     * @throws IllegalArgumentException if this map itself has a
     *         restricted range, and `toKey` lies outside the
     *         bounds of the range
     */
    @Throws(ClassCastException::class, NullPointerException::class, IllegalArgumentException::class)
    fun head(toKey: K): ImmutableSortedMap<K, V>

    /**
     * Returns a view of the portion of this map whose keys are
     * greater than or equal to `fromKey`.  The returned map is
     * backed by this map, so changes in the returned map are
     * reflected in this map, and vice-versa.  The returned map
     * supports all optional map operations that this map supports.
     *
     *
     * The returned map will throw an `IllegalArgumentException`
     * on an attempt to insert a key outside its range.
     *
     * @param fromKey low endpoint (inclusive) of the keys in the returned map
     *
     * @return a view of the portion of this map whose keys are greater
     *         than or equal to `fromKey`
     *
     * @throws ClassCastException if `fromKey` is not compatible
     *         with this map's comparator (or, if the map has no comparator,
     *         if `fromKey` does not implement [Comparable]).
     *         Implementations may, but are not required to, throw this
     *         exception if `fromKey` cannot be compared to keys
     *         currently in the map.
     *
     * @throws NullPointerException if `fromKey` is null and
     *         this map does not permit null keys
     *
     * @throws IllegalArgumentException if this map itself has a
     *         restricted range, and `fromKey` lies outside the
     *          bounds of the range
     */
    @Throws(ClassCastException::class, NullPointerException::class, IllegalArgumentException::class)
    fun tail(fromKey: K): ImmutableSortedMap<K, V>

    /**
     * Returns the first (lowest) key currently in this map.
     *
     * @return the first (lowest) key currently in this map
     *
     * @throws NoSuchElementException if this map is empty
     */
    @Throws(NoSuchElementException::class)
    fun firstKey(): K

    /**
     * Returns the last (highest) key currently in this map.
     *
     * @return the last (highest) key currently in this map
     *
     * @throws NoSuchElementException if this map is empty
     */
    @Throws(NoSuchElementException::class)
    fun lastKey(): K

    /**
     * Returns a [Set] view of the keys contained in this map.
     * The set's iterator returns the keys in ascending order.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own `remove` operation), the results of
     * the iteration are undefined.  The set supports element removal,
     * which removes the corresponding mapping from the map, via the
     * `Iterator.remove`, `Set.remove`,
     * `removeAll`, `retainAll`, and `clear`
     * operations.  It does not support the `add` or `addAll`
     * operations.
     *
     * @return a set view of the keys contained in this map, sorted in
     *         ascending order
     */
    override val keys: Set<K>

    /**
     * Returns a [Collection] view of the values contained in this map.
     * The collection's iterator returns the values in ascending order
     * of the corresponding keys.
     * The collection is backed by the map, so changes to the map are
     * reflected in the collection, and vice-versa.  If the map is
     * modified while an iteration over the collection is in progress
     * (except through the iterator's own `remove` operation),
     * the results of the iteration are undefined.  The collection
     * supports element removal, which removes the corresponding
     * mapping from the map, via the `Iterator.remove`,
     * `Collection.remove`, `removeAll`,
     * `retainAll` and `clear` operations.  It does not
     * support the `add` or `addAll` operations.
     *
     * @return a collection view of the values contained in this map,
     *         sorted in ascending key order
     */
    override val values: Collection<V>

    /**
     * Returns a [Set] view of the mappings contained in this map.
     * The set's iterator returns the entries in ascending key order.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own `remove` operation, or through the
     * `setValue` operation on a map entry returned by the
     * iterator) the results of the iteration are undefined.  The set
     * supports element removal, which removes the corresponding
     * mapping from the map, via the `Iterator.remove`,
     * `Set.remove`, `removeAll`, `retainAll` and
     * `clear` operations.  It does not support the
     * `add` or `addAll` operations.
     *
     * @return a set view of the mappings contained in this map,
     *         sorted in ascending key order
     */
    override val entries: Set<Map.Entry<K, V>>
}
