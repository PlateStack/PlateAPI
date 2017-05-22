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

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

@RunWith(JUnitPlatform::class)
class VersionRangeTest: Spek({
    given("Version range from 1.0.0 to 10.0.0 excluding 5.0.0") {
        val range = VersionRange(Version(1), Version(10), VersionRange(Version(5)))
        on("check if 5.0.0 is in it") {
            it("must return false") {
                assertFalse(Version(5) in range)
            }
        }

        on("check if 5.0.0 is excluded") {
            it("must return true") {
                assertTrue(range excludes Version(5))
            }
        }

        on("check if 5.0.1 is in it") {
            it("must return true") {
                assertTrue(Version(5,0,1) in range)
            }
        }

        on("check if 5.0.1 is excluded") {
            it("must return false") {
                assertFalse(range excludes Version(5,0,1))
            }
        }
    }
})