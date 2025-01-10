package com.example.modulo7

import android.os.Bundle
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DetalhesAvaliacaoActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var avaliacaoId: String // Usando ID da avaliação

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes_avaliacao)

        // Inicializando o Firebase Database
        database = FirebaseDatabase.getInstance().reference

        // Receber dados passados pela intent
        val nomeDoLocal = intent.getStringExtra("nomeDoLocal")
        val nomeDoUsuario = intent.getStringExtra("nomeDoUsuario")
        avaliacaoId = intent.getStringExtra("avaliacaoId") ?: "" // Usando ID da avaliação
        val rating = intent.getFloatExtra("rating", 0f)
        val status = intent.getStringExtra("status")

        // Referenciar os componentes da UI
        val textViewNomeDoLocal = findViewById<TextView>(R.id.textViewNomeDoLocal)
        val textViewNomeDoUsuario = findViewById<TextView>(R.id.textViewNomeDoUsuario)
        val ratingBar = findViewById<RatingBar>(R.id.ratingBar)
        val status2 = findViewById<TextView>(R.id.status)
        val textViewComentario = findViewById<TextView>(R.id.textViewComentario)
        val buttonAprovar = findViewById<Button>(R.id.buttonAprovar)
        val buttonReprovar = findViewById<Button>(R.id.buttonReprovar)

        // Preencher os campos com as informações recebidas
        textViewNomeDoLocal.text = nomeDoLocal
        textViewNomeDoUsuario.text = nomeDoUsuario
        ratingBar.rating = rating
        textViewComentario.text = "Comentário indisponível"
        status2.text = status

        // Função para Aprovar
        buttonAprovar.setOnClickListener {
            alterarStatus("Aprovado")
        }

        // Função para Reprovar
        buttonReprovar.setOnClickListener {
            alterarStatus("Reprovado")
        }
    }

    // Função para alterar o status da avaliação
    private fun alterarStatus(novoStatus: String) {
        // Atualizar o status no Firebase usando o ID da avaliação
        database.child("avaliacoes").child(avaliacaoId).child("status").setValue(novoStatus)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Avaliação $novoStatus com sucesso!", Toast.LENGTH_SHORT).show()
                    finish() // Voltar para a tela anterior
                } else {
                    Toast.makeText(this, "Erro ao atualizar status: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
