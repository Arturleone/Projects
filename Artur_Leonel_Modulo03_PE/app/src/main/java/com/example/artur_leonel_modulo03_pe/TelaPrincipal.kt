package com.example.artur_leonel_modulo03_pe

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.w3c.dom.Text
import java.util.Calendar

class TelaPrincipal : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_principal)
        horas(30000)

        val textView = findViewById<TextView>(R.id.textView3)
        textView.text = gettingCloud()

    }

    private fun timer(tempo: Long) {
        val count = findViewById<TextView>(R.id.countDown)

        object : CountDownTimer(tempo, 1000) {
            override fun onTick(tempo2: Long) {
                count.text = String.format("%02d:%02d:%02d",
                    tempo2 / 3600000,
                    (tempo2 % 3600000) / 60000,
                    (tempo2 % 60000) / 1000)
            }

            override fun onFinish() {
                AlertDialog.Builder(this@TelaPrincipal)
                    .setTitle("tempo")
                    .setMessage("tempo acabou, mano")
                    .setPositiveButton("login") {_, _ ->


                    }
                    .setNegativeButton("sair") {_,_ ->
                        finishAffinity()
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
            in 0..11 -> "bom dia"
            in 12..17 -> "boa tarde"
            else -> "boa noite"
        }

    }
    private fun horas(long: Long) {
        val papel = findViewById<TextView>(R.id.countDown)

        object: CountDownTimer(long, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                papel.text = String.format("%02d:%02d:%02d",
                    millisUntilFinished / 3600000,
                    (millisUntilFinished % 3600000) / 60000,
                    (millisUntilFinished % 60000) / 1000)
            }

            override fun onFinish() {
                AlertDialog.Builder(this@TelaPrincipal)
                    .setTitle("bolonha")
                    .setMessage("papel")
                    .setPositiveButton("bolonha") {_,_->

                    }
                    .setNegativeButton("bolonha") {_,_->

                    }
                    .show()
                papel.text = "00:00:00"
            }
        }
            .start()
    }




}