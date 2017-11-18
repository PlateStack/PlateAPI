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

package org.platestack.api.mojang

import org.platestack.structure.immutable.ImmutableMap
import java.util.*

data class MojangAuth(val accessToken: String, val clientToken: String?, val availableProfiles: ImmutableMap<UUID, Profile>, val selectedProfile: Profile, val user: User) {
    init {
        availableProfiles.forEach { key, (id) -> check(key == id) }
        check(selectedProfile.id in availableProfiles)
    }

    data class Profile(val id: UUID, val name: String, val legacy: Boolean)
    data class User(val id: UUID, val properties: ImmutableMap<String, String>) {
        val preferredLanguage : String? get() = properties["preferredLanguage"]
        val twitchAccessToken : String? get() = properties["twitch_access_token"]
    }
}