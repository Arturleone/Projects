package com.example.modulo8

data class EstadoWrapper(
    val estados: List<Estado>
)

data class Estado(
    val sigla: String,
    val nome: String,
    val cidades: List<Cidade>
)

data class Cidade(
    val cidade: String,
    val latitude: Double,
    val longitude: Double
)
