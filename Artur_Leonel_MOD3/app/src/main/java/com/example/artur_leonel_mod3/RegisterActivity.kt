package com.example.artur_leonel_mod3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        val nome = findViewById<EditText>(R.id.editTextText4)
        val email = findViewById<EditText>(R.id.editTextText5)
        val senha = findViewById<EditText>(R.id.editTextText6)
        val dr = findViewById<EditText>(R.id.editTextText7)
        val botao = findViewById<Button>(R.id.button3)
        val botaoCancelar = findViewById<Button>(R.id.button4)
        botao.setOnClickListener {
            if (nome.text.toString().isBlank() || email.text.toString().isBlank() || senha.text.toString().isBlank() || dr.text.toString().isBlank()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_LONG).show()
            } else {
                val usuario = Usuario(nome.text.toString(), email.text.toString(), senha.text.toString(),"MG", 1)
                cadastrarUsuario(usuario)

            }
        }

    }

    private fun cadastrarUsuario(usuario: Usuario) {
        RetrofitClient.UsuariosApi.cadastrarUsuario(usuario).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@RegisterActivity, "Usuário cadastrado com sucesso!", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                } else {
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
            }
        })
    }

}