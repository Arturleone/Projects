package com.example.modulo7

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ListActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var avaliacaoList: ArrayList<Avaliacao>
    private lateinit var emptyMessage: TextView
    private lateinit var adapter: ArrayAdapter<Avaliacao>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        listView = findViewById(R.id.listview)
        avaliacaoList = ArrayList()
        emptyMessage = findViewById(R.id.emptyMessage)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, avaliacaoList)
        listView.adapter = adapter

        // Carregar as avaliações do Firebase
        val database = FirebaseDatabase.getInstance().reference
        database.child("avaliacoes").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                avaliacaoList.clear()
                for (data in snapshot.children) {
                    val avaliacao = data.getValue(Avaliacao::class.java)
                    val avaliacaoId = data.key // Obter o ID da avaliação
                    if (avaliacao != null) {
                        avaliacao.avaliacaoId = avaliacaoId.toString() // Definir o ID da avaliação
                        avaliacaoList.add(avaliacao)
                    }
                }
                // Atualizar visibilidade da mensagem caso a lista esteja vazia
                if (avaliacaoList.isEmpty()) {
                    listView.visibility = View.GONE
                    emptyMessage.visibility = View.VISIBLE
                } else {
                    listView.visibility = View.VISIBLE
                    emptyMessage.visibility = View.GONE
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ListActivity, "Erro ao carregar avaliações", Toast.LENGTH_SHORT).show()
            }
        })


        // Configurar item click
        listView.setOnItemClickListener { _, _, position, _ ->
            val avaliacao = avaliacaoList[position]
            val intent = Intent(this, DetalhesAvaliacaoActivity::class.java).apply {
                putExtra("nomeDoLocal", avaliacao.nomeDoLocal)
                putExtra("nomeDoUsuario", avaliacao.nomeDoUsuario)
                putExtra("cidade", avaliacao.cidade)
                putExtra("comentario", avaliacao.comentario)
                putExtra("rating", avaliacao.rating)
                putExtra("status", avaliacao.status)
                putExtra("avaliacaoID", avaliacao.avaliacaoId)
            }
            startActivity(intent)
        }
    }
}
