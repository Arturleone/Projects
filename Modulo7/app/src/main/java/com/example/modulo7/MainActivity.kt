package com.example.modulo7

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.Manifest


class MainActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var imageView: ImageView
    private val CAMERA_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Se não foi concedida, solicitar permissão
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
        }

        database = FirebaseDatabase.getInstance().reference

        // Referenciar a ImageView
        imageView = findViewById(R.id.imageView5)

        // Configurar a ação de clique para abrir a câmera
        imageView.setOnClickListener {
            abrirCamera()
        }

        val enviarButton = findViewById<Button>(R.id.button2)
        enviarButton.setOnClickListener {
            salvarAvaliacao()
        }

        val voltarButton = findViewById<Button>(R.id.button)
        voltarButton.setOnClickListener {
            startActivity(Intent(this, com.example.modulo7.ListActivity::class.java))
        }
    }

    // Função para abrir a câmera
    private fun abrirCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    // Salvar avaliação no Firebase
    private fun salvarAvaliacao() {
        // Capturar os valores do formulário
        val nomeDoLocal = findViewById<EditText>(R.id.nomelocal).text.toString()
        val nomeDoUsuario = findViewById<EditText>(R.id.nomeusuario).text.toString()
        val cidade = findViewById<EditText>(R.id.cidade).text.toString()
        val comentario = findViewById<EditText>(R.id.comentario).text.toString()
        val rating = findViewById<RatingBar>(R.id.ratingBar).rating

        // Criar objeto de avaliação
        val avaliacao = mapOf(
            "nomeDoLocal" to nomeDoLocal,
            "nomeDoUsuario" to nomeDoUsuario,
            "cidade" to cidade,
            "comentario" to comentario,
            "rating" to rating,
            "status" to "Pendente"
        )

        // Salvar no Firebase
        val avaliacaoRef = database.child("avaliacoes").push()
        avaliacaoRef.setValue(avaliacao)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Avaliação salva com sucesso!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, com.example.modulo7.ListActivity::class.java))
                } else {
                    Toast.makeText(this, "Erro ao salvar avaliação: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Permissão para a câmera
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissão concedida
                Toast.makeText(this, "Permissão de câmera concedida", Toast.LENGTH_SHORT).show()
            } else {
                // Permissão negada
                Toast.makeText(this, "Permissão de câmera negada", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
