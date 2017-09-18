package com.runesuite.client.core.api

import com.runesuite.client.core.api.live.Worlds
import com.runesuite.client.core.raw.Wrapper
import com.runesuite.client.core.raw.access.XFriend

class Friend(override val accessor: XFriend) : Wrapper() {

    val name: String get() = accessor.name

    val previousName: String? get() = accessor.previousName.takeUnless { it.isNullOrEmpty() }

    val worldId get() = accessor.world

    val world get() = Worlds[accessor.world]

    val rank get() = checkNotNull(ClanMate.Rank.LOOKUP[accessor.rank.toByte()]) { accessor.rank }

    override fun toString(): String {
        return "Friend(name=$name, previousName=$previousName, worldId=$worldId, rank=$rank)"
    }
}