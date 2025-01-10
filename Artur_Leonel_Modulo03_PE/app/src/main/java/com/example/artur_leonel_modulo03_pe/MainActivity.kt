package com.example.artur_leonel_modulo03_pe

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.artur_leonel_modulo03_pe.API.LoginResponse
import com.example.artur_leonel_modulo03_pe.API.RetrofitClient
import com.example.artur_leonel_modulo03_pe.API.UserDetails
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private var attemptCount = 0
    private val maxAttempts = 3
    private val lockoutDuration = 30000L // 30 segundos

    private lateinit var btnAcessar: Button
    private lateinit var btnCadastrar: Button
    private lateinit var edtUsuario: EditText
    private lateinit var edtSenha: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnAcessar = findViewById(R.id.button)
        btnCadastrar = findViewById(R.id.button2)
        edtUsuario = findViewById(R.id.editTextText)
        edtSenha = findViewById(R.id.editTextText2)

        btnCadastrar.setOnClickListener {
            startActivity(Intent(this, CadastrarActivity::class.java))
        }

        btnAcessar.setOnClickListener {
            val inputUsuario = edtUsuario.text.toString()
            val inputSenha = edtSenha.text.toString()

            if (inputUsuario.isBlank() || inputSenha.isBlank()) {
                exibirErroCampos("Preencha todos os campos")
            } else {
                realizarLogin(inputUsuario, inputSenha)
            }
        }
    }

    private fun exibirErroCampos(mensagem: String) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
        val redDrawable = ContextCompat.getDrawable(this, R.drawable.redblackgrounded)
        edtSenha.background = redDrawable
        edtUsuario.background = redDrawable

        attemptCount++
        if (attemptCount >= maxAttempts) {
            bloquearTela()
        }
    }

    private fun bloquearTela() {
        Toast.makeText(this, "Tela bloqueada por $lockoutDuration ms", Toast.LENGTH_SHORT).show()
        edtUsuario.isEnabled = false
        edtSenha.isEnabled = false
        btnAcessar.isEnabled = false
        btnCadastrar.isEnabled = false

        Handler().postDelayed({
            edtUsuario.isEnabled = true
            edtSenha.isEnabled = true
            btnAcessar.isEnabled = true
            btnCadastrar.isEnabled = true
            attemptCount = 0
        }, lockoutDuration)
    }

    private fun realizarLogin(email: String, senha: String) {
        RetrofitClient.UsuariosApi.realizarLogin(email, senha).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    listarUsuarios(email)
                } else {
                    exibirErroCampos("Credenciais inválidas")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                exibirErroCampos("Erro ao tentar login")
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
                    val usuarioCerto = usuarios?.find { it.email == email }

                    if (usuarioCerto != null) {
                        val intent = Intent(this@MainActivity, TelaPrincipal::class.java)
                        intent.putExtra("usuario", usuarioCerto.nome)
                        intent.putExtra("email", usuarioCerto.email)
                        intent.putExtra("dr", usuarioCerto.dr)
                        startActivity(intent)
                    } else {
                        exibirErroCampos("Usuário não encontrado")
                    }
                } else {
                    exibirErroCampos("Erro ao listar usuários")
                }
            }

            override fun onFailure(call: Call<List<UserDetails>>, t: Throwable) {
                exibirErroCampos("Erro ao listar usuários")
            }
        })
    }
}
