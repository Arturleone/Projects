package com.example.splashfadein

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class SplashScreen : AppCompatActivity() {

    private val tempoExibicaoSplash = 3000L
    private val tempoExibicaoSplashPrimeiraVez = 10000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        val textView = findViewById<TextView>(R.id.textview)

        aplicarAnimacoesNoTexto(textView)

        val duracao = if (verificarSePrimeiraVez()) tempoExibicaoSplashPrimeiraVez else tempoExibicaoSplash

        Handler().postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, duracao)
    }

    private fun aplicarAnimacoesNoTexto(textView: TextView) {
        aplicarAnimacaoDeAparecimentoGradual(textView)
        iniciarAnimacaoDeGradienteNoTexto(textView)
    }

    private fun aplicarAnimacaoDeAparecimentoGradual(textView: TextView) {
        val fadeIn = AlphaAnimation(0.0f, 1.0f).apply {
            duration = 2000
            fillAfter = true
        }
        textView.startAnimation(fadeIn)
    }

    private fun iniciarAnimacaoDeGradienteNoTexto(textView: TextView) {
        val conjuntosDeCores = listOf(
            intArrayOf(R.color.gradient_color_1, R.color.gradient_color_2, R.color.gradient_color_3),
            intArrayOf(R.color.gradient_color_3, R.color.gradient_color_1, R.color.gradient_color_2),
            intArrayOf(R.color.gradient_color_2, R.color.gradient_color_3, R.color.gradient_color_1)
        )

        var indiceConjuntoDeCorAtual = 0
        val handler = Handler(Looper.getMainLooper())
        val atualizadorDeGradiente = object : Runnable {
            override fun run() {
                val largura = textView.width.toFloat()
                val shader = LinearGradient(0f, 0f, largura, 0f,
                    conjuntosDeCores[indiceConjuntoDeCorAtual].map {
                        ContextCompat.getColor(this@SplashScreen, it)
                    }.toIntArray(),
                    null, Shader.TileMode.CLAMP)
                textView.paint.shader = shader
                textView.invalidate()

                indiceConjuntoDeCorAtual = (indiceConjuntoDeCorAtual + 1) % conjuntosDeCores.size
                handler.postDelayed(this, 500)
            }
        }
        handler.post(atualizadorDeGradiente)
    }

    private fun verificarSePrimeiraVez(): Boolean {
        val sharedPreferences = getSharedPreferences("", MODE_PRIVATE)
        val ePrimeiraVez = sharedPreferences.getBoolean("firstTime", true)
        if (ePrimeiraVez) {
            sharedPreferences.edit().putBoolean("firstTime", false).apply()
        }
        return ePrimeiraVez
    }
}
