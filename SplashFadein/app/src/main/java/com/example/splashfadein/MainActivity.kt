package com.example.splashfadein

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configurando animação de Fade In
        val textView = findViewById<TextView>(R.id.textview)
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        textView.startAnimation(fadeIn)

        // Configurando animação de troca de gradiente
        val gradientAnimator = ObjectAnimator.ofArgb(
            textView,
            "textColor",
            Color.RED,
            Color.BLUE,
            Color.GREEN
        )
        gradientAnimator.duration = 3000
        gradientAnimator.repeatCount = ObjectAnimator.INFINITE
        gradientAnimator.repeatMode = ObjectAnimator.REVERSE
        gradientAnimator.start()

        // Espera de 3 segundos antes de ir para a próxima Activity
        textView.postDelayed({
            startActivity(Intent(this, Home::class.java))
            finish()
        }, 3000)
    }
}
