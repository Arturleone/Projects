package com.example.splashfadein

import android.os.Bundle
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import com.example.splashfadein.API.RetrofitClient
import com.example.splashfadein.API.Usuario

class CadastroActivity : AppCompatActivity() {

    private val TAG = "CadastroActivity" // Tag para logs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cadastro)

        val nameFull = findViewById<EditText>(R.id.nome_completo_cadastro)
        val emailUsuario = findViewById<EditText>(R.id.email_usuario)
        val password = findViewById<EditText>(R.id.senha_cadastro)
        val confirmPassword = findViewById<EditText>(R.id.confirmar_senha_cadastro)
        val buttonCadastrarCadastro = findViewById<Button>(R.id.botao_cadastrar_cadastro)
        val iconVoltar = findViewById<ImageView>(R.id.icon_voltar)

        iconVoltar.setOnClickListener {
            Log.d(TAG, "Voltar para LoginActivity")
            startActivity(Intent(this, LoginActivity::class.java))
        }

        buttonCadastrarCadastro.setOnClickListener {
            val inputNameFull = nameFull.text.toString()
            val inputEmail = emailUsuario.text.toString()
            val inputPassword = password.text.toString()

            Log.d(TAG, "Campos de cadastro: Nome: $inputNameFull, Email: $inputEmail")

            val usuario = Usuario(inputNameFull, inputEmail, inputPassword, "MG", 1)

            Log.d(TAG, "Enviando dados para cadastro: $usuario")
            cadastrarUsuario(usuario)
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun cadastrarUsuario(usuario: Usuario) {
        Log.d(TAG, "Iniciando a chamada para cadastrar o usuário...")
        RetrofitClient.usuarioApi.cadastrarUsuario(usuario).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d(TAG, "Cadastro bem-sucedido: ${response.code()} ${response.message()}")
                    Toast.makeText(this@CadastroActivity, "Usuário cadastrado com sucesso!", Toast.LENGTH_LONG).show()
                } else {
                    Log.e(TAG, "Erro no cadastro: ${response.code()} ${response.message()}")
                    Toast.makeText(this@CadastroActivity, "Erro no cadastro: ${response.code()} ${response.message()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e(TAG, "Erro na comunicação com a API: ${t.message}", t)
                Toast.makeText(this@CadastroActivity, "Erro na comunicação com a API: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
