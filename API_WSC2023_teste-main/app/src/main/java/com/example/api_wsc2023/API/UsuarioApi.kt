package com.example.api_wsc2023.API

import com.example.api_wsc2023.DateClasses.LoginResponse
import com.example.api_wsc2023.DateClasses.UserDetailsResponse
import com.example.api_wsc2023.DateClasses.Usuario
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface UsuarioApi {
    @POST("/api/Usuarios")
    fun cadastrarUsuario(@Body usuario: Usuario): Call<Void>

    @POST("/api/Usuarios/Login")
    fun realizarLogin(
        @Query("email") email: String,
        @Query("senha") senha: String
    ): Call<LoginResponse>

    // Método para listar todos os usuários
    @GET("/api/Usuarios")
    fun listarUsuarios(): Call<List<UserDetailsResponse>>
}
