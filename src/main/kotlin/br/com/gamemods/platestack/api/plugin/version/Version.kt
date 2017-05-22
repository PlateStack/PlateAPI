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

package br.com.gamemods.platestack.api.plugin.version

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.immutableListOf
import kotlinx.collections.immutable.toImmutableList

/**
 * A version following the [Semantic Versioning 2.0.0](http://semver.org/spec/v2.0.0.html) rules.
 *
 * Examples from simplest to full versions:
 * * 0.13.5 : `Version(0,13,5)`
 * * 2.8.255-beta : `Version(2,8,255,"beta")`
 * * 1.3.13-2.5.13.0.alpha.3 : `Version(1,3,13, "2","5","13","0" "alpha","3")
 * * 45.158.9-rc.3.50+build38.sha.5589 : `Version(45,158,9, "rc","3","50", metadata = "build38.sha.5589")`
 *
 * @property major A number that is incremented when an incompatible API change happens. `0` means that no public or stable API is available.
 * @property minor A number that is incremented when a new functionality in a backwards-compatible manner
 * @property patch A number that is incremented when backwards-compatible bug fixes are published
 * @property label Empty label indicates that this is a stable version, non-empty indicates.
 *
 * Check the [specification item 9](http://semver.org/spec/v2.0.0.html#spec-item-9) for more information
 * @property metadata An optional build metadata. Check ths [specification item 10](http://semver.org/spec/v2.0.0.html#spec-item-10) for more information
 * @property raw The original string which generated this version object
 * @property stable If this represents a stable version. SemVer defines that stable versions doesn't have labels and the major version is higher then zero.
 */
data class Version(
        val major: Int, val minor: Int = 0, val patch: Int = 0,
        val label: ImmutableList<String> = immutableListOf(), val metadata: String = "",
        val raw: String = "",
        val stable: Boolean = label.isEmpty() && major > 0
): Comparable<Version> {

    /**
     * Friendly constructor which allows to define the labels as direct arguments
     */
    @JvmOverloads
    constructor(major: Int, minor: Int = 0, patch: Int = 0, vararg label: String = emptyArray(), metadata: String = "")
            : this(major, minor, patch, immutableListOf(*label), metadata)

    init {
        require(major >= 0) { "The major version must be >= 0. $major was used. $this" }
        require(minor >= 0) { "The minor version must be >= 0. $minor was used. $this" }
        require(patch >= 0) { "The patch version must be >= 0. $patch was used. $this" }
        label.forEach {
            require(it.isNotBlank()) { "Empty label strings are not allowed. Use an empty array if the version has no label. $this" }
            if(it.isInt())
                require(!INVALID_NUMERIC_LABEL.matches(it)) { "The label \"$it\" is invalid. Numeric identifiers MUST NOT include leading zeroes. $this" }
            else
                require(STRING_LABEL.matches(it)) { "The label \"$it\" is invalid. Identifiers MUST comprise only ASCII alphanumerics and hyphen [0-9A-Za-z-]. $this" }
        }
        require(metadata.isEmpty() || VALID_METADATA.matches(metadata)) {
            "The metadata \"$metadata\" is invalid. Build metadata MAY be denoted by a series of dot separated identifiers. " +
                    "Identifiers MUST comprise only ASCII alphanumerics and hyphen [0-9A-Za-z-]. Identifiers MUST NOT be empty. $this "
        }
    }

    override fun compareTo(other: Version) = compareTo(other, true)

    /**
     * Compares both version respecting the SemVersion definition
     */
    fun compareTo(other: Version, ignoreCase: Boolean): Int {
        var diff: Int
        diff = major.compareTo(other.major); if(diff != 0) return diff
        diff = minor.compareTo(other.minor); if(diff != 0) return diff
        diff = patch.compareTo(other.patch); if(diff != 0) return diff

        if(stable) {
            if(!other.stable) return 1
        } else if(other.stable) return -1

        if(label.isEmpty()) {
            if(other.label.isNotEmpty()) return 1
        } else if(other.label.isEmpty()) return -1

        val thisSize = label.size
        val otherSize = other.label.size
        val differentSize = thisSize != otherSize
        val maxSize = if(differentSize) maxOf(label.size, other.label.size) else thisSize

        if(maxSize == 0) return 0

        for (i in 0..maxSize-1) {
            if(differentSize) {
                if (i == thisSize) return -1
                if (i == otherSize) return 1
            }

            val a = label[i]
            val b = other.label[i]

            if(!a.equals(b, ignoreCase)) {
                if(a.isInt()) {
                    if(b.isInt()) {
                        diff = a.toInt().compareTo(b.toInt())
                        if(diff != 0) return diff
                    } else return -1
                }
                else if(b.isInt()) return 1

                val differentCharLen = a.length != b.length
                val maxChar = if(differentCharLen) maxOf(a.length, b.length) else a.length
                for (c in 0..maxChar - 1) {
                    if(differentCharLen) {
                        if (c == a.length) return -1
                        if (c == b.length) return 1
                    }

                    val ac: Char
                    val bc: Char
                    if(ignoreCase) {
                        ac = a[c].toLowerCase()
                        bc = b[c].toLowerCase()
                    } else {
                        ac = a[c]
                        bc = b[c]
                    }

                    diff = ac.compareTo(bc)
                    if(diff != 0) return diff
                }
            }
        }

        return 0
    }

    /**
     * Externalize this version
     */
    override fun toString(): String {
        if(raw.isNotBlank())
            return raw

        return "$major.$minor.$patch" +
                (if(label.isNotEmpty()) "-"+label.joinToString(".") else "") +
                (if(metadata.isNotBlank()) "+$metadata" else "")
    }

    /**
     * Checks if this version is the same as an other according to the SemVersion definition
     */
    infix fun sameAs(other: Version) = compareTo(other) == 0

    companion object {
        private val INVALID_NUMERIC_LABEL = Regex("^0[0-9]+$")
        private val STRING_LABEL = Regex("^[0-9A-Za-z-]+$")
        private val VALID_METADATA = Regex("^[0-9A-Za-z-]+(\\.[0-9A-Za-z-]+)*$")

        private val FIRST_NUMBERS = Regex("^([0-9]+)(?:\\.([0-9]+))*")
        private val LABEL_PART = Regex("^-?([^0-9a-zA-Z._-]+)?\\+?(.*)?$")
        private val MULTI_DOT = Regex("\\.{2,}")
        private val FIX_ZERO = Regex("^0+")

        /**
         * Checks if a string only contains digits
         */
        fun String.isInt() = all { it in '0'..'9' }

        /**
         * Parses a version annotation
         */
        @Suppress("DEPRECATION")
        @JvmStatic fun from(annotation: br.com.gamemods.platestack.api.plugin.annotation.Version): Version {
            if(annotation.value.isBlank()) {
                return Version(annotation.major, annotation.minor, annotation.patch, immutableListOf(*annotation.label), annotation.metadata)
            }
            return parse(annotation.value)
        }

        /**
         * Parses a string into the closest SemVersion definition as possible
         */
        @JvmStatic fun parse(version: String): Version {
            var carret = 0
            val numbers = FIRST_NUMBERS.find(version)?.value?.let {
                carret += it.length
                it.split(".")
            } ?: emptyList()

            val maj = numbers.getOrNull(0)?.toInt() ?: 0
            val min = numbers.getOrNull(1)?.toInt() ?: 0
            val pat = numbers.getOrNull(2)?.toInt() ?: 0

            val label = mutableListOf<String>()
            numbers.listIterator(3).forEach { label += it }

            var unstable = numbers.isEmpty()
            val build = LABEL_PART.find(version.substring(carret))?.groupValues?.let {
                val definedLabel = it[1]
                if(definedLabel.isNotBlank()) {
                    unstable = true
                    val fixedLabel = definedLabel.replace('_', '-').replace(MULTI_DOT, ".")
                    label += fixedLabel.split(".")
                }
                it[2]
            } ?: ""

            val iter = label.listIterator()
            for (item in iter) {
                if(item.isInt() && INVALID_NUMERIC_LABEL.matches(item)) {
                    val replaced = item.replace(FIRST_NUMBERS, "")
                    if(replaced.isEmpty())
                        iter.set("0")
                    else
                        iter.set(replaced)
                }
            }

            return Version(maj, min, pat, label.toImmutableList(), build, version, !unstable && maj > 0)
        }
    }
}
