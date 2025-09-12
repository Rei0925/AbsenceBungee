package com.github.rei0925.absenceBungee

import co.aikar.commands.BungeeCommandManager
import net.kyori.adventure.platform.bungeecord.BungeeAudiences
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.plugin.Plugin
import java.sql.Connection
import java.sql.SQLException

class AbsenceBungee : Plugin() {

    private var connection: Connection? = null

    companion object {
        lateinit var dbManager: DbManager
            private set
        lateinit var adventure: BungeeAudiences
    }

    override fun onEnable() {
        adventure = BungeeAudiences.create(this)
        //CommandManager.adventure = adventure
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

        val commandManager = BungeeCommandManager(this)

        // タブ補完登録
        commandManager.commandCompletions.registerCompletion("players") { context ->
            dbManager.getAllPlayerNames()
        }
        commandManager.commandCompletions.registerCompletion("online_players") { context ->
            ProxyServer.getInstance().players.map { it.name }
        }

        // コマンド登録
        commandManager.registerCommand(CommandListener())
        commandManager.registerCommand(CommandListenerAdmin())
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
