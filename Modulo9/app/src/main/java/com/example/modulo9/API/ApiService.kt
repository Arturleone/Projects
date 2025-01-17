package com.example.modulo9.API

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("/api/reclamacoes") // Altere conforme o endpoint da API
    suspend fun criarReclamacao(@Body reclamacao: Reclamacao): retrofit2.Response<Void>

    @GET("/api/reclamacoes") // Adicione o endpoint para buscar as reclamações
    suspend fun listarReclamacoes(): Response<List<Reclamacao>>
}