package com.example.modulo7

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var avaliacaoList: ArrayList<Avaliacao>
    private lateinit var emptyMessage: TextView
    private lateinit var adapter: AvaliacaoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        recyclerView = findViewById(R.id.reciclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        avaliacaoList = ArrayList()
        emptyMessage = findViewById(R.id.emptyMessage)

        adapter = AvaliacaoAdapter(avaliacaoList) { avaliacao ->
            val intent = Intent(this, DetalhesAvaliacaoActivity::class.java).apply {
                putExtra("nomeDoLocal", avaliacao.nomeDoLocal)
                putExtra("nomeDoUsuario", avaliacao.nomeDoUsuario)
                putExtra("cidade", avaliacao.cidade)
                putExtra("comentario", avaliacao.comentario)
                putExtra("rating", avaliacao.rating)
                putExtra("status", avaliacao.status)
            }
            startActivity(intent)
        }

        recyclerView.adapter = adapter

        // Carregar as avaliações do Firebase
        val database = FirebaseDatabase.getInstance().reference
        database.child("avaliacoes").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                avaliacaoList.clear()
                for (data in snapshot.children) {
                    val avaliacao = data.getValue(Avaliacao::class.java)
                    if (avaliacao != null) {
                        avaliacaoList.add(avaliacao)
                    }
                }
                // Atualizar visibilidade da mensagem caso a lista esteja vazia
                if (avaliacaoList.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    emptyMessage.visibility = View.VISIBLE
                } else {
                    recyclerView.visibility = View.VISIBLE
                    emptyMessage.visibility = View.GONE
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ListActivity, "Erro ao carregar avaliações", Toast.LENGTH_SHORT).show()
            }
        })

        // Configurar o swipe para deletar
        configurarSwipeParaDeletar()
    }

    private fun configurarSwipeParaDeletar() {
        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val avaliacao = avaliacaoList[position]

                // Remover do Firebase
                val database = FirebaseDatabase.getInstance().reference
                database.child("avaliacoes").orderByChild("comentario")
                    .equalTo(avaliacao.comentario)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (child in snapshot.children) {
                                child.ref.removeValue()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(this@ListActivity, "Erro ao excluir avaliação", Toast.LENGTH_SHORT).show()
                        }
                    })

                // Remover da lista local
                avaliacaoList.removeAt(position)
                adapter.notifyItemRemoved(position)

                // Atualizar visibilidade da mensagem
                if (avaliacaoList.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    emptyMessage.visibility = View.VISIBLE
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}
