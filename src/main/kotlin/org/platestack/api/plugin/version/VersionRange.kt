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

package org.platestack.api.plugin.version

import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.immutableHashSetOf

/**
 * A selection of ranges which supports exclusions.
 *
 * @property min The minimum inclusive version which matches this range. Must be lower then [max]. `null` indicates no minimal.
 * @property max The maximum inclusive version which matches this range. Must be higher then [min]. `null` indicates no maximum.
 * @property excludes Versions which will be excluded from this version range
 * @property unstable If the range accepts unstable versions
 * @property caseSensitive If the label checks are sensitive to uppercase
 */
data class VersionRange(val min: Version?, val max: Version?, val exclusions: ImmutableSet<VersionRange> = immutableHashSetOf(), val unstable: Boolean = true, val caseSensitive: Boolean = true) {
    constructor(min: Version? = null, max: Version? = null, vararg exclusions: VersionRange, unstable: Boolean = true, caseSensitive: Boolean = true): this(min, max, immutableHashSetOf(*exclusions), unstable, caseSensitive)
    @JvmOverloads constructor(min: Version?, max: Version?, vararg exclusions: VersionRange, unstable: Boolean = true): this(min, max, immutableHashSetOf(*exclusions), unstable, true)
    @JvmOverloads constructor(single: Version, vararg exclusion: VersionRange, unstable: Boolean = true, caseSensitive: Boolean = true): this(single, single, immutableHashSetOf(*exclusion), unstable, caseSensitive)
    init {
        if(min != null && max != null) {
            require(min <= max) { "$min must be less or equals to $max"}
        }
    }

    operator fun contains(version: Version) = (min == null || min <= version) && (max == null || version <= max) && !excludes(version)
    infix fun excludes(version: Version): Boolean = exclusions.any { version in it }
}
