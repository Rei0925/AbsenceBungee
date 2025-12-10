package com.github.rei0925.absenceBungee

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class DbManager(
    url: String,
    user: String,
    password: String
) {
    private var connection: Connection? = null

    init {
        try {
            // Explicitly load MariaDB JDBC driver
            Class.forName("org.mariadb.jdbc.Driver")
            connection = DriverManager.getConnection(url, user, password)
            setupDatabase()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    private fun setupDatabase() {
        try {
            // Create database if not exists
            connection?.createStatement()?.use { stmt ->
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS Absence")
            }
            // Switch to Absence database
            connection?.createStatement()?.use { stmt ->
                stmt.executeUpdate("USE Absence")
            }
            // Create table if not exists
            connection?.createStatement()?.use { stmt ->
                stmt.executeUpdate(
                    """
                    CREATE TABLE IF NOT EXISTS AbsencePlayerList (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(50),
                        uuid VARCHAR(36),
                        end_date DATE
                    )
                    """.trimIndent()
                )
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun getAllPlayers(): List<Map<String, Any>> {
        val result = mutableListOf<Map<String, Any>>()
        try {
            connection?.prepareStatement("SELECT * FROM AbsencePlayerList")?.use { stmt ->
                stmt.executeQuery().use { rs ->
                    val meta = rs.metaData
                    while (rs.next()) {
                        val row = mutableMapOf<String, Any>()
                        for (i in 1..meta.columnCount) {
                            row[meta.getColumnName(i)] = rs.getObject(i) ?: ""
                        }
                        result.add(row)
                    }
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return result
    }

    fun getPlayerByUuid(uuid: String): Map<String, Any>? {
        try {
            connection?.prepareStatement("SELECT * FROM AbsencePlayerList WHERE uuid = ?")?.use { stmt ->
                stmt.setString(1, uuid)
                stmt.executeQuery().use { rs ->
                    val meta = rs.metaData
                    if (rs.next()) {
                        val row = mutableMapOf<String, Any>()
                        for (i in 1..meta.columnCount) {
                            row[meta.getColumnName(i)] = rs.getObject(i) ?: ""
                        }
                        return row
                    }
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return null
    }

    fun getPlayerByName(name: String): Map<String, Any>? {
        try {
            connection?.prepareStatement("SELECT * FROM AbsencePlayerList WHERE name = ?")?.use { stmt ->
                stmt.setString(1, name)
                stmt.executeQuery().use { rs ->
                    val meta = rs.metaData
                    if (rs.next()) {
                        val row = mutableMapOf<String, Any>()
                        for (i in 1..meta.columnCount) {
                            row[meta.getColumnName(i)] = rs.getObject(i) ?: ""
                        }
                        return row
                    }
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return null
    }

    fun getAllPlayerNames(): List<String> {
        val names = mutableListOf<String>()
        try {
            connection?.prepareStatement("SELECT name FROM AbsencePlayerList")?.use { stmt ->
                stmt.executeQuery().use { rs ->
                    while (rs.next()) {
                        names.add(rs.getString("name"))
                    }
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return names
    }

    fun playerExists(name: String, uuid: String): Boolean {
        try {
            connection?.prepareStatement("SELECT 1 FROM AbsencePlayerList WHERE name = ? OR uuid = ? LIMIT 1")?.use { stmt ->
                stmt.setString(1, name)
                stmt.setString(2, uuid)
                stmt.executeQuery().use { rs ->
                    if (rs.next()) {
                        return true
                    }
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return false
    }

    fun addPlayerIfNotExists(name: String, uuid: String, endDate: String): Boolean {
        if (playerExists(name, uuid)) {
            return false
        }
        try {
            connection?.prepareStatement("INSERT INTO AbsencePlayerList (name, uuid, end_date) VALUES (?, ?, ?)")?.use { stmt ->
                stmt.setString(1, name)
                stmt.setString(2, uuid)
                stmt.setString(3, endDate)
                stmt.executeUpdate()
                return true
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return false
    }

    fun removePlayerByName(name: String): Boolean {
        try {
            connection?.prepareStatement("DELETE FROM AbsencePlayerList WHERE name = ?")?.use { stmt ->
                stmt.setString(1, name)
                val updatedRows = stmt.executeUpdate()
                return updatedRows > 0
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return false
    }
}