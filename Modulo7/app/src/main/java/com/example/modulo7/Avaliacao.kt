package com.example.modulo7

data class Avaliacao(
    var nomeDoLocal: String? = "",
    var nomeDoUsuario: String? = "",
    val cidade: String? = "",
    val comentario: String? = "",
    val rating: Float = 0f,
    var status: String? = "",  // Status da avaliação (Aprovado/Reprovado)
    var avaliacaoId: String? = ""// ID único da avaliação
)
