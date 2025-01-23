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
import androidx.appcompat.app.AlertDialog
import com.example.splashfadein.API.RetrofitClient
import com.example.splashfadein.API.Usuario

class CadastroActivity : AppCompatActivity() {

    private val TAG = "CadastroActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cadastro)

        val nameFull = findViewById<EditText>(R.id.nome_completo_cadastro)
        val emailUsuario = findViewById<EditText>(R.id.email_usuario)
        val password = findViewById<EditText>(R.id.senha_cadastro)
        val buttonCadastrarCadastro = findViewById<Button>(R.id.botao_cadastrar_cadastro)
        val iconVoltar = findViewById<ImageView>(R.id.icon_voltar)
        val iconVoltar2 = findViewById<Button>(R.id.cancelar)

        iconVoltar2.setOnClickListener{
            AlertDialog.Builder(this)
                .setTitle("Deseja sair mesmo?")
                .setPositiveButton("Sim") { _, _ ->
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                .setNegativeButton("Não") {_,_ ->
                }
                .show()
        }

        iconVoltar.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        buttonCadastrarCadastro.setOnClickListener {
            val inputNameFull = nameFull.text.toString()
            val inputEmail = emailUsuario.text.toString()
            val inputPassword = password.text.toString()
            if (inputPassword.isNotBlank() && inputEmail.isNotBlank() && inputNameFull.isNotBlank() ) {
                val usuario = Usuario(inputNameFull, inputEmail, inputPassword, "MG", 1)
                cadastrarUsuario(usuario)
        } else {
            Toast.makeText(this, "Campos Inválidos!", Toast.LENGTH_SHORT).show()
            }


        }
    }

    private fun cadastrarUsuario(usuario: Usuario) {
        RetrofitClient.usuarioApi.cadastrarUsuario(usuario).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                startActivity(Intent(this@CadastroActivity, LoginActivity::class.java))
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
            }
        })
    }
}
