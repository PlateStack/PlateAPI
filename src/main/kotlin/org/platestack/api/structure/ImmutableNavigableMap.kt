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
import kotlin.collections.Map.Entry

interface ImmutableNavigableMap<K, V>: ImmutableSortedMap<K, V> {
    /**
     * Returns a key-value mapping associated with the greatest key
     * strictly less than the given key, or `null` if there is
     * no such key.
     *
     * @param key the key
     * 
     * @return an entry with the greatest key less than `key`,
     *         or `null` if there is no such key
     * 
     * @throws ClassCastException if the specified key cannot be compared
     *         with the keys currently in the map
     * 
     * @throws NullPointerException if the specified key is null
     *         and this map does not permit null keys
     */
    @Throws(ClassCastException::class, NullPointerException::class)
    fun lowerEntry(key: K): Entry<K, V>?

    /**
     * Returns the greatest key strictly less than the given key, or
     * `null` if there is no such key.
     *
     * @param key the key
     * 
     * @return the greatest key less than `key`,
     *         or `null` if there is no such key
     * 
     * @throws ClassCastException if the specified key cannot be compared
     *         with the keys currently in the map
     * 
     * @throws NullPointerException if the specified key is null
     *         and this map does not permit null keys
     */
    @Throws(ClassCastException::class, NullPointerException::class)
    fun lowerKey(key: K): K?

    /**
     * Returns a key-value mapping associated with the greatest key
     * less than or equal to the given key, or `null` if there
     * is no such key.
     *
     * @param key the key
     * 
     * @return an entry with the greatest key less than or equal to
     *         `key`, or `null` if there is no such key
     * 
     * @throws ClassCastException if the specified key cannot be compared
     *         with the keys currently in the map
     * 
     * @throws NullPointerException if the specified key is null
     *         and this map does not permit null keys
     */
    @Throws(ClassCastException::class, NullPointerException::class)
    fun floorEntry(key: K): Entry<K, V>?

    /**
     * Returns the greatest key less than or equal to the given key,
     * or `null` if there is no such key.
     *
     * @param key the key
     * 
     * @return the greatest key less than or equal to `key`,
     *         or `null` if there is no such key
     * 
     * @throws ClassCastException if the specified key cannot be compared
     *         with the keys currently in the map
     * 
     * @throws NullPointerException if the specified key is null
     *         and this map does not permit null keys
     */
    @Throws(ClassCastException::class, NullPointerException::class)
    fun floorKey(key: K): K?

    /**
     * Returns a key-value mapping associated with the least key
     * greater than or equal to the given key, or `null` if
     * there is no such key.
     *
     * @param key the key
     * 
     * @return an entry with the least key greater than or equal to
     *         `key`, or `null` if there is no such key
     * 
     * @throws ClassCastException if the specified key cannot be compared
     *         with the keys currently in the map
     * 
     * @throws NullPointerException if the specified key is null
     *         and this map does not permit null keys
     */
    @Throws(ClassCastException::class, NullPointerException::class)
    fun ceilingEntry(key: K): Entry<K, V>?

    /**
     * Returns the least key greater than or equal to the given key,
     * or `null` if there is no such key.
     *
     * @param key the key
     * 
     * @return the least key greater than or equal to `key`,
     *         or `null` if there is no such key
     * 
     * @throws ClassCastException if the specified key cannot be compared
     *         with the keys currently in the map
     * 
     * @throws NullPointerException if the specified key is null
     *         and this map does not permit null keys
     */
    @Throws(ClassCastException::class, NullPointerException::class)
    fun ceilingKey(key: K): K?

    /**
     * Returns a key-value mapping associated with the least key
     * strictly greater than the given key, or `null` if there
     * is no such key.
     *
     * @param key the key
     * 
     * @return an entry with the least key greater than `key`,
     *         or `null` if there is no such key
     * 
     * @throws ClassCastException if the specified key cannot be compared
     *         with the keys currently in the map
     * 
     * @throws NullPointerException if the specified key is null
     *         and this map does not permit null keys
     */
    @Throws(ClassCastException::class, NullPointerException::class)
    fun higherEntry(key: K): Entry<K, V>?

    /**
     * Returns the least key strictly greater than the given key, or
     * `null` if there is no such key.
     *
     * @param key the key
     * 
     * @return the least key greater than `key`,
     *         or `null` if there is no such key
     * 
     * @throws ClassCastException if the specified key cannot be compared
     *         with the keys currently in the map
     * 
     * @throws NullPointerException if the specified key is null
     *         and this map does not permit null keys
     */
    @Throws(ClassCastException::class, NullPointerException::class)
    fun higherKey(key: K): K?

    /**
     * Returns a key-value mapping associated with the least
     * key in this map, or `null` if the map is empty.
     *
     * @return an entry with the least key,
     *         or `null` if this map is empty
     */
    fun firstEntry(): Entry<K, V>?

    /**
     * Returns a key-value mapping associated with the greatest
     * key in this map, or `null` if the map is empty.
     *
     * @return an entry with the greatest key,
     *         or `null` if this map is empty
     */
    fun lastEntry(): Entry<K, V>?

    /**
     * Removes and returns a key-value mapping associated with
     * the least key in this map, or `null` if the map is empty.
     *
     * @return the removed first entry of this map,
     *         or `null` if this map is empty
     */
    fun pollFirstEntry(): Entry<K, V>?

    /**
     * Removes and returns a key-value mapping associated with
     * the greatest key in this map, or `null` if the map is empty.
     *
     * @return the removed last entry of this map,
     *         or `null` if this map is empty
     */
    fun pollLastEntry(): Entry<K, V>?

    /**
     * Returns a reverse order view of the mappings contained in this map.
     * The descending map is backed by this map, so changes to the map are
     * reflected in the descending map, and vice-versa.  If either map is
     * modified while an iteration over a collection view of either map
     * is in progress (except through the iterator's own `remove`
     * operation), the results of the iteration are undefined.
     *
     *
     * The returned map has an ordering equivalent to
     * <tt>[Collections.reverseOrder](comparator())</tt>.
     * The expression `m.descendingMap().descendingMap()` returns a
     * view of `m` essentially equivalent to `m`.
     *
     * @return a reverse order view of this map
     */
    val descending: ImmutableNavigableMap<K, V>

    /**
     * Returns a [NavigableSet] view of the keys contained in this map.
     * The set's iterator returns the keys in ascending order.
     * The set is backed by the map, so changes to the map are reflected in
     * the set, and vice-versa.  If the map is modified while an iteration
     * over the set is in progress (except through the iterator's own `remove` operation), the results of the iteration are undefined.  The
     * set supports element removal, which removes the corresponding mapping
     * from the map, via the `Iterator.remove`, `Set.remove`,
     * `removeAll`, `retainAll`, and `clear` operations.
     * It does not support the `add` or `addAll` operations.
     *
     * @return a navigable set view of the keys in this map
     */
    val navigableKeys: ImmutableNavigableSet<K>

    /**
     * Returns a reverse order [NavigableSet] view of the keys contained in this map.
     * The set's iterator returns the keys in descending order.
     * The set is backed by the map, so changes to the map are reflected in
     * the set, and vice-versa.  If the map is modified while an iteration
     * over the set is in progress (except through the iterator's own `remove` operation), the results of the iteration are undefined.  The
     * set supports element removal, which removes the corresponding mapping
     * from the map, via the `Iterator.remove`, `Set.remove`,
     * `removeAll`, `retainAll`, and `clear` operations.
     * It does not support the `add` or `addAll` operations.
     *
     * @return a reverse order navigable set view of the keys in this map
     */
    val descendingKeys: ImmutableNavigableSet<K>

    /**
     * Returns a view of the portion of this map whose keys range from
     * `fromKey` to `toKey`.  If `fromKey` and
     * `toKey` are equal, the returned map is empty unless
     * `fromInclusive` and `toInclusive` are both true.  The
     * returned map is backed by this map, so changes in the returned map are
     * reflected in this map, and vice-versa.  The returned map supports all
     * optional map operations that this map supports.
     *
     *
     * The returned map will throw an `IllegalArgumentException`
     * on an attempt to insert a key outside of its range, or to construct a
     * submap either of whose endpoints lie outside its range.
     *
     * @param fromKey low endpoint of the keys in the returned map
     * 
     * @param fromInclusive `true` if the low endpoint
     *        is to be included in the returned view
     * 
     * @param toKey high endpoint of the keys in the returned map
     * 
     * @param toInclusive `true` if the high endpoint
     *        is to be included in the returned view
     * 
     * @return a view of the portion of this map whose keys range from
     *         `fromKey` to `toKey`
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
    fun sub(fromKey: K, fromInclusive: Boolean,
               toKey: K, toInclusive: Boolean): ImmutableNavigableMap<K, V>

    /**
     * Returns a view of the portion of this map whose keys are less than (or
     * equal to, if `inclusive` is true) `toKey`.  The returned
     * map is backed by this map, so changes in the returned map are reflected
     * in this map, and vice-versa.  The returned map supports all optional
     * map operations that this map supports.
     *
     *
     * The returned map will throw an `IllegalArgumentException`
     * on an attempt to insert a key outside its range.
     *
     * @param toKey high endpoint of the keys in the returned map
     * 
     * @param inclusive `true` if the high endpoint
     *        is to be included in the returned view
     * 
     * @return a view of the portion of this map whose keys are less than
     *         (or equal to, if `inclusive` is true) `toKey`
     * 
     * @throws ClassCastException if `toKey` is not compatible
     *         with this map's comparator (or, if the map has no comparator,
     *         if `toKey` does not implement [Comparable]).
     *         Implementations may, but are not required to, throw this
     *         exception if `toKey` cannot be compared to keys
     *         currently in the map.
     * 
     * @throws NullPointerException if `toKey` is null
     *         and this map does not permit null keys
     * 
     * @throws IllegalArgumentException if this map itself has a
     *         restricted range, and `toKey` lies outside the
     *         bounds of the range
     */
    @Throws(ClassCastException::class, NullPointerException::class, IllegalArgumentException::class)
    fun head(toKey: K, inclusive: Boolean): ImmutableNavigableMap<K, V>

    /**
     * Returns a view of the portion of this map whose keys are greater than (or
     * equal to, if `inclusive` is true) `fromKey`.  The returned
     * map is backed by this map, so changes in the returned map are reflected
     * in this map, and vice-versa.  The returned map supports all optional
     * map operations that this map supports.
     *
     *
     * The returned map will throw an `IllegalArgumentException`
     * on an attempt to insert a key outside its range.
     *
     * @param fromKey low endpoint of the keys in the returned map
     * 
     * @param inclusive `true` if the low endpoint
     *        is to be included in the returned view
     * 
     * @return a view of the portion of this map whose keys are greater than
     *         (or equal to, if `inclusive` is true) `fromKey`
     * 
     * @throws ClassCastException if `fromKey` is not compatible
     *         with this map's comparator (or, if the map has no comparator,
     *         if `fromKey` does not implement [Comparable]).
     *         Implementations may, but are not required to, throw this
     *         exception if `fromKey` cannot be compared to keys
     *         currently in the map.
     * 
     * @throws NullPointerException if `fromKey` is null
     *         and this map does not permit null keys
     * 
     * @throws IllegalArgumentException if this map itself has a
     *         restricted range, and `fromKey` lies outside the
     *         bounds of the range
     */
    @Throws(ClassCastException::class, NullPointerException::class, IllegalArgumentException::class)
    fun tail(fromKey: K, inclusive: Boolean): ImmutableNavigableMap<K, V>

    /**
     * See [ImmutableSortedMap.sub]
     *
     * Equivalent to `subMap(fromKey, true, toKey, false)`.
     */
    @Throws(ClassCastException::class, NullPointerException::class, IllegalArgumentException::class)
    override fun sub(fromKey: K, toKey: K): ImmutableSortedMap<K, V>

    /**
     * See [ImmutableSortedMap.head]
     *
     * Equivalent to `headMap(toKey, false)`.
     */
    @Throws(ClassCastException::class, NullPointerException::class, IllegalArgumentException::class)
    override fun head(toKey: K): ImmutableSortedMap<K, V>

    /**
     * See [ImmutableSortedMap.tail]
     *
     * Equivalent to `tailMap(fromKey, true)`.
     */
    @Throws(ClassCastException::class, NullPointerException::class, IllegalArgumentException::class)
    override fun tail(fromKey: K): ImmutableSortedMap<K, V>
}
