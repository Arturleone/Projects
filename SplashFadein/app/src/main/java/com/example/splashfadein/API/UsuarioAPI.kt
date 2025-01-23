package com.example.splashfadein.API

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface UsuarioApi {
    @POST("api/Usuarios")
    fun cadastrarUsuario(@Body usuario: Usuario): Call<Void>

    @POST("/api/Usuarios/Login")
    fun realizarLogin(
        @Query("email") email: String,
        @Query("senha") senha: String
    ): Call<LoginResponse>

    // Método para listar todos os usuários
    @GET("/api/Usuarios")
    fun listarUsuarios(): Call<List<UserDetails>>

}
