package com.example.splashfadein

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.splashfadein.API.LoginResponse
import com.example.splashfadein.API.RetrofitClient
import com.example.splashfadein.API.UserDetailsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private var attemptCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val sharedPreferences: SharedPreferences = getSharedPreferences("PREFS", MODE_PRIVATE)
        val botaoAcessar = findViewById<Button>(R.id.acessar_login)
        val botaoCadastro = findViewById<Button>(R.id.cadastrar_login)
        val userName = findViewById<EditText>(R.id.username_acessar)
        val password = findViewById<EditText>(R.id.password_login)

        botaoCadastro.setOnClickListener {
            startActivity(Intent(this, CadastroActivity::class.java))
        }

        botaoAcessar.setOnClickListener {
            val inputUsername = userName.text.toString()
            val inputPassword = password.text.toString()

            if (inputUsername.isNotBlank() && inputPassword.isNotBlank()) {
                realizarLogin(inputUsername, inputPassword)
            } else {
            }
        }
    }

    private fun messageShowBlock(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun realizarLogin(nome: String, senha: String) {
        listarUsuarios(nome, senha)
    }

    private fun listarUsuarios(nome: String, senha: String) {
        val call = RetrofitClient.usuarioApi.listarUsuarios()
        call.enqueue(object : Callback<List<UserDetailsResponse>> {
            override fun onResponse(
                call: Call<List<UserDetailsResponse>>,
                response: Response<List<UserDetailsResponse>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val usuarios = response.body() ?: emptyList()
                    val usuario = usuarios.find { it.nome == nome }

                    if (usuario != null) {
                        val email = usuario.email
                        realizarLoginComEmail(email, senha)
                    } else {
                        Toast.makeText(this@LoginActivity, "Usuário não encontrado.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<List<UserDetailsResponse>>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Erro de rede: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun realizarLoginComEmail(email: String, senha: String) {
        val call = RetrofitClient.usuarioApi.realizarLogin(email, senha)
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val usuario = response.body()
                    val intent = Intent(this@LoginActivity, Home::class.java)
                    intent.putExtra("EMAIL", email)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Erro ao realizar login. Código de erro: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Erro de rede: ${t.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
}
