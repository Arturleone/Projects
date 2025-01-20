package com.example.splashfadein

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Calendar

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val textView7 = findViewById<TextView>(R.id.textView7)
        val textView6 = findViewById<TextView>(R.id.textView6)
        val userName = intent.getStringExtra("username")
        val email = intent.getStringExtra("email")
        val text = findViewById<TextView>(R.id.textView5)
        text.text = "${updateGreeting()}, $userName"

        textView6.text = userName

        textView7.text = email
        countDown(30000) //inserir milisegundos desejados
    }

    //função para o "Onclick" do imageview
    fun Logout(view: View) {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    //Função para puxar o horário
    private fun updateGreeting(): String {
        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val greeting = when {
            hourOfDay < 12 -> "Bom dia"
            hourOfDay < 18 -> "Boa tarde"
            else -> "Boa noite"
        }

        val userName = "Usuário" //name do usuário
        return greeting
    }

    //Função para iniciar cronometro
    private fun countDown(millis: Long) {
        val tvCountdown = findViewById<TextView>(R.id.cronometro)

        object : CountDownTimer(millis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tvCountdown.text = String.format(
                    "%02d:%02d:%02d",
                    millisUntilFinished / 3600000, // Horas
                    (millisUntilFinished % 3600000) / 60000, // Minutos
                    (millisUntilFinished % 60000) / 1000 // Segundos
                )
            }

            override fun onFinish() {
                AlertDialog.Builder(this@Home)
                    .setTitle("FIM DE TEMPO")
                    .setMessage("O tempo limite foi atingido. Escolha uma ação:")
                    .setPositiveButton("Fechar App") { _, _ ->
                        finishAffinity()
                        System.exit(0)// Fecha o app
                    }
                    .setNegativeButton("Voltar ao Login") { _, _ ->
                        // Navegar para a tela de login
                        startActivity(Intent(this@Home, LoginActivity::class.java))
                        finish() // Fecha a tela atual
                    }
                    .setCancelable(false)
                    .show()
                tvCountdown.text = "00:00:00"
            }
        }.start()
    }

}