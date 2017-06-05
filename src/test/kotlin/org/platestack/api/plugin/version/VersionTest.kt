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
import org.junit.Assert.*
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

@RunWith(JUnitPlatform::class)
class VersionTest: Spek({
    given("String 1.11.2-R0.1-SNAPSHOT") {
        val string = "1.11.2-R0.1-SNAPSHOT"
        on("parse") {
            val parse = Version.parse(string)
            it("should create a version instance like: Version(1,11,2,\"R0\",\"1-SNAPSHOT\"") {
                assertTrue(Version(1,11,2,"R0","1-SNAPSHOT").sameAs(parse))
            }
        }
    }

    given("String git-Bukkit-6e3cec8 (MC: 1.11.2)") {
        val string = "git-Bukkit-6e3cec8 (MC: 1.11.2)"
        on("parse") {
            val parse = Version.parse(string)
            it("should have major, minor and patch = 0") {
                assertEquals(0, parse.major)
                assertEquals(0, parse.minor)
                assertEquals(0, parse.patch)
            }
            it("should have a single label git-Bukkit-6e3cec8") {
                assertEquals(listOf("git-Bukkit-6e3cec8"), parse.label)
            }
            it("should have metadata MC1.11.2") {
                assertEquals("MC1.11.2", parse.metadata)
            }
            it("should still print as git-Bukkit-6e3cec8 (MC: 1.11.2)") {
                assertEquals("git-Bukkit-6e3cec8 (MC: 1.11.2)", parse.toString())
            }
        }
    }

    given("Version 1.2.3-0.a.b.c.33.d-e-f+00.g.h.i--j-l") {
        val version = Version(1,2,3,"0","a","b","c","33","d-e-f", metadata = "00.g.h.i--j-l")
        on("toString") {
            it("should print correctly") {
                assertEquals("1.2.3-0.a.b.c.33.d-e-f+00.g.h.i--j-l", version.toString())
            }
        }
    }

    given("Versions 1.0.0 , 2.0.0 , 2.1.0 , 2.1.1") {
        val versions = arrayOf(Version(1), Version(2), Version(2,1), Version(2,1,1))
        on("compare them") {
            it("must return that the versions on the right are higher") {
                for (i in 1..versions.size-1) {
                    for (j in i-1 downTo 0) {
                        assertTrue("${versions[j]} must be lower then ${versions[i]}", versions[i - 1] < versions[i])
                    }
                }
            }
        }
    }

    given("Versions 1.0.0-alpha , 1.0.0-alpha.1 , 1.0.0-alpha.beta , 1.0.0-beta , 1.0.0-beta.2 , 1.0.0-beta.11 , 1.0.0-rc.1 < 1.0.0") {
        val versions = arrayOf(
                Version(1,0,0,"alpha"), Version(1,0,0,"alpha","1"), Version(1,0,0,"alpha","beta"),
                Version(1,0,0,"beta"), Version(1,0,0,"beta","2"), Version(1,0,0,"beta","11"), Version(1,0,0,"beta","a"),
                Version(1,0,0,"rc","1"), Version(1)
        )

        on("compare them") {
            it("must return that the versions on the right are higher") {
                for (i in 1..versions.size-1) {
                    for (j in i-1 downTo 0) {
                        assertTrue("${versions[j]} must be lower then ${versions[i]}", versions[j] < versions[i])
                    }
                }
            }
        }
    }

    given("Versions 1.0.0+001, 1.0.0+20130313144700, 1.0.0+exp.sha.5114f85") {
        val versions = arrayOf(Version(1, metadata = "001"), Version(1, metadata = "20130313144700"), Version(1, metadata = "exp.sha.5114f85"))
        on("compareTo all them") {
            it("must result that all them are the same") {
                versions.forEach { a->
                    versions.forEach { b->
                        assertTrue("$a compareTo $b must return 0", a sameAs b)
                    }
                }
            }
        }

        on("equals all them") {
            it("must result that are all different") {
                for (i in 1..versions.size-1) {
                    for (j in i-1 downTo 0) {
                        assertFalse("${versions[j]} == ${versions[i]} must return false", versions[j] == versions[i])
                    }
                }
            }
        }
    }
})
