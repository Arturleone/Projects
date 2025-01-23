package com.example.splashfadein

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.splashfadein.API.LoginResponse
import com.example.splashfadein.API.RetrofitClient
import com.example.splashfadein.API.UserDetails
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "LoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

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
                Toast.makeText(this, "Campos Inválidos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun realizarLogin(email: String, senha: String) {
        RetrofitClient.usuarioApi.realizarLogin(email, senha).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    listarUsuarios(email)
                } else {
                    Toast.makeText(this@LoginActivity, "Campos Inválidos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
            }
        })
    }

    private fun listarUsuarios(email: String) {
        RetrofitClient.usuarioApi.listarUsuarios().enqueue(object : Callback<List<UserDetails>> {
            override fun onResponse(
                call: Call<List<UserDetails>>,
                response: Response<List<UserDetails>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val usuarios = response.body()
                    val usuarioCerto = usuarios?.find { it.email == email }

                    if (usuarioCerto != null) {
                        val intent = Intent(this@LoginActivity, Home::class.java)
                        intent.putExtra("username", usuarioCerto.nome)
                        intent.putExtra("email", usuarioCerto.email)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@LoginActivity, "Campos Inválidos", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<List<UserDetails>>, t: Throwable) {
               }
        })
    }
}
