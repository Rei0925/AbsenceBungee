package com.github.rei0925.absenceBungee

import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.chat.TextComponent
import java.time.LocalDate
import java.time.format.DateTimeParseException

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

        val proxiedPlayer = ProxyServer.getInstance().getPlayer(target)
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

    fun add(sender: CommandSender, target: String, endDate: String) {
        // 日付チェック
        sender.sendMessage(TextComponent("§6長期不在届提出者情報》"))
        val parsedDate: LocalDate
        try {
            parsedDate = LocalDate.parse(endDate) // YYYY-MM-DD形式
        } catch (e: DateTimeParseException) {
            sender.sendMessage(TextComponent("§c日付の形式が不正です。YYYY-MM-DDで入力してください。"))
            return
        }

        // オンラインならUUIDを取得、オフラインなら空文字
        val proxiedPlayer = ProxyServer.getInstance().getPlayer(target)
        val uuid = proxiedPlayer?.uniqueId?.toString() ?: ""

        // DB に追加（重複を避ける）
        val added = AbsenceBungee.dbManager.addPlayerIfNotExists(target, uuid, parsedDate.toString())

        if (added) {
            sender.sendMessage(TextComponent("§aプレイヤー $target の不在届を追加しました。終了日: §c$parsedDate"))
        } else {
            sender.sendMessage(TextComponent("§eプレイヤー $target の不在届は既に存在します。"))
        }
    }

    fun del(sender: CommandSender, target: String) {
        sender.sendMessage(TextComponent("§6長期不在届提出者情報》"))
        try {
            // UUID is unknown, pass empty string
            val exists = AbsenceBungee.dbManager.playerExists(target, "")
            if (!exists) {
                sender.sendMessage(TextComponent("§cプレイヤー $target の情報が見つかりませんでした。"))
                return
            }
            // Delete player from AbsencePlayerList using dbManager method
            val removed = AbsenceBungee.dbManager.removePlayerByName(target)
            if (removed) {
                sender.sendMessage(TextComponent("§aプレイヤー $target の不在届情報を削除しました。"))
            } else {
                sender.sendMessage(TextComponent("§cプレイヤー $target の削除に失敗しました。"))
            }
        } catch (e: Exception) {
            sender.sendMessage(TextComponent("§c削除中にエラーが発生しました: ${e.message}"))
        }
    }
}