package com.example.modulo7

data class Avaliacao(
    val nomeDoLocal: String = "",
    val nomeDoUsuario: String = "",
    val cidade: String = "",
    val comentario: String = "",
    val rating: Float = 0f,
    val status: String = "",  // Status da avaliação (Aprovado/Reprovado)
    val avaliacaoId: String = "" // ID único da avaliação
)
