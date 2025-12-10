package com.github.rei0925.absenceBungee

import net.md_5.bungee.api.event.PostLoginEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler

class JoinListener: Listener {

    @EventHandler
    fun onJoin(event: PostLoginEvent) {
        val player = event.player
        val name = player.name
        val uuid = player.uniqueId

        val data = AbsenceBungee.dbManager.getPlayerByUuid(uuid.toString())
        val endDateRaw = data?.get("end_date")?.toString() ?: "0000-00-00"
        val formattedDate = endDateRaw.substring(2, 4) + "-" + endDateRaw.substring(5, 7) + "-" + endDateRaw.substring(8, 10)

        val exists = AbsenceBungee.dbManager.playerExists(name, uuid.toString())

        if (exists) {
            val audience = AbsenceBungee.adventure.sender(player)

            // 日付チェック
            val isExpired = try {
                val endDate = java.time.LocalDate.parse(endDateRaw)
                endDate.isBefore(java.time.LocalDate.now())
            } catch (e: Exception) {
                false
            }

            if (isExpired) {
                // 不在届破棄
                AbsenceBungee.dbManager.removePlayerByName(name)

                // 期限切れ通知
                audience.sendMessage(
                    AbsenceComponent.JOIN_MESSAGE_EXPIRED(formattedDate)
                )
            } else {
                // 通常の不在届通知
                audience.sendMessage(
                    AbsenceComponent.JOIN_MESSAGE(formattedDate)
                )
            }
        }
    }
}