package com.example.podeapagardepois

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        val textView: TextView = findViewById(R.id.bolonha)
        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.duration = 5000
        textView.startAnimation(fadeIn)

        val timer: Long = if (verificarDownload()) 6000L else 3000L

        Handler().postDelayed({
            val intent = Intent(this, HOme::class.java)
            startActivity(intent)
            finish()
        }, timer)
    }

    private fun verificarDownload(): Boolean {
        val sharedPreferences: SharedPreferences = getSharedPreferences("FirstApp", MODE_PRIVATE)
        val firstAppDownload = sharedPreferences.getBoolean("firstAppDownload", true)
        if (firstAppDownload) {
            sharedPreferences.edit().putBoolean("firstAppDownload", false).apply()
            return true
        } else return false
    }
}
