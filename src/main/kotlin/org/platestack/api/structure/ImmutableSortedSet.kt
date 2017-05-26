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

interface ImmutableSortedSet<E>: ImmutableSet<E> {
    /**
     * Returns the comparator used to order the elements in this set,
     * or <tt>null</tt> if this set uses the [ natural ordering][Comparable] of its elements.
     *
     * @return the comparator used to order the elements in this set,
     *         or <tt>null</tt> if this set uses the natural ordering
     *         of its elements
     */
    fun comparator(): Comparator<in E>?

    /**
     * Returns a view of the portion of this set whose elements range
     * from <tt>fromElement</tt>, inclusive, to <tt>toElement</tt>,
     * exclusive.  (If <tt>fromElement</tt> and <tt>toElement</tt> are
     * equal, the returned set is empty.)  The returned set is backed
     * by this set, so changes in the returned set are reflected in
     * this set, and vice-versa.  The returned set supports all
     * optional set operations that this set supports.
     *
     *
     * The returned set will throw an <tt>IllegalArgumentException</tt>
     * on an attempt to insert an element outside its range.
     *
     * @param fromElement low endpoint (inclusive) of the returned set
     *
     * @param toElement high endpoint (exclusive) of the returned set
     *
     * @return a view of the portion of this set whose elements range from
     *         <tt>fromElement</tt>, inclusive, to <tt>toElement</tt>, exclusive
     *
     * @throws ClassCastException if <tt>fromElement</tt> and
     *         <tt>toElement</tt> cannot be compared to one another using this
     *         set's comparator (or, if the set has no comparator, using
     *         natural ordering).  Implementations may, but are not required
     *         to, throw this exception if <tt>fromElement</tt> or
     *         <tt>toElement</tt> cannot be compared to elements currently in
     *         the set.
     *
     * @throws NullPointerException if <tt>fromElement</tt> or
     *         <tt>toElement</tt> is null and this set does not permit null
     *         elements
     * @throws IllegalArgumentException if <tt>fromElement</tt> is
     *         greater than <tt>toElement</tt>; or if this set itself
     *         has a restricted range, and <tt>fromElement</tt> or
     *         <tt>toElement</tt> lies outside the bounds of the range
     */
    @Throws(ClassCastException::class, NullPointerException::class, IllegalArgumentException::class)
    fun sub(fromElement: E, toElement: E): ImmutableSortedSet<E>

    /**
     * Returns a view of the portion of this set whose elements are
     * strictly less than <tt>toElement</tt>.  The returned set is
     * backed by this set, so changes in the returned set are
     * reflected in this set, and vice-versa.  The returned set
     * supports all optional set operations that this set supports.
     *
     *
     * The returned set will throw an <tt>IllegalArgumentException</tt>
     * on an attempt to insert an element outside its range.
     *
     * @param toElement high endpoint (exclusive) of the returned set
     *
     * @return a view of the portion of this set whose elements are strictly
     *         less than <tt>toElement</tt>
     *
     * @throws ClassCastException if <tt>toElement</tt> is not compatible
     *         with this set's comparator (or, if the set has no comparator,
     *         if <tt>toElement</tt> does not implement [Comparable]).
     *         Implementations may, but are not required to, throw this
     *         exception if <tt>toElement</tt> cannot be compared to elements
     *         currently in the set.
     *
     * @throws NullPointerException if <tt>toElement</tt> is null and
     *         this set does not permit null elements
     *
     * @throws IllegalArgumentException if this set itself has a
     *         restricted range, and <tt>toElement</tt> lies outside the
     *         bounds of the range
     */
    @Throws(ClassCastException::class, NullPointerException::class, IllegalArgumentException::class)
    fun head(toElement: E): ImmutableSortedSet<E>

    /**
     * Returns a view of the portion of this set whose elements are
     * greater than or equal to <tt>fromElement</tt>.  The returned
     * set is backed by this set, so changes in the returned set are
     * reflected in this set, and vice-versa.  The returned set
     * supports all optional set operations that this set supports.
     *
     *
     * The returned set will throw an <tt>IllegalArgumentException</tt>
     * on an attempt to insert an element outside its range.
     *
     * @param fromElement low endpoint (inclusive) of the returned set
     *
     * @return a view of the portion of this set whose elements are greater
     *         than or equal to <tt>fromElement</tt>
     *
     * @throws ClassCastException if <tt>fromElement</tt> is not compatible
     *         with this set's comparator (or, if the set has no comparator,
     *         if <tt>fromElement</tt> does not implement [Comparable]).
     *         Implementations may, but are not required to, throw this
     *         exception if <tt>fromElement</tt> cannot be compared to elements
     *         currently in the set.
     *
     * @throws NullPointerException if <tt>fromElement</tt> is null
     *         and this set does not permit null elements
     *
     * @throws IllegalArgumentException if this set itself has a
     *         restricted range, and <tt>fromElement</tt> lies outside the
     *         bounds of the range
     */
    @Throws(ClassCastException::class, NullPointerException::class, IllegalArgumentException::class)
    fun tail(fromElement: E): ImmutableSortedSet<E>

    /**
     * Returns the first (lowest) element currently in this set.

     * @return the first (lowest) element currently in this set
     *
     * @throws NoSuchElementException if this set is empty
     */
    @Throws(NoSuchElementException::class)
    fun first(): E

    /**
     * Returns the last (highest) element currently in this set.

     * @return the last (highest) element currently in this set
     *
     * @throws NoSuchElementException if this set is empty
     */
    @Throws(NoSuchElementException::class)
    fun last(): E
}
