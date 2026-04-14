package com.example.assignment.util

import com.example.assignment.model.User

object SessionManager {
    var currentUser: User? = null

    fun login(user: User) {
        currentUser = user
    }

    fun logout() {
        currentUser = null
    }

    fun isLoggedIn(): Boolean {
        return currentUser != null
    }
}
