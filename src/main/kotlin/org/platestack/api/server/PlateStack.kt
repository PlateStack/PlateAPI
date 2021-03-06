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
@file:JvmName("PlateStack")

package org.platestack.api.server

import org.platestack.util.UniqueModification

/**
 * An identification pattern which must be followed in all PlateStack's API which requires ID registration.
 *
 * Additional requirements may be applied, such as minimum and maximum ID length.
 *
 * Platform and Minecraft IDs do not follow this rule.
 *
 * This pattern enforces that IDs must:
 * * Be completely lower-cased
 * * Begins with a letter
 * * Ends with a letter
 * * Have only English unaccented letters
 * * Have no spaces (underscore is allowed as long as it doesn't repeat)
 */
@JvmField val ID_VALIDATOR = Regex("^[a-z](_[a-z]+)+$")

var PlateStack by UniqueModification<PlateServer>()
    @JvmName("getServer") get
    @JvmName("setServer") set

