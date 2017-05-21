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

package br.com.gamemods.platestack.api.message

import java.net.URL
import java.nio.file.Path

sealed class ClickEvent(val action: String, val value: Message) {
    companion object {
        internal fun <B, R> B.tryTo(action: B.() -> R): R? {
            try {
                return action()
            }
            catch (e: Exception) {
                return null
            }
        }
    }

    class OpenURL(url: Message): ClickEvent("open_url", url) {
        constructor(url: String): this(Message(url))
        constructor(url: URL): this(url.toString())
    }

    class OpenFile(file: Message): ClickEvent("open_file", file) {
        constructor(file: String): this(Message(file))
        constructor(path: Path): this(path.toString())
    }

    @Deprecated("Removed from Minecraft 1.8+")
    class TwitchUserInfo(user: Message): ClickEvent("twitch_user_info", user) {

        @Suppress("DEPRECATION")
        constructor(user: String): this(Message(user))
    }

    class RunCommand(command: Message): ClickEvent("run_command", command) {
        constructor(command: String): this(Message(command))
    }

    class SuggestCommand(command: Message): ClickEvent("suggest_command", command) {
        constructor(command: String): this(Message(command))
    }

    class ChangePage private constructor(val page: Int?, value: Message): ClickEvent("change_page", value) {
        constructor(page: String): this(page.tryTo { toInt() }, Message(page))
        constructor(page: Int): this(page.toString())

        override fun toString(): String {
            return "ChangePage(page=$page, value=$value)"
        }
    }

    override fun toString(): String {
        return "${javaClass.simpleName}(action='$action', value=$value)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as ClickEvent

        if (action != other.action) return false
        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = action.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }
}