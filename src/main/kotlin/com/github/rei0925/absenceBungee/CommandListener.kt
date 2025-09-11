package com.github.rei0925.absenceBungee

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Subcommand
import co.aikar.commands.annotation.Syntax
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer

@Suppress("unused")
@CommandAlias("absence")
class CommandListener : BaseCommand() {
    @Default
    @Subcommand("list")
    @Description("不在届提出者を一覧表示")
    fun absenceList(sender: CommandSender){
        CommandManager.list(sender)
    }
    @Subcommand("check")
    @Description("引数に指定したプレイヤーの状況を確認")
    @CommandCompletion("@players")
    @Syntax("<player>")
    fun absenceCheck(sender: CommandSender, target: String){
        CommandManager.check(sender,target)
    }
}