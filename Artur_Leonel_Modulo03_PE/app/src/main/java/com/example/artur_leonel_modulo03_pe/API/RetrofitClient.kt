package com.example.artur_leonel_modulo03_pe.API

import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {
    private const val url = "https://apieuvounatrip.azurewebsites.net/"

    val UsuariosApi: UsuarioApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(UsuarioApi::class.java)
    }
}
