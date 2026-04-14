package com.example.assignment.model

data class Role(
    val id: Int,
    val roleName: String
) {
    override fun toString(): String = roleName
}
