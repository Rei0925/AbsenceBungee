package com.github.rei0925.absenceBungee

import net.kyori.adventure.audience.Audience
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.ProxyServer
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeParseException

class CommandManager(
    private val absenceBungee: AbsenceBungee
) {

    fun list(sender: CommandSender) {
        val audience = AbsenceBungee.adventure.sender(sender)

        audience.sendMessage(AbsenceComponent.LIST_HEADER)

        val players = AbsenceBungee.dbManager.getAllPlayers()

        for (player in players) {
            val name = player["name"]?.toString() ?: "Unknown"
            val endDate = player["end_date"]?.toString() ?: "0000-00-00"

            val formatted = endDate.substring(2, 4) + "-" +
                    endDate.substring(5, 7) + "-" +
                    endDate.substring(8, 10)

            audience.sendMessage(
                AbsenceComponent.LIST_LINE(name, formatted)
            )
        }
    }
    fun check(sender: CommandSender, target: String) {
        val proxiedPlayer = ProxyServer.getInstance().getPlayer(target)
        val audience = AbsenceBungee.adventure.sender(sender)

        if (proxiedPlayer != null) {

            val playerData = AbsenceBungee.dbManager.getPlayerByName(proxiedPlayer.name)
            if (playerData != null) {
                val name = playerData["name"]?.toString() ?: "Unknown"
                val endDateRaw = playerData["end_date"]?.toString() ?: "0000-00-00"
                val formattedDate = endDateRaw.substring(2, 4) + "-" + endDateRaw.substring(5, 7) + "-" + endDateRaw.substring(8, 10)
                audience.sendMessage(AbsenceComponent.PLAYER_CHECK(name,formattedDate))

            } else {
                audience.sendMessage(AbsenceComponent.DATA_NOT_FOUND(proxiedPlayer.name))
            }
        } else {
            // Player is offline, try to get from DB
            val playerData = AbsenceBungee.dbManager.getPlayerByName(target)
            if (playerData != null) {
                val name = playerData["name"]?.toString() ?: "Unknown"
                val endDateRaw = playerData["end_date"]?.toString() ?: "0000-00-00"
                val formattedDate = endDateRaw.substring(2, 4) + "-" + endDateRaw.substring(5, 7) + "-" + endDateRaw.substring(8, 10)
                audience.sendMessage(AbsenceComponent.PLAYER_CHECK(name,formattedDate))
            } else {
                audience.sendMessage(AbsenceComponent.DATA_NOT_FOUND(target))
            }
        }
    }

    fun add(sender: CommandSender, target: String, endDate: String) {
        val audience = AbsenceBungee.adventure.sender(sender)

        val parsedDate = try {
            LocalDate.parse(endDate)
        } catch (e: DateTimeParseException) {
            audience.sendMessage(AbsenceComponent.DATE_FORMAT_INVALID)
            return
        }

        val proxiedPlayer = ProxyServer.getInstance().getPlayer(target)

        if (proxiedPlayer != null) {
            val uuid = proxiedPlayer.uniqueId.toString()
            handleAdd(target, uuid, parsedDate, audience)
            return
        }

        // ↓ オフライン → Mojang API に非同期問い合わせ
        ProxyServer.getInstance().scheduler.runAsync(AbsenceBungee.instance) {

            val uuid = fetchUUIDFromMojang(target) // 下に関数実装あり

            ProxyServer.getInstance().scheduler.runAsync(AbsenceBungee.instance) {
                if (uuid == null) {
                    audience.sendMessage(AbsenceComponent.DATA_NOT_FOUND(target))
                    return@runAsync
                }

                handleAdd(target, uuid, parsedDate, audience)
            }
        }
    }

    fun del(sender: CommandSender, target: String) {
        val audience = AbsenceBungee.adventure.sender(sender)

        try {
            val exists = AbsenceBungee.dbManager.playerExists(target, "")
            if (!exists) {
                audience.sendMessage(AbsenceComponent.PLAYER_NOT_FOUND(target))
                return
            }

            val removed = AbsenceBungee.dbManager.removePlayerByName(target)
            if (removed) {
                audience.sendMessage(AbsenceComponent.PLAYER_DELETED(target))
            } else {
                audience.sendMessage(AbsenceComponent.DELETE_FAILED)
            }

        } catch (e: Exception) {
            audience.sendMessage(AbsenceComponent.ERROR_OCCURED(e.message ?: "不明なエラー"))
        }
    }

    private fun handleAdd(name: String, uuid: String, date: LocalDate, audience: Audience) {
        val added = AbsenceBungee.dbManager.addPlayerIfNotExists(name, uuid, date.toString())
        if (added) {
            audience.sendMessage(AbsenceComponent.PLAYER_ADDED(name, date.toString()))
        } else {
            audience.sendMessage(AbsenceComponent.PLAYER_ALREADY_EXISTS(name))
        }
    }

    private fun fetchUUIDFromMojang(name: String): String? {
        return try {
            val url = URL("https://api.mojang.com/users/profiles/minecraft/$name")
            val conn = url.openConnection()
            conn.connectTimeout = 3000
            conn.readTimeout = 3000

            val json = conn.getInputStream().bufferedReader().readText()

            val gson = com.google.gson.Gson()
            val obj = gson.fromJson(json, Map::class.java)

            val rawId = obj["id"]?.toString() ?: return null
            formatUUID(rawId)
        } catch (e: Exception) {
            null
        }
    }

    private fun formatUUID(raw: String): String {
        return raw.replace(
            Regex("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{12})")
        ) {
            "${it.groupValues[1]}-${it.groupValues[2]}-${it.groupValues[3]}-${it.groupValues[4]}-${it.groupValues[5]}"
        }
    }
}