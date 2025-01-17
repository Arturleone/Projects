package com.example.modulo7

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DetalhesAvaliacaoActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var avaliacaoID: String // Usando ID da avaliação
    private val TAG = "DetalhesAvaliacao"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes_avaliacao)

        Log.d(TAG, "onCreate chamado. Inicializando Firebase e UI.")

        // Inicializando o Firebase Database
        database = FirebaseDatabase.getInstance().reference

        // Receber dados passados pela intent
        val nomeDoLocal = intent.getStringExtra("nomeDoLocal")
        val nomeDoUsuario = intent.getStringExtra("nomeDoUsuario")
        avaliacaoID = intent.getStringExtra("avaliacaoID") ?: "" // Usando ID da avaliação
        val rating = intent.getFloatExtra("rating", 0f)
        val status = intent.getStringExtra("status")

        Log.d(TAG, "Dados recebidos: nomeDoLocal=$nomeDoLocal, nomeDoUsuario=$nomeDoUsuario, avaliacaoID=$avaliacaoID, rating=$rating, status=$status")

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

        Log.d(TAG, "UI inicializada com os dados.")

        // Função para Aprovar
        buttonAprovar.setOnClickListener {
            Log.d(TAG, "Botão Aprovar clicado. avaliacaoID=$avaliacaoID")
            alterarStatus("Aprovado")
        }

        // Função para Reprovar
        buttonReprovar.setOnClickListener {
            Log.d(TAG, "Botão Reprovar clicado. avaliacaoID=$avaliacaoID")
            alterarStatus("Reprovado")
        }
    }

    // Função para alterar o status da avaliação
    private fun alterarStatus(novoStatus: String) {
        Log.d(TAG, "alterarStatus chamado com novoStatus=$novoStatus, avaliacaoID=$avaliacaoID")

        // Atualizar o status no Firebase usando o ID da avaliação
        database.child("avaliacoes").child(avaliacaoID).child("status").setValue(novoStatus)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Status atualizado com sucesso no Firebase.")
                    Toast.makeText(this, "Avaliação $novoStatus com sucesso!", Toast.LENGTH_SHORT).show()
                    finish() // Voltar para a tela anterior
                } else {
                    Log.e(TAG, "Erro ao atualizar status: ${task.exception?.message}")
                    Toast.makeText(this, "Erro ao atualizar status: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
