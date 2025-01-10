package com.example.artur_leonel_mod3

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private lateinit var Senha: EditText
private lateinit var usuario: EditText
private lateinit var Cadastrar: Button
private lateinit var Acessar: Button

private var attempCount = 0

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val red = ContextCompat.getDrawable(this, R.drawable.redground)
        val black = ContextCompat.getDrawable(this, R.drawable.blackground)

        val usuario = findViewById<EditText>(R.id.editTextText)
        val Senha = findViewById<EditText>(R.id.editTextText2)
        val Acessar = findViewById<Button>(R.id.button)
        val Cadastrar = findViewById<Button>(R.id.button2)

        Cadastrar.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        Acessar.setOnClickListener {
            val email = usuario.text.toString()
            val senha = Senha.text.toString()
            if (email.isBlank() || senha.isBlank()) {
                attempCount++
                if (attempCount > 3) {
                    Senha.isEnabled = false
                    usuario.isEnabled = false
                    Cadastrar.isEnabled = false
                    Acessar.isEnabled = false
                    usuario.background = black
                    Senha.background = black
                    Handler().postDelayed({
                        Senha.isEnabled = true
                        usuario.isEnabled = true
                        Cadastrar.isEnabled = true
                        Acessar.isEnabled = true
                        attempCount = 0
                    }, 30000)
                }
                usuario.background = red
                Senha.background = red

                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            } else {
                realizarLogin(email, senha)
            }

        }
    }

    private fun realizarLogin(email: String, senha: String) {
        RetrofitClient.UsuariosApi.realizarLogin(email, senha).enqueue(object:Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if(response.isSuccessful) {
                    listarUsuarios(email)
                } else {
                    listarUsuarios(email)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {

            }
        })

    }


    private fun listarUsuarios(email: String) {
        RetrofitClient.UsuariosApi.listarUsuarios().enqueue(object : Callback<List<UserDetails>> {
            override fun onResponse(
                call: Call<List<UserDetails>>,
                response: Response<List<UserDetails>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val usuarios = response.body()
                    val usuarioCerto = usuarios?.find {it.email == email}

                    if (usuarioCerto != null) {

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        val email = usuarioCerto.email
                        val dr = usuarioCerto.dr
                        val nome = usuarioCerto.nome
                        intent.putExtra("usuario", nome)
                        intent.putExtra("email", email)
                        intent.putExtra("dr", dr)
                        startActivity(intent)
                    } else {
                        val red = ContextCompat.getDrawable(this@LoginActivity, R.drawable.redground)
                        val black = ContextCompat.getDrawable(this@LoginActivity, R.drawable.blackground)
                        attempCount++
                        if (attempCount > 3) {
                            Senha.isEnabled = false
                            usuario.isEnabled = false
                            Cadastrar.isEnabled = false
                            Acessar.isEnabled = false
                            usuario.background = black
                            Senha.background = black
                            Handler().postDelayed({
                                Senha.isEnabled = true
                                usuario.isEnabled = true
                                Cadastrar.isEnabled = true
                                Acessar.isEnabled = true
                                attempCount = 0
                            }, 30000)
                        }
                        usuario.background = red
                        Senha.background = red
                        Toast.makeText(this@LoginActivity, "Usuário não encontrado", Toast.LENGTH_SHORT).show()
                    }


                } else {

                }
            }

            override fun onFailure(call: Call<List<UserDetails>>, t: Throwable) {
            }

        })
    }

    private fun verificarQuantidadeEBloquear() {
        val red = ContextCompat.getDrawable(this, R.drawable.redground)
        val black = ContextCompat.getDrawable(this, R.drawable.blackground)

        }
    }