package com.example.api_wsc2023.Activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.api_wsc2023.API.RetrofitClient
import com.example.api_wsc2023.DateClasses.LoginResponse
import com.example.api_wsc2023.DateClasses.UserDetailsResponse
import com.example.api_wsc2023.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etSenha: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnCadastrar: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etEmailLogin)
        etSenha = findViewById(R.id.etSenhaLogin)
        btnLogin = findViewById(R.id.btnLogin)
        btnCadastrar = findViewById(R.id.btnCadastrar)

        sharedPreferences = getSharedPreferences("APP_PREFS", MODE_PRIVATE)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val senha = etSenha.text.toString()

            if (email.isNotEmpty() && senha.isNotEmpty()) {
                realizarLogin(email, senha)
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }

        btnCadastrar.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }
    }

    private fun realizarLogin(email: String, senha: String) {
        val call = RetrofitClient.usuarioApi.realizarLogin(email, senha)
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    // Chamar a função para listar usuários e filtrar pelo e-mail
                    listarUsuarios(email)
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Erro ao realizar login: ${response.errorBody()?.string()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
            }
        })
    }

    private fun listarUsuarios(email: String) {
        val call = RetrofitClient.usuarioApi.listarUsuarios()
        call.enqueue(object : Callback<List<UserDetailsResponse>> {
            override fun onResponse(
                call: Call<List<UserDetailsResponse>>,
                response: Response<List<UserDetailsResponse>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val usuarios = response.body()!!
                    val usuario = usuarios.find { it.email == email }

                    if (usuario != null) {
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.putExtra("EMAIL", usuario.email) // Email
                        intent.putExtra("DR", usuario.dr) // DR
                        intent.putExtra("NOME", usuario.nome) // Nome
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Usuário não encontrado.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<List<UserDetailsResponse>>, t: Throwable) {
            }
        })
    }
}
