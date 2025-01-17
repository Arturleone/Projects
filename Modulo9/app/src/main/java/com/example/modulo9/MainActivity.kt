package com.example.modulo9

import ShortcutHelper
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.modulo9.API.RetrofitClient
import com.example.modulo9.API.Reclamacao
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var titulo: EditText
    private lateinit var descricao: EditText
    private lateinit var spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        ShortcutHelper.criarAtalhos(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        titulo = findViewById(R.id.editTextText)
        descricao = findViewById(R.id.editTextText2)
        spinner = findViewById(R.id.spinner)

        if (!verificarConexao()) {
            mostrarModalSemConexao()
        }

        findViewById<Button>(R.id.button).setOnClickListener {
            if (validarCampos()) {
                if (verificarConexao()) cadastrarReclamacao()
                else mostrarModalSemConexao()
            } else showToast("Todos os campos são obrigatórios!")
        }

        findViewById<Button>(R.id.button2).setOnClickListener { limparCampos() }

        findViewById<LinearLayout>(R.id.buttonReclamacoes).setOnClickListener {
            startActivity(Intent(this, ListaActivity::class.java))
        }
    }

    private fun limparCampos() {
        titulo.text.clear()
        descricao.text.clear()
        spinner.setSelection(0)
    }

    private fun validarCampos(): Boolean = when {
        titulo.text.isBlank() -> {
            showToast("Todos os campos são obrigatórios!")
            false
        }
        descricao.text.isBlank() -> {
            showToast("Todos os campos são obrigatórios!")
            false
        }

        else -> true
    }

    private fun verificarConexao(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    private fun mostrarModalSemConexao() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Atenção!")
            .setMessage("Sua conexão com a internet está indisponível! Aguarde.")
            .setCancelable(false)
            .create()
            .apply {
                show()
                lifecycleScope.launch {
                    while (!verificarConexao()) withContext(Dispatchers.IO) { Thread.sleep(1000) }
                    dismiss()
                }
            }
    }

    private fun cadastrarReclamacao() {
        val reclamacao = Reclamacao(
            titulo = titulo.text.toString(),
            descricao = descricao.text.toString(),
            tipo = ""
        )

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.criarReclamacao(reclamacao)
                }
                if (response.isSuccessful) {
                    showToast("Reclamação cadastrada com sucesso!")
                    limparCampos()
                    startActivity(Intent(this@MainActivity, ListaActivity::class.java))
                } else {
                    showToast("Erro ao cadastrar reclamação. Código: ${response.code()}")
                }
            } catch (e: Exception) {
                showToast("Falha na comunicação: ${e.message}")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
