package com.eric.proyectoqr.network

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("token") val token: String?,
    @SerializedName("user") val user: User?
)

data class User(
    val id: Int?,
    val name: String?,
    val email: String?
)
