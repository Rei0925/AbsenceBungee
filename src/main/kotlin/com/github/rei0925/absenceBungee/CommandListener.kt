package com.github.rei0925.absenceBungee

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer

@Suppress("unused")
@CommandAlias("absence")
class CommandListener(
    private val commandManager: CommandManager
) : BaseCommand() {
    @Subcommand("list")
    @Description("不在届提出者を一覧表示")
    fun absenceList(sender: CommandSender){
        commandManager.list(sender)
    }
    @Subcommand("check")
    @Description("引数に指定したプレイヤーの状況を確認")
    @CommandCompletion("@players")
    @Syntax("<player>")
    fun absenceCheck(sender: CommandSender, target: String){
        commandManager.check(sender,target)
    }
    @Default
    fun checkMe(sender: CommandSender){
        if(sender is ProxiedPlayer) {
            commandManager.check(sender, sender.name)
        }else{
            print("このコマンドはプレイヤーのみ実行可能です。")
        }
    }
}

@Suppress("unused")
@CommandAlias("absence-admin")
class CommandListenerAdmin(
    private val commandManager: CommandManager
) : BaseCommand(){
    @CommandAlias("add")
    @Description("不在届けを追加")
    @CommandCompletion("@online_players YYYY-MM-DD")
    @Syntax("<player> <end_date>")
    fun adminAdd(sender: CommandSender, target: String, endDate: String) {
        commandManager.add(sender,target,endDate)
    }
    @CommandAlias("del")
    @Description("不在届けを削除")
    @CommandCompletion("@players")
    @Syntax("<player>")
    fun adminDel(sender: CommandSender,target: String){
        commandManager.del(sender,target)
    }
}