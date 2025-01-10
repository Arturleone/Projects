package com.example.modulo8

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.google.android.material.slider.Slider

class MainActivity : AppCompatActivity() {

    private lateinit var spinnerIdioma: Spinner
    private lateinit var spinnerContrasteTela2: Spinner
    private lateinit var spinnerContrasteTela: Spinner
    private lateinit var spinnerDeuteranotopia: Spinner
    private lateinit var slider: Slider
    private lateinit var voltar: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        voltar = findViewById(R.id.ImageVoltar)
        voltar.setOnClickListener {
            startActivity(Intent(this, SobreActivity::class.java))
            finish()
        }

        // Inicializando os Spinners
        spinnerIdioma = findViewById(R.id.spinnerIdioma)
        spinnerContrasteTela = findViewById(R.id.spinnerContrasteTela2)
        spinnerDeuteranotopia = findViewById(R.id.spinnerContrasteTela)

        // Configurando o Spinner para deuteranotopia
        val deuteranotopia = arrayOf("Desativado", "Ativado")
        val adapterDeute = ArrayAdapter(this, android.R.layout.simple_spinner_item, deuteranotopia)
        adapterDeute.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDeuteranotopia.adapter = adapterDeute

        // Inicializando o Slider
        slider = findViewById(R.id.slider)

        // Configurando o Spinner para Contraste de Tela
        val contrastesTela = arrayOf("Claro", "Escuro")
        val adapterContrasteTela =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, contrastesTela)
        adapterContrasteTela.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerContrasteTela.adapter = adapterContrasteTela

        // Configurando a interação do Slider para alterar o tamanho do texto
        slider.addOnChangeListener { _, value, _ ->
            FontSizeManager.fontSize = value.toInt() // Atualiza o tamanho global
            FontSizeManager.updateFontSize(findViewById<ViewGroup>(R.id.main)) // Aplica na tela atual
        }

        // Lógica para alternar entre os temas Claro e Escuro
        spinnerContrasteTela.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: android.view.View?,
                position: Int,
                id: Long
            ) {
                val contrasteSelecionado = parent?.getItemAtPosition(position).toString()
                when (contrasteSelecionado) {
                    "Claro" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    "Escuro" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Lógica para alternar o modo de Deuteranotopia
        spinnerDeuteranotopia.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: android.view.View?,
                position: Int,
                id: Long
            ) {
                val deuteSelecionado = parent?.getItemAtPosition(position).toString()
                when (deuteSelecionado) {
                    "Ativado" -> applyDeuteranotopia(true)  // Ativa o modo deuteranotopia
                    "Desativado" -> applyDeuteranotopia(false)  // Desativa
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Aplica o tamanho de fonte na tela
        FontSizeManager.updateFontSize(findViewById(R.id.main))
    }

    // Método para aplicar as cores de deuteranotopia
    private fun applyDeuteranotopia(ativado: Boolean) {
        val layout = findViewById<ViewGroup>(R.id.main)

        if (ativado) {
            // Aplicar cores para Deuteranotopia (contraste mais alto)
            layout.setBackgroundColor(resources.getColor(R.color.deuteranotopia_background)) // Fundo
            // Outros ajustes de cores de texto e botões podem ser feitos aqui também
        } else {
            layout.setBackgroundColor(0)
        }
    }
}
