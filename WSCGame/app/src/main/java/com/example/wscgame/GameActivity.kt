package com.example.wscgame

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class GameActivity : AppCompatActivity() {
    private var coins = 10
    private var gameDuration = 0
    private lateinit var playerName: String
    private var isInvincibilityMode = false
    private lateinit var backgroundMusic: MediaPlayer
    private var gameTimer: CountDownTimer? = null
    private var obstacleTimer: CountDownTimer? = null
    private var obstacles: MutableList<ImageView> = mutableListOf()
    private var score = 0

    // Variáveis para armazenar os estados antes da pausa
    private var playerPositionX = 0f
    private var playerPositionY = 0f
    private var obstaclePositions: MutableList<Pair<Float, Float>> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        playerName = intent.getStringExtra("PLAYER_NAME") ?: "Jogador"
        findViewById<TextView>(R.id.playerNameText).text = "$playerName!"

        backgroundMusic = MediaPlayer.create(this, R.raw.crhistman)

        startGame()

        findViewById<View>(R.id.pauseButton).setOnClickListener { pauseGame() }
        findViewById<View>(R.id.playButton).setOnClickListener { resumeGame() }

        findViewById<Button>(R.id.invincibilityButton).setOnClickListener {
            toggleInvincibility()
        }

        // Adicionando a funcionalidade de pulo do personagem
        findViewById<View>(R.id.playerImage).setOnClickListener {
            jumpCharacter()
        }

        // Iniciar a geração de obstáculos
        startObstacleGeneration()
    }

    private fun startGame() {
        gameDuration = 0
        val timerText = findViewById<TextView>(R.id.timerText)
        gameTimer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                gameDuration++
                timerText.text = "Tempo: $gameDuration"
            }

            override fun onFinish() {}
        }
        gameTimer?.start()
    }

    private fun startObstacleGeneration() {
        obstacleTimer = object : CountDownTimer(Long.MAX_VALUE, 2000) { // Gera obstáculos a cada 2 segundos
            override fun onTick(millisUntilFinished: Long) {
                spawnObstacle()
            }

            override fun onFinish() {}
        }
        obstacleTimer?.start()
    }

    private fun spawnObstacle() {
        val obstacle = ImageView(this)
        obstacle.setImageResource(R.drawable.obstaculo) // Sua imagem de obstáculo aqui

        // Usando ConstraintLayout.LayoutParams para configurar o tamanho e posição
        val layoutParams = ConstraintLayout.LayoutParams(200, 300) // Tamanho do obstáculo
        obstacle.layoutParams = layoutParams

        // Adiciona o obstáculo ao layout
        findViewById<ConstraintLayout>(R.id.constraintlayouut).addView(obstacle)

        // Definindo a posição inicial do obstáculo
        obstacle.translationX = 800f // posição inicial fora da tela
        val obstacleY = (0..600).random() // Posicionamento aleatório de obstáculos no eixo Y
        obstacle.translationY = obstacleY.toFloat()

        obstacles.add(obstacle)

        // Movimento do obstáculo
        obstacle.animate()
            .translationXBy(-1000f) // Move o obstáculo para a esquerda da tela
            .setDuration(3000)
            .withEndAction {
                obstacle.visibility = View.GONE
                obstacles.remove(obstacle)
            }
    }

    private fun pauseGame() {
        // Salvar a posição do personagem e obstáculos
        val playerImage = findViewById<ImageView>(R.id.playerImage)
        playerPositionX = playerImage.translationX
        playerPositionY = playerImage.translationY

        // Salvar as posições dos obstáculos
        obstaclePositions.clear()
        for (obstacle in obstacles) {
            obstaclePositions.add(Pair(obstacle.translationX, obstacle.translationY))
        }

        // Parar animações e temporizadores
        gameTimer?.cancel()
        obstacleTimer?.cancel()

        // Pausar o movimento do personagem
        playerImage.animate().cancel()

        // Esconder o botão de continuar
        findViewById<View>(R.id.playButton).visibility = View.VISIBLE
    }

    private fun resumeGame() {
        // Restaurar a posição do personagem
        val playerImage = findViewById<ImageView>(R.id.playerImage)
        playerImage.translationX = playerPositionX
        playerImage.translationY = playerPositionY

        // Restaurar as posições dos obstáculos
        for (i in obstacles.indices) {
            obstacles[i].translationX = obstaclePositions[i].first
            obstacles[i].translationY = obstaclePositions[i].second
        }

        // Reiniciar os temporizadores
        startGame()
        startObstacleGeneration()

        // Esconder o botão de continuar
        findViewById<View>(R.id.playButton).visibility = View.GONE
    }

    private fun toggleInvincibility() {
        isInvincibilityMode = !isInvincibilityMode
        if (isInvincibilityMode) {
            Toast.makeText(this, "Modo Invencível Ativado", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Modo Invencível Desativado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun jumpCharacter() {
        val playerImage = findViewById<ImageView>(R.id.playerImage)

        // Animação de pulo (subir e descer)
        playerImage.animate()
            .translationYBy(-300f) // Pular para cima (valor negativo)
            .setDuration(500) // Duração do pulo
            .withEndAction {
                // Voltar para baixo após o pulo
                playerImage.animate()
                    .translationYBy(300f) // Descer para a posição original
                    .setDuration(500)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        backgroundMusic.release()
    }
}

