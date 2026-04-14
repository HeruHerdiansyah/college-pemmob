package com.example.assignment.service

import com.example.assignment.model.Role
import com.example.assignment.model.User
import com.example.assignment.util.DatabaseConnection

object AuthService {

    fun authenticate(username: String, password: String): User? {
        val conn = DatabaseConnection.getConnection() ?: return null
        return try {
            val sql = "SELECT u.id, u.full_name, u.username, u.password, u.role_id, r.role_name " +
                    "FROM users u JOIN roles r ON u.role_id = r.id " +
                    "WHERE u.username = ? AND u.password = ?"
            val stmt = conn.prepareStatement(sql)
            stmt.setString(1, username)
            stmt.setString(2, password)
            val rs = stmt.executeQuery()
            if (rs.next()) {
                User(
                    id = rs.getInt("id"),
                    fullName = rs.getString("full_name"),
                    username = rs.getString("username"),
                    password = rs.getString("password"),
                    roleId = rs.getInt("role_id"),
                    roleName = rs.getString("role_name")
                )
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            conn.close()
        }
    }

    fun register(fullName: String, username: String, password: String, roleId: Int): Boolean {
        val conn = DatabaseConnection.getConnection() ?: return false
        return try {
            val sql = "INSERT INTO users (full_name, username, password, role_id) VALUES (?, ?, ?, ?)"
            val stmt = conn.prepareStatement(sql)
            stmt.setString(1, fullName)
            stmt.setString(2, username)
            stmt.setString(3, password)
            stmt.setInt(4, roleId)
            stmt.executeUpdate() > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            conn.close()
        }
    }

    fun isUsernameTaken(username: String): Boolean {
        val conn = DatabaseConnection.getConnection() ?: return false
        return try {
            val sql = "SELECT COUNT(*) FROM users WHERE username = ?"
            val stmt = conn.prepareStatement(sql)
            stmt.setString(1, username)
            val rs = stmt.executeQuery()
            rs.next() && rs.getInt(1) > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            conn.close()
        }
    }

    fun getAllRoles(): List<Role> {
        val conn = DatabaseConnection.getConnection() ?: return emptyList()
        return try {
            val sql = "SELECT id, role_name FROM roles ORDER BY id"
            val stmt = conn.prepareStatement(sql)
            val rs = stmt.executeQuery()
            val roles = mutableListOf<Role>()
            while (rs.next()) {
                roles.add(Role(rs.getInt("id"), rs.getString("role_name")))
            }
            roles
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        } finally {
            conn.close()
        }
    }

    fun verifyPassword(userId: Int, password: String): Boolean {
        val conn = DatabaseConnection.getConnection() ?: return false
        return try {
            val sql = "SELECT COUNT(*) FROM users WHERE id = ? AND password = ?"
            val stmt = conn.prepareStatement(sql)
            stmt.setInt(1, userId)
            stmt.setString(2, password)
            val rs = stmt.executeQuery()
            rs.next() && rs.getInt(1) > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            conn.close()
        }
    }

    fun changePassword(userId: Int, newPassword: String): Boolean {
        val conn = DatabaseConnection.getConnection() ?: return false
        return try {
            val sql = "UPDATE users SET password = ? WHERE id = ?"
            val stmt = conn.prepareStatement(sql)
            stmt.setString(1, newPassword)
            stmt.setInt(2, userId)
            stmt.executeUpdate() > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            conn.close()
        }
    }
}
