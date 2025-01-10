package com.example.artur_leonel_modulo03_pe

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.artur_leonel_modulo03_pe.API.RetrofitClient
import com.example.artur_leonel_modulo03_pe.API.Usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CadastrarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cadastrar)

        val imageView = findViewById<ImageView>(R.id.imageView4)
        val nome = findViewById<EditText>(R.id.editTextText3)
        val email = findViewById<EditText>(R.id.editTextText4)
        val senha = findViewById<EditText>(R.id.editTextText5)
        val confirmar = findViewById<EditText>(R.id.editTextText6)

        imageView.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        val button = findViewById<Button>(R.id.button3)
        button.setOnClickListener {
            val inputNome = nome.text.toString()
            val inputEmail = email.text.toString()
            val inputSenha = senha.text.toString()
            val inputConfirmar = confirmar.text.toString()
            if (inputNome.isBlank() || inputEmail.isBlank() || inputSenha.isBlank() || inputConfirmar.isBlank()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            } else {
                val usuario = Usuario(inputNome, inputEmail, inputSenha,"MG" , 1)
                cadastrarUsuario(usuario)

            }
        }

    }

    private fun cadastrarUsuario(usuario: Usuario) {
        RetrofitClient.UsuariosApi.cadastrarUsuario(usuario).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@CadastrarActivity, "Usuário cadastrado com sucesso!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@CadastrarActivity, "Erro no cadastro: ${response.code()} ${response.message()}", Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@CadastrarActivity, "Erro na comunicação com a API: ${t.message}", Toast.LENGTH_LONG).show()
                Log.e("CadastroActivity", "Erro na comunicação: ${t.message}", t)
            }
        })
    }


}