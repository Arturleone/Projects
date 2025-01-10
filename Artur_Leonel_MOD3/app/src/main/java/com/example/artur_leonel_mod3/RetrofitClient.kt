package com.example.artur_leonel_mod3

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val url = "https://apieuvounatrip.azurewebsites.net/"

    val UsuariosApi: UsuarioApi by lazy {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(url)
            .build()
        retrofit.create(UsuarioApi::class.java)
    }
}