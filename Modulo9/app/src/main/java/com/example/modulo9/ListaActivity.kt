package com.example.modulo9

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.example.modulo9.API.Reclamacao
import com.example.modulo9.API.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListaActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var editTextPesquisa: EditText
    private var reclamacoes: MutableList<Reclamacao> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista)

        // Inicializa componentes
        listView = findViewById(R.id.ListView)
        editTextPesquisa = findViewById(R.id.editTextPesquisa)
        val buttonRegistro = findViewById<LinearLayout>(R.id.buttonRegistro)
        buttonRegistro.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        buscarReclamacoes()

        configurarEditTextPesquisa()
    }

    private fun buscarReclamacoes() {
        Log.d("ListaActivity", "Iniciando busca de reclamações...")
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) { RetrofitClient.apiService.listarReclamacoes() }
                if (response.isSuccessful && response.body() != null) {
                    Log.d("ListaActivity", "Reclamações carregadas com sucesso.")
                    reclamacoes.clear()
                    reclamacoes.addAll(response.body()!!)
                    atualizarListView(reclamacoes)
                } else {
                    Log.e("ListaActivity", "Erro ao carregar reclamações: ${response.code()}")
                    Toast.makeText(this@ListaActivity, "Erro ao carregar reclamações: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("ListaActivity", "Falha na conexão: ${e.message}")
                Toast.makeText(this@ListaActivity, "Falha na conexão: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun atualizarListView(reclamacoes: List<Reclamacao>) {
        Log.d("ListaActivity", "Atualizando ListView com ${reclamacoes.size} reclamações.")
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            reclamacoes.map { "${it.titulo} - ${it.tipo}" } // Exibe título e tipo
        )
        listView.adapter = adapter
    }

    private fun configurarEditTextPesquisa() {
        Log.d("ListaActivity", "Configuração do EditText iniciada.")
        editTextPesquisa.addTextChangedListener { newText ->
            Log.d("ListaActivity", "Texto alterado na pesquisa: $newText")
            filtrarReclamacoes(newText.toString())
        }
    }

    private fun filtrarReclamacoes(query: String) {
        Log.d("ListaActivity", "Filtrando reclamações com a consulta: $query")
        val reclamacoesFiltradas = if (query.isEmpty()) {
            Log.d("ListaActivity", "Nenhum filtro aplicado, mostrando todas as reclamações.")
            reclamacoes // Se não há texto, exibe todas as reclamações
        } else {
            reclamacoes.filter {
                val tituloValido = it.titulo.contains(query, ignoreCase = true) ?: false
                val tipoValido = it.tipo.contains(query, ignoreCase = true) ?: false
                tituloValido || tipoValido
            }
        }
        Log.d("ListaActivity", "Número de reclamações filtradas: ${reclamacoesFiltradas.size}")
        atualizarListView(reclamacoesFiltradas)
    }
}
