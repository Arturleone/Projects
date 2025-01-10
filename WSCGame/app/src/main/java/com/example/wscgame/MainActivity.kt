package com.example.wscgame

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val playerNameInput = findViewById<EditText>(R.id.playerNameInput)
        val startGameButton = findViewById<Button>(R.id.startGameButton)
        val rankingsButton = findViewById<Button>(R.id.rankingsButton)

        startGameButton.setOnClickListener {
            val playerName = playerNameInput.text.toString()
            if (playerName.isBlank()) {
                Toast.makeText(this, "Nome inválido!", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, GameActivity::class.java)
                intent.putExtra("PLAYER_NAME", playerName)
                startActivity(intent)
            }
        }

        // Configuração do botão de rankings, você pode adicionar a ação que desejar
        rankingsButton.setOnClickListener {
            // Ação de classificações
            Toast.makeText(this, "Rankings ainda não implementado", Toast.LENGTH_SHORT).show()
        }
    }
}
