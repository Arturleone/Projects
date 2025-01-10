package com.example.artur_leonel_modulo03_pe.API

data class LoginResponse (
    val token: String,
    val email: String,
    val dr: String,
    val nome: String,
    val id: Int
)