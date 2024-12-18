package com.example.modulo5

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: LatLng? = null
    private var currentLocationMarker: Marker? = null

    private val handler = Handler(Looper.getMainLooper())
    private var lastTypedTime = System.currentTimeMillis()
    private var checkTask: Runnable? = null

    private var lastSearchedDestination: String = ""

    private val apiKey = "AIzaSyAeYy7pns-XYVsx0xC5w0MSZ1J4fudrHsg" // Substitua pela sua API Key

    private lateinit var startButton: Button  // Referência ao botão "Iniciar Viagem"
    private lateinit var cancelButton: Button // Referência ao botão "Cancelar Viagem"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("MainActivity", "onCreate: Inicializando FusedLocationProviderClient e MapFragment")

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val destinationInput = findViewById<EditText>(R.id.destination_input)
        startButton = findViewById<Button>(R.id.trace_route_button)
        cancelButton = findViewById<Button>(R.id.cancel_trip_button)

        // Desabilitar o botão "Cancelar Viagem" inicialmente
        cancelButton.visibility = Button.GONE
        cancelButton.isEnabled = false

        // Desabilitar o botão "Iniciar Viagem" inicialmente
        startButton.isEnabled = false

        destinationInput.addTextChangedListener(object : SimpleTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                lastTypedTime = System.currentTimeMillis()
                startChecking() // Inicia a verificação da entrada de texto
            }
        })

        // Ação do botão "Iniciar Viagem"
        startButton.setOnClickListener {
            startButton.isEnabled = false
            startButton.text = "Encerrar Viagem"
            cancelButton.visibility = Button.VISIBLE
            cancelButton.isEnabled = true
            destinationInput.isEnabled = false
        }

        cancelButton.setOnClickListener {
            destinationInput.isEnabled = true
            startButton.isEnabled = true
            startButton.text = "Iniciar Viagem"
            cancelButton.visibility = Button.GONE
            cancelButton.isEnabled = false
            googleMap.clear()  // Limpa a rota, mas não a localização
            currentLocation?.let { current ->
                googleMap.addMarker(MarkerOptions().position(current).title("Você está aqui"))
            }
            destinationInput.clearFocus()
            destinationInput.text = null
        }
    }

    override fun onMapReady(map: GoogleMap) {
        Log.d("MainActivity", "onMapReady: Mapa pronto para uso")
        googleMap = map

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
            getCurrentLocation()
        } else {
            requestLocationPermission()
        }
    }

    private fun getCurrentLocation() {
        Log.d("MainActivity", "getCurrentLocation: Tentando obter localização atual")
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLocation = LatLng(location.latitude, location.longitude)
                    currentLocation?.let { current ->
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 15f))
                        if (currentLocationMarker == null) {
                            currentLocationMarker = googleMap.addMarker(MarkerOptions().position(current).title("Você está aqui"))
                        } else {
                            currentLocationMarker?.position = current
                        }
                    }
                }
            }
        } else {
            requestLocationPermission()
        }
    }

    private fun searchAndDrawRoute(destination: String) {
        Log.d("MainActivity", "searchAndDrawRoute: Iniciando busca pela rota para o destino: $destination")
        if (destination == lastSearchedDestination) return // Evita pesquisa desnecessária

        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            Log.d("MainActivity", "searchAndDrawRoute: Usando Geocoder para buscar o destino")
            val addresses = geocoder.getFromLocationName(destination, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val destinationLatLng = LatLng(addresses[0].latitude, addresses[0].longitude)

                currentLocation?.let { current ->
                    googleMap.clear()  // Limpa apenas as rotas antigas
                    googleMap.addMarker(MarkerOptions().position(current).title("Você está aqui"))
                    googleMap.addMarker(MarkerOptions().position(destinationLatLng).title("Destino"))
                    getRoute(current, destinationLatLng)
                    lastSearchedDestination = destination // Atualiza o destino pesquisado
                }
            } else {
                Toast.makeText(this, "Endereço não encontrado", Toast.LENGTH_SHORT).show()
                Log.d("MainActivity", "searchAndDrawRoute: Endereço não encontrado")
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Erro ao buscar endereço", e)
        }
    }

    private fun getRoute(origin: LatLng, destination: LatLng) {
        Log.d("MainActivity", "getRoute: Buscando rota de $origin para $destination")
        val url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=${origin.latitude},${origin.longitude}" +
                "&destination=${destination.latitude},${destination.longitude}" +
                "&mode=driving&key=$apiKey"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.connect()
                val stream = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonResponse = JSONObject(stream)

                val routes = jsonResponse.getJSONArray("routes")
                if (routes.length() > 0) {
                    val points = routes.getJSONObject(0)
                        .getJSONObject("overview_polyline")
                        .getString("points")
                    val decodedPoints = decodePolyline(points)

                    withContext(Dispatchers.Main) {
                        val polylineOptions = PolylineOptions()
                            .addAll(decodedPoints)
                            .color(ContextCompat.getColor(this@MainActivity, android.R.color.holo_blue_dark))
                            .width(10f)
                        googleMap.addPolyline(polylineOptions)
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                            LatLngBounds.builder().apply {
                                decodedPoints.forEach { include(it) }
                            }.build(), 100
                        ))

                        // Habilita o botão após desenhar a rota
                        startButton.isEnabled = true
                    }
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Erro ao obter rota", e)
            }
        }
    }

    private fun decodePolyline(encoded: String): List<LatLng> {
        Log.d("MainActivity", "decodePolyline: Decodificando polilinha")
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(lat / 1E5, lng / 1E5)
            poly.add(p)
        }

        return poly
    }

    private fun requestLocationPermission() {
        Log.d("MainActivity", "requestLocationPermission: Solicitando permissão de localização")
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            1
        )
    }

    private fun startChecking() {
        Log.d("MainActivity", "startChecking: Iniciando verificação da entrada de texto")
        checkTask?.let { handler.removeCallbacks(it) }

        checkTask = Runnable {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastTypedTime >= 1000) { // 1 segundo após o último caractere digitado
                val destinationInput = findViewById<EditText>(R.id.destination_input)
                val destination = destinationInput.text.toString()
                if (destination.isNotEmpty()) {
                    searchAndDrawRoute(destination)
                    stopChecking() // Para a verificação após processar a rota
                }
            } else {
                handler.postDelayed(checkTask!!, 300)
            }
        }

        handler.postDelayed(checkTask!!, 300)
    }

    private fun stopChecking() {
        Log.d("MainActivity", "stopChecking: Parando a verificação de entrada de texto.")
        checkTask?.let {
            handler.removeCallbacks(it)
            checkTask = null
        }
    }
}

abstract class SimpleTextWatcher : android.text.TextWatcher {
    override fun afterTextChanged(s: android.text.Editable?) {}
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
}