package com.example.modulo8

import android.animation.ObjectAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SobreActivity : AppCompatActivity() {
    private lateinit var voltar: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sobre)
        FontSizeManager.updateFontSize(findViewById<ViewGroup>(R.id.another_main))
        voltar = findViewById(R.id.LayoutVoltar)
        voltar.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        val linearLayout = findViewById<LinearLayout>(R.id.linearLayout2)
        val circulo = findViewById<ImageView>(R.id.logoanimator)
        linearLayout.setOnClickListener{
            val fadeOut = ObjectAnimator.ofFloat(circulo, "alpha", 1f, 0f) // Torna invisível
            val fadeIn = ObjectAnimator.ofFloat(circulo, "alpha", 0f, 1f)  // Torna visível novamente

            // Define a duração das animações
            fadeOut.duration = 200 // 200ms para desaparecer
            fadeIn.duration = 200  // 200ms para aparecer

            // Cria um AnimatorSet para rodar as animações uma após a outra
            val animatorSet = android.animation.AnimatorSet()
            animatorSet.playSequentially(fadeOut, fadeIn) // Executa uma animação e depois a outra
            animatorSet.start() // Inicia a animação
        }

        val twitter = findViewById<ImageView>(R.id.twitter)
        twitter.setOnClickListener {
            val uri = Uri.parse("https://X.com")
            val intent = Intent(Intent.ACTION_SEND, uri)
            startActivity(intent)
        }

        val face = findViewById<ImageView>(R.id.face)
        face.setOnClickListener {
            val uri = Uri.parse("https://facebook.com")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        val insta = findViewById<ImageView>(R.id.insta)
        insta.setOnClickListener {
            val uri = Uri.parse("https://instagram.com")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        val youtube = findViewById<ImageView>(R.id.youtube)
        youtube.setOnClickListener {
            val uri = Uri.parse("https://youtube.com")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)

        }


    }
}



