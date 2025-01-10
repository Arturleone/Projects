package com.example.ws_modulo3_nacional

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private var attempCount = 0
    private val maxAttempts = 3
    private lateinit var Acessar: Button
    private lateinit var Cadastrar: Button
    private lateinit var Usuario: EditText
    private lateinit var Senha: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


    }
}