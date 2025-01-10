package com.example.googlemaps_api_wsk

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var originLatLng: LatLng
    private lateinit var destinationLatLng: LatLng
    private lateinit var traceRouteButton: Button
    private lateinit var startNavigationButton: Button
    private lateinit var destinationInput: EditText
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Inicializar os elementos do layout
        destinationInput = findViewById(R.id.destination_input)
        traceRouteButton = findViewById(R.id.trace_route_button)
        startNavigationButton = findViewById(R.id.start_navigation_button)

        // Inicializar o botão "Traçar Rota" como desabilitado
        traceRouteButton.isEnabled = false

        // Configurar o mapa
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Inicializar o cliente de localização
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Configurar o botão de traçar rota
        traceRouteButton.setOnClickListener {
            val destination = destinationInput.text.toString()
            Log.d("MainActivity", "Destino digitado: $destination")
            if (destination.isNotEmpty()) {
                getCoordinatesAndDrawRoute(destination)
            } else {
                Toast.makeText(this, "Digite um destino", Toast.LENGTH_SHORT).show()
            }
        }

        // Iniciar navegação
        startNavigationButton.setOnClickListener {
            val navigationUri = Uri.parse("google.navigation:q=${destinationLatLng.latitude},${destinationLatLng.longitude}")
            val navigationIntent = Intent(Intent.ACTION_VIEW, navigationUri)
            navigationIntent.setPackage("com.google.android.apps.maps")
            startActivity(navigationIntent)
        }

        // Adicionar um TextWatcher no EditText para habilitar o botão quando houver texto
        destinationInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, before: Int, after: Int) {
                // Não é necessário implementar, mas é parte da interface.
            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, after: Int) {
                // Quando o texto for alterado, verifica se o campo não está vazio
                traceRouteButton.isEnabled = charSequence?.isNotEmpty() == true
            }

            override fun afterTextChanged(editable: Editable?) {
                // Não é necessário implementar, mas é parte da interface.
            }
        })
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.isMyLocationEnabled = true

        // Obter a localização atual do usuário
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                originLatLng = LatLng(location.latitude, location.longitude)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(originLatLng, 12f))
                googleMap.addMarker(MarkerOptions(). position(originLatLng).title("Você está aqui"))
                Log.d("MainActivity", "Localização do usuário: $originLatLng")
            } else {
                Log.d("MainActivity", "Falha ao obter localização do usuário.")
            }
        }
    }

    private fun getCoordinatesAndDrawRoute(destination: String) {
        // Aqui você deve usar a geolocalização para converter o endereço digitado em coordenadas
        val apiKey = "AIzaSyBavrBMWZ95acGkTjCXsVN1gCXfVOVJQGw"
        val geocodeUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=$destination&key=$apiKey"
        Log.d("MainActivity", "Consultando geocode para: $destination")

        Thread {
            val client = OkHttpClient()
            val request = Request.Builder().url(geocodeUrl).build()
            val response = client.newCall(request).execute()
            val responseData = response.body?.string()

            Log.d("MainActivity", "Resposta da geocodificação: $responseData")

            responseData?.let {
                val jsonObject = JSONObject(it)
                val results = jsonObject.getJSONArray("results")
                if (results.length() > 0) {
                    val location = results.getJSONObject(0)
                        .getJSONObject("geometry")
                        .getJSONObject("location")
                    val lat = location.getDouble("lat")
                    val lng = location.getDouble("lng")
                    destinationLatLng = LatLng(lat, lng)

                    Log.d("MainActivity", "Coordenadas do destino: $destinationLatLng")

                    // Agora, traçar a rota do usuário até o destino
                    val directionsUrl = "https://maps.googleapis.com/maps/api/directions/json?origin=${originLatLng.latitude},${originLatLng.longitude}&destination=$destination&key=$apiKey"
                    val directionsRequest = Request.Builder().url(directionsUrl).build()
                    val directionsResponse = client.newCall(directionsRequest).execute()
                    val directionsData = directionsResponse.body?.string()

                    Log.d("MainActivity", "Resposta da direção: $directionsData")

                    directionsData?.let { data ->
                        val directionsJson = JSONObject(data)
                        val routes = directionsJson.getJSONArray("routes")
                        if (routes.length() > 0) {
                            val points = routes.getJSONObject(0)
                                .getJSONObject("overview_polyline")
                                .getString("points")
                            val decodedPoints = com.google.maps.android.PolyUtil.decode(points)

                            runOnUiThread {
                                googleMap.clear()
                                googleMap.addMarker(MarkerOptions().position(originLatLng).title("Origem"))
                                googleMap.addMarker(MarkerOptions().position(destinationLatLng).title("Destino"))
                                googleMap.addPolyline(PolylineOptions().addAll(decodedPoints).color(R.color.black))

                                // Habilitar o botão de iniciar navegação
                                startNavigationButton.visibility = Button.VISIBLE
                                Log.d("MainActivity", "Rota traçada com sucesso.")
                            }
                        } else {
                            Log.d("MainActivity", "Nenhuma rota encontrada.")
                        }
                    }
                } else {
                    Log.d("MainActivity", "Nenhuma coordenada encontrada para o destino.")
                }
            } ?: run {
                Log.d("MainActivity", "Resposta da geocodificação vazia.")
            }
        }.start()
    }
}
