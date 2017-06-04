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

package org.platestack.api.plugin

import org.platestack.api.plugin.version.MavenArtifact
import org.platestack.api.plugin.version.Version
import org.platestack.structure.immutable.ImmutableSet
import org.platestack.structure.immutable.toImmutableSet

data class PlateMetadata(val id: String, val name: String, val version: Version, val jdk: String,
                         val relations: ImmutableSet<Relation>, val libraries: ImmutableSet<MavenArtifact>
) {
    constructor(id: String, name: String, version: Version, jdk: String, relations: Iterable<Relation>, libraries: Iterable<MavenArtifact> = emptyList())
            : this(id, name, version, jdk, relations.toImmutableSet(), libraries.toImmutableSet())
}
