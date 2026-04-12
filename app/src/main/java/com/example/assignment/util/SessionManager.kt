package com.example.assignment.util

import com.example.assignment.model.User

// Menyimpan informasi user yang sedang login
object SessionManager {
    var currentUser: User? = null
        private set

    fun setUser(user: User) {
        currentUser = user
    }

    fun logout() {
        currentUser = null
    }
}
