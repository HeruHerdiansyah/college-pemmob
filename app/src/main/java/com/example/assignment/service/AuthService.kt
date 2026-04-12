package com.example.assignment.service

import com.example.assignment.model.Role
import com.example.assignment.model.User
import com.example.assignment.util.DatabaseConnection
import java.sql.SQLException

class AuthService {

    // Autentikasi login: cek username & password di database
    fun authenticate(username: String, password: String): User? {
        val query = """
            SELECT u.id, u.username, u.password, u.full_name, r.id AS role_id, r.role_name, r.description 
            FROM users u JOIN roles r ON u.role_id = r.id 
            WHERE u.username = ? AND u.password = ?
        """.trimIndent()

        try {
            val conn = DatabaseConnection.getConnection() ?: return null
            val stmt = conn.prepareStatement(query)
            stmt.setString(1, username)
            stmt.setString(2, password)
            val rs = stmt.executeQuery()

            if (rs.next()) {
                val role = Role(
                    id = rs.getInt("role_id"),
                    roleName = rs.getString("role_name"),
                    description = rs.getString("description")
                )
                return User(
                    id = rs.getInt("id"),
                    username = rs.getString("username"),
                    password = rs.getString("password"),
                    fullName = rs.getString("full_name"),
                    role = role
                )
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return null
    }

    // Register user baru
    fun register(username: String, password: String, fullName: String, roleId: Int): Boolean {
        if (isUsernameTaken(username)) {
            return false
        }

        val query = "INSERT INTO users (username, password, full_name, role_id) VALUES (?, ?, ?, ?)"
        try {
            val conn = DatabaseConnection.getConnection() ?: return false
            val stmt = conn.prepareStatement(query)
            stmt.setString(1, username)
            stmt.setString(2, password)
            stmt.setString(3, fullName)
            stmt.setInt(4, roleId)
            return stmt.executeUpdate() > 0
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return false
    }

    // Cek apakah username sudah digunakan
    fun isUsernameTaken(username: String): Boolean {
        val query = "SELECT id FROM users WHERE username = ?"
        try {
            val conn = DatabaseConnection.getConnection() ?: return false
            val stmt = conn.prepareStatement(query)
            stmt.setString(1, username)
            val rs = stmt.executeQuery()
            return rs.next()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return false
    }

    // Ambil semua role dari database untuk Spinner register
    fun getAllRoles(): List<Role> {
        val roles = mutableListOf<Role>()
        val query = "SELECT * FROM roles ORDER BY id"
        try {
            val conn = DatabaseConnection.getConnection() ?: return roles
            val stmt = conn.prepareStatement(query)
            val rs = stmt.executeQuery()

            while (rs.next()) {
                roles.add(
                    Role(
                        id = rs.getInt("id"),
                        roleName = rs.getString("role_name"),
                        description = rs.getString("description")
                    )
                )
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return roles
    }

    // Verifikasi password lama user
    fun verifyPassword(userId: Int, password: String): Boolean {
        val query = "SELECT id FROM users WHERE id = ? AND password = ?"
        try {
            val conn = DatabaseConnection.getConnection() ?: return false
            val stmt = conn.prepareStatement(query)
            stmt.setInt(1, userId)
            stmt.setString(2, password)
            val rs = stmt.executeQuery()
            return rs.next()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return false
    }

    // Ganti password user
    fun changePassword(userId: Int, oldPassword: String, newPassword: String): Boolean {
        if (!verifyPassword(userId, oldPassword)) {
            return false
        }

        val query = "UPDATE users SET password = ? WHERE id = ?"
        try {
            val conn = DatabaseConnection.getConnection() ?: return false
            val stmt = conn.prepareStatement(query)
            stmt.setString(1, newPassword)
            stmt.setInt(2, userId)
            return stmt.executeUpdate() > 0
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return false
    }
}
