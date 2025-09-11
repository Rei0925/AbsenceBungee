package com.github.rei0925.absenceBungee

import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent

object CommandManager {
    fun list(sender: CommandSender){
        val header = TextComponent("§6長期不在届提出者情報》")

        sender.sendMessage(header)
        val players = AbsenceBungee.dbManager.getAllPlayers()
        for (player in players) {
            val name = player["name"]?.toString() ?: "Unknown"
            val endDate = player["end_date"]?.toString() ?: "0000-00-00"
            val formattedDate = endDate.substring(2, 4) + "-" + endDate.substring(5, 7) + "-" + endDate.substring(8, 10)
            val line = TextComponent("§a$name ~$formattedDate")
            sender.sendMessage(line)
        }
    }
    fun check(sender: CommandSender, target: String) {
        // Send header
        sender.sendMessage(TextComponent("§6長期不在届提出者情報》"))

        val proxiedPlayer = net.md_5.bungee.api.ProxyServer.getInstance().getPlayer(target)
        if (proxiedPlayer != null) {
            // Player is online
            val playerData = AbsenceBungee.dbManager.getPlayerByName(proxiedPlayer.name)
            if (playerData != null) {
                val name = playerData["name"]?.toString() ?: "Unknown"
                val endDateRaw = playerData["end_date"]?.toString() ?: "0000-00-00"
                val formattedDate = endDateRaw.substring(2, 4) + "-" + endDateRaw.substring(5, 7) + "-" + endDateRaw.substring(8, 10)
                sender.sendMessage(TextComponent("§a$name§fは§c~$formattedDate§fで不在届を受理しています。"))
            } else {
                sender.sendMessage(TextComponent("§cプレイヤー ${proxiedPlayer.name} の情報が見つかりませんでした。"))
            }
        } else {
            // Player is offline, try to get from DB
            val playerData = AbsenceBungee.dbManager.getPlayerByName(target)
            if (playerData != null) {
                val name = playerData["name"]?.toString() ?: "Unknown"
                val endDateRaw = playerData["end_date"]?.toString() ?: "0000-00-00"
                val formattedDate = endDateRaw.substring(2, 4) + "-" + endDateRaw.substring(5, 7) + "-" + endDateRaw.substring(8, 10)
                sender.sendMessage(TextComponent("§a$name§fは§c~$formattedDate§fで不在届を受理しています。"))
            } else {
                sender.sendMessage(TextComponent("§cプレイヤー $target の情報が見つかりませんでした。"))
            }
        }
    }
}