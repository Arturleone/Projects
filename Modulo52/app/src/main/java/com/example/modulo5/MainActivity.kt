package com.example.modulo5

import Point
import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback

import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class AtividadePrincipal : AppCompatActivity(), OnMapReadyCallback {

    // Configurações gerais do mapa e localização
    private lateinit var mapaGoogle: GoogleMap
    private lateinit var clienteLocalizacao: FusedLocationProviderClient
    private lateinit var requisicaoLocalizacao: LocationRequest
    private lateinit var retornoLocalizacao: LocationCallback
    private var marcadorLocalAtual: Marker? = null
    private var marcadorDestino: Marker? = null
    private val manipulador = Handler()
    private var tarefaPesquisa: Runnable? = null

    // Gerenciador de permissões para localização
    private val solicitacaoPermissao = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedida ->
        if (concedida) ativarMinhaLocalizacao() else mostrarToast("Permissão de localização negada.")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializando os serviços de localização e o mapa
        clienteLocalizacao = LocationServices.getFusedLocationProviderClient(this)
        configurarMapa()
        configurarServicosLocalizacao()
        configurarEntradaEndereco()

        // Configuração dos botões de controle
        val botaoIniciar = findViewById<Button>(R.id.button)
        val botaoCancelar = findViewById<Button>(R.id.button2)
        val campoTexto: EditText = findViewById(R.id.editTextText)

        botaoIniciar.setOnClickListener {
            mostrarToast("Viagem iniciada!")
            botaoIniciar.isEnabled = false
            botaoCancelar.isEnabled = true
            campoTexto.isEnabled = false
        }

        botaoCancelar.setOnClickListener {
            mostrarToast("Viagem cancelada!")
            botaoIniciar.isEnabled = true
            botaoCancelar.isEnabled = false
            campoTexto.isEnabled = true
        }
    }

    // --- Configuração do mapa ---
    private fun configurarMapa() {
        val fragmentoMapa = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        fragmentoMapa.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mapaGoogle = googleMap
        verificarPermissaoLocalizacao()
        adicionarPontosPredefinidos()
    }

    private fun adicionarPontosPredefinidos() {
        val pontos = listOf(
            Point.VenezaPark() to "Veneza Park",
            Point.ShoppingTacaruna() to "Shopping Tacaruna"
        )

        pontos.forEach { (localizacao, titulo) ->
            mapaGoogle.addMarker(
                MarkerOptions().position(LatLng(localizacao.latitude, localizacao.longitude)).title(titulo)
            )
        }
    }

    // --- Configuração de localização ---
    private fun configurarServicosLocalizacao() {
        requisicaoLocalizacao = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000L)
            .setMinUpdateIntervalMillis(1000L)
            .build()

        retornoLocalizacao = object : LocationCallback() {
            override fun onLocationResult(resultadoLocalizacao: LocationResult) {
                resultadoLocalizacao.locations.firstOrNull()?.let { atualizarLocalizacaoNoMapa(it) }
            }
        }
    }

    private fun atualizarLocalizacaoNoMapa(localizacao: Location) {
        val coordenadasAtuais = LatLng(localizacao.latitude, localizacao.longitude)
        marcadorLocalAtual = if (marcadorLocalAtual == null) {
            mapaGoogle.addMarker(MarkerOptions().position(coordenadasAtuais).title("Você está aqui!"))
        } else {
            marcadorLocalAtual?.apply { position = coordenadasAtuais }
        }
    }

    private fun ativarMinhaLocalizacao() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mapaGoogle.isMyLocationEnabled = true
            clienteLocalizacao.requestLocationUpdates(requisicaoLocalizacao, retornoLocalizacao, mainLooper)
        }
    }

    // --- Configuração de entrada de endereço ---
    private fun configurarEntradaEndereco() {
        val campoTexto: EditText = findViewById(R.id.editTextText)
        val botao: Button = findViewById(R.id.button)
        botao.isEnabled = false

        campoTexto.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                botao.isEnabled = !s.isNullOrEmpty()
                tarefaPesquisa?.let { manipulador.removeCallbacks(it) }

                tarefaPesquisa = Runnable {
                    s?.takeIf { it.isNotEmpty() }?.let {
                        obterLatLngDoEndereco(it.toString())?.let { coordenadas ->
                            atualizarMarcadorDestino(coordenadas, it.toString())
                        }
                    }
                }
                manipulador.postDelayed(tarefaPesquisa!!, 1000L)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun obterLatLngDoEndereco(endereco: String): LatLng? {
        return try {
            Geocoder(this).getFromLocationName(endereco, 1)?.firstOrNull()?.let {
                LatLng(it.latitude, it.longitude)
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun atualizarMarcadorDestino(coordenadas: LatLng, titulo: String) {
        marcadorDestino?.remove()
        marcadorDestino = mapaGoogle.addMarker(MarkerOptions().position(coordenadas).title(titulo))
        mapaGoogle.animateCamera(CameraUpdateFactory.newLatLngZoom(coordenadas, 15f))
    }

    // --- Gerenciamento de permissões ---
    private fun verificarPermissaoLocalizacao() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            ativarMinhaLocalizacao()
        } else {
            solicitacaoPermissao.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // --- Exibição de mensagens ---
    private fun mostrarToast(mensagem: String) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
    }
}
