package com.example.artur_leonel_mod3

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        timer(30000)
        val nome = intent.getStringExtra("usuario")
        val email = intent.getStringExtra("email")
        val dr = intent.getStringExtra("dr")

        val textView = findViewById<TextView>(R.id.textView6)
        textView.text = "${gettingCloud()}, Usuário!"

    }

    private fun timer(tempo: Long) {
        val count = findViewById<TextView>(R.id.countDown)

        object : CountDownTimer(tempo, 1000) {
            override fun onTick(tempo2: Long) {
                count.text = String.format(
                    "%02d:%02d:%02d",
                    tempo2 / 3600000,
                    (tempo2 % 3600000) / 60000,
                    (tempo2 % 60000) / 1000
                )
            }

            override fun onFinish() {
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("TIMER")
                    .setMessage("tempo acabou")
                    .setPositiveButton("Fechar Aplicativo") { _, _ ->
                        finishAffinity()

                    }
                    .setNegativeButton("Voltar ao Login") { _, _ ->
                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        startActivity(intent)
                    }
                    .show()
                count.text = "00:00:00"
            }
        }
            .start()
    }

    private fun gettingCloud(): String {
        val calendario = Calendar.getInstance()
        val hora = calendario.get(Calendar.HOUR_OF_DAY)
        return when (hora) {
            in 0..11 -> "Bom Dia"
            in 12..17 -> "Boa Tarde"
            else -> "Boa Noite"
        }

    }
}




