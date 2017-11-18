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

enum class MojangStatistics(val key: String) {
    MINECRAFT_SOLD("item_minecraft_sold"),
    PREPAID_MINECRAFT_REDEEMED("prepaid_card_redeemed_minecraft"),
    COBALT_SOLD("item_sold_cobalt"),
    SCROLLS_SOLD("item_sold_scrolls")
}