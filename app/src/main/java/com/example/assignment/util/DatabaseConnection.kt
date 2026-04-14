package com.example.assignment.util

import com.example.assignment.BuildConfig
import java.sql.Connection
import java.sql.DriverManager

object DatabaseConnection {

    private val DB_HOST = BuildConfig.DB_HOST
    private val DB_PORT = BuildConfig.DB_PORT
    private val DB_NAME = BuildConfig.DB_NAME
    private val DB_USER = BuildConfig.DB_USER
    private val DB_PASSWORD = BuildConfig.DB_PASSWORD

    init {
        try {
            Class.forName("com.mysql.jdbc.Driver")
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }

    fun getConnection(): Connection? {
        return try {
            val url = "jdbc:mysql://$DB_HOST:$DB_PORT/$DB_NAME?useSSL=false&connectTimeout=10000&socketTimeout=15000"
            DriverManager.getConnection(url, DB_USER, DB_PASSWORD)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
