package com.example.assignment.util

import android.util.Log
import com.example.assignment.BuildConfig
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object DatabaseConnection {

    private const val TAG = "DatabaseConnection"

    private val url: String
        get() = "jdbc:mysql://${BuildConfig.DB_HOST}:${BuildConfig.DB_PORT}/${BuildConfig.DB_NAME}" +
                "?connectTimeout=5000&socketTimeout=10000&useSSL=false&allowPublicKeyRetrieval=true"

    private val user: String get() = BuildConfig.DB_USER
    private val password: String get() = BuildConfig.DB_PASSWORD

    private var connection: Connection? = null

    fun getConnection(): Connection? {
        try {
            if (connection == null || connection!!.isClosed) {
                Log.d(TAG, "Opening new connection to $url")
                Class.forName("com.mysql.jdbc.Driver")
                connection = DriverManager.getConnection(url, user, password)
                Log.d(TAG, "✅ Connection established")
            }
        } catch (e: SQLException) {
            Log.e(TAG, "❌ SQLException [${e.sqlState}] ${e.message}")
        } catch (e: ClassNotFoundException) {
            Log.e(TAG, "❌ Driver not found: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "❌ ${e.javaClass.simpleName}: ${e.message}")
        }
        return connection
    }

    fun testConnection(): String {
        return try {
            Class.forName("com.mysql.jdbc.Driver")
            val conn = DriverManager.getConnection(url, user, password)
            val valid = conn.isValid(3)
            conn.close()
            if (valid) "✅ Berhasil terhubung ke $url"
            else "❌ Koneksi tidak valid"
        } catch (e: Exception) {
            "❌ ${e.javaClass.simpleName}: ${e.message}"
        }
    }
}
