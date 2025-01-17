package com.example.modulo6

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle

import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationProvider: FusedLocationProviderClient

    private val pontosTuristicos = mutableListOf(
        "Shopping Tacaruna",
        "Veneza Water Park",
        "Moreno PE"
    )

    private val pontosTuristicosFiltrados = mutableListOf<String>().apply {
        addAll(pontosTuristicos) // Inicializando com todos os pontos turísticos
    }

    private val detalhesPontos = mapOf(
        "Veneza Water Park" to Pair(
            LatLng(-7.819056, -34.914172), // Coordenadas do ponto
            "R. Arlindo Menezes, 240 - Maria Farinha, Paulista - PE, 53427-610"
        ),
        "Shopping Tacaruna" to Pair(
            LatLng(-8.038157, -34.871584), // Coordenadas do ponto
            "Av. Gov. Agamenon Magalhães, 153 - Santo Amaro, Recife - PE, 50110-920"
        ),
        "Moreno PE" to Pair(
            LatLng(-7.0, 1.3),
            "bolota"
        )
    )

    private val solicitarPermissao =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { concedida ->
            if (concedida) {
                Log.d("MainActivity", "Permissão concedida")
                ativarLocalizacao()
            } else {
                Log.d("MainActivity", "Permissão negada")
                Toast.makeText(this, "Permissão negada", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("MainActivity", "Iniciando MainActivity")

        // Inicializando o FusedLocationProviderClient
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
        Log.d("MainActivity", "FusedLocationProviderClient inicializado")

        // Configurando o mapa
        val fragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        fragment.getMapAsync(this)
        Log.d("MainActivity", "Mapa configurado e aguardando callback")

        // Configurando o ListView
        val listView: ListView = findViewById(R.id.listview)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, pontosTuristicosFiltrados)
        listView.adapter = adapter
        Log.d("MainActivity", "ListView configurado")

        // Configurando o EditText para busca
        val editTextBusca: EditText = findViewById(R.id.editTextText)
        editTextBusca.addTextChangedListener { newText ->
            Log.d("MainActivity", "Texto alterado na pesquisa: $newText")
            filtrarPontosTuristicos(newText.toString(), adapter)
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val ponto = adapter.getItem(position) ?: return@setOnItemClickListener
            Log.d("MainActivity", "Ponto selecionado: $ponto")
            exibirDetalhesPonto(ponto)
        }

        verificarPermissao()
    }

    private fun filtrarPontosTuristicos(query: String, adapter: ArrayAdapter<String>) {
        Log.d("MainActivity", "Filtrando pontos turísticos com a consulta: $query")

        // Verifica se a consulta está vazia. Se estiver, exibe todos os pontos turísticos.
        val pontosFiltrados = if (query.isEmpty()) {
            Log.d("MainActivity", "Nenhum filtro aplicado, mostrando todos os pontos turísticos.")
            pontosTuristicos // Exibe todos os pontos quando a consulta está vazia
        } else {
            pontosTuristicos.filter {
                it.contains(query, ignoreCase = true) // Filtra com base no texto de busca
            }
        }

        Log.d("MainActivity", "Número de pontos filtrados: ${pontosFiltrados.size}")
        pontosTuristicosFiltrados.clear() // Limpa a lista de filtrados
        pontosTuristicosFiltrados.addAll(pontosFiltrados) // Atualiza a lista filtrada
        adapter.notifyDataSetChanged() // Notifica o adapter sobre a mudança
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val localInicial = LatLng(-8.05, -34.88) // Coordenadas iniciais do mapa
        Log.d("MainActivity", "Mapa carregado, movendo câmera para coordenadas iniciais")
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(localInicial, 12f))
        ativarLocalizacao()
    }

    private fun verificarPermissao() {
        Log.d("MainActivity", "Verificando permissão de localização")
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("MainActivity", "Permissão não concedida, solicitando")
            solicitarPermissao.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            Log.d("MainActivity", "Permissão já concedida")
        }
    }

    private fun ativarLocalizacao() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("MainActivity", "Ativando localização")
            mMap.isMyLocationEnabled = true
        } else {
            Log.d("MainActivity", "Não foi possível ativar a localização: permissão ausente")
        }
    }

    private fun exibirDetalhesPonto(ponto: String) {
        val (coordenadas, endereco) = detalhesPontos[ponto] ?: return
        Log.d("MainActivity", "Exibindo detalhes para o ponto: $ponto")

        val dialogView = layoutInflater.inflate(R.layout.dialog_detalhes_ponto, null)
        val textViewEndereco = dialogView.findViewById<android.widget.TextView>(R.id.textViewEndereco)
        val buttonNavegar = dialogView.findViewById<android.widget.Button>(R.id.buttonAcao)
        val buttonCheckIn = dialogView.findViewById<android.widget.Button>(R.id.button2)

        buttonCheckIn.setOnClickListener{
            Log.d("MainActivity", "Realizando check-in para $ponto")
            startActivity(Intent(this@MainActivity, PontosActivity::class.java))
        }

        textViewEndereco.text = endereco

        buttonNavegar.setOnClickListener {
            Log.d("MainActivity", "Adicionando marcador no mapa para $ponto")
            mMap.addMarker(MarkerOptions().position(coordenadas).title(ponto))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordenadas, 15f))
            Toast.makeText(this, "Marcador adicionado no mapa para $ponto", Toast.LENGTH_SHORT).show()
        }

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle(ponto)
            .setPositiveButton("Fechar") { dialog, _ ->
                Log.d("MainActivity", "Fechando diálogo para $ponto")
                dialog.dismiss()
            }
            .show()
    }
}
