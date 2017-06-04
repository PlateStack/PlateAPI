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

import java.net.URI
import java.nio.file.Paths

data class MavenArtifact(val group: String, val artifact: String, val version: String) {
    init {
        require(group.isNotBlank()) { "The maven group may not be blank. $this" }
        require(artifact.isNotBlank()) { "The artifact name may not be blank. $this" }
        require(version.isNotBlank()) { "The version may not be blank. $this" }
    }

    val artifactFileName get() = "$artifact-$version.jar"
    val path get() = Paths.get(URI(group.replace('.', '/')+"/$artifact/$version/$artifactFileName"))!!
}
