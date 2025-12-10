package com.github.rei0925.absenceBungee

import co.aikar.commands.BungeeCommandManager
import net.kyori.adventure.platform.bungeecord.BungeeAudiences
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.plugin.Plugin
import java.sql.Connection
import java.sql.SQLException

class AbsenceBungee : Plugin() {

    private var connection: Connection? = null
    lateinit var commandManager: CommandManager

    companion object {
        lateinit var instance: AbsenceBungee
            private set
        lateinit var dbManager: DbManager
            private set
        lateinit var adventure: BungeeAudiences
    }

    override fun onEnable() {
        instance = this
        adventure = BungeeAudiences.create(this)
        // config.ymlをコピーするよん
        val configFile = java.io.File(dataFolder, "config.yml")
        if (!configFile.exists()) {
            if (!dataFolder.exists()) {
                dataFolder.mkdirs()
            }
            // resources/config.yml を dataFolder にコピー
            getResourceAsStream("config.yml")?.use { input ->
                configFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }

        // config.ymlをロード
        val config = net.md_5.bungee.config.ConfigurationProvider.getProvider(
            net.md_5.bungee.config.YamlConfiguration::class.java
        ).load(configFile)

        dbManager = DbManager(
            config.getString("database.url"),
            config.getString("database.user"),
            config.getString("database.password")
        )
        commandManager = CommandManager(this)

        val manager = BungeeCommandManager(this)

        // タブ補完登録
        manager.commandCompletions.registerCompletion("players") { context ->
            dbManager.getAllPlayerNames()
        }
        manager.commandCompletions.registerCompletion("online_players") { context ->
            ProxyServer.getInstance().players.map { it.name }
        }

        // コマンド登録
        manager.registerCommand(CommandListener(commandManager))
        manager.registerCommand(CommandListenerAdmin(commandManager))
        //イベントリスナー登録
        proxy.pluginManager.registerListener(this, JoinListener())
    }

    override fun onDisable() {
        // Plugin shutdown logic
        connection?.let {
            try {
                it.close()
            } catch (e: SQLException) {
                proxy.logger.severe("Error closing database connection: ${e.message}")
            }
        }
    }
}
