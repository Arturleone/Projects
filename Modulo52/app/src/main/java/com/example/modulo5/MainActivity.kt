package com.example.modulo5

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {


    private lateinit var editTextText: EditText
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var button: Button


    ////PEDIR PERMISSÃO PARA USAR A LOCALIZAÇÃO

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("MainActivity", "Permissão de localização concedida.")
            enableMyLocation()
        } else {
            Log.w("MainActivity", "Permissão de localização negada.")
            Toast.makeText(this, "Permissão de localização negada.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("MainActivity", "onCreate chamado.")
        setContentView(R.layout.activity_main)

        // Inicializar FusedLocationProviderClient E PUXAR A LOCALIZAÇÂO DO USUARIO
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        editTextText = findViewById(R.id.editTextText)
        button = findViewById(R.id.button)
        button.isEnabled = false

        rodarMaps()

        alterarButton()

    }

    //ALTERAR BOTÂOOOOOOOOOOOOOOOOOOOO
    fun alterarButton() {
        editTextText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Não é necessário implementar nada aqui neste caso
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Habilita ou desabilita o botão com base no conteúdo do EditText
                button.isEnabled = !s.isNullOrEmpty()
            }

            override fun afterTextChanged(s: Editable?) {
                // Não é necessário implementar nada aqui neste caso
            }
        })
    }






    //////RODAR O MAPA, SÒ NECESSARIO ISSO PARA RODAR E FUNCIONAR TUDO
    fun rodarMaps() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this) // Passa o callback para o mapa
        Log.d("MainActivity", "Solicitado mapa para inicialização.")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.i("MainActivity", "onMapReady chamado.")
        mMap = googleMap

//        // Adiciona um marcador em uma coordenada
//        val rioDeJaneiro = LatLng(-22.9068, -43.1729)
//        mMap.addMarker(MarkerOptions().position(rioDeJaneiro).title("Marker in Rio de Janeiro"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(rioDeJaneiro, 10f))

        checkLocationPermission()
    }




    ///DESCOBRIR LOCALIZAÇÃO
    private fun checkLocationPermission() {
        Log.d("MainActivity", "Verificando permissão de localização.")
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.i("MainActivity", "Permissão de localização já concedida.")
                enableMyLocation()
            }
            else -> {
                Log.w("MainActivity", "Permissão de localização não concedida. Solicitando...")
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }



    ///HABILITAR A LOCALIZAÇÃO DO USUARIO
    private fun enableMyLocation() {
        Log.d("MainActivity", "Tentando habilitar a localização do usuário.")
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            getCurrentLocation()
        } else {
            Log.w("MainActivity", "Permissão de localização ainda não concedida.")
        }
    }

    ///PUXAR A LOCALIDADE ATUAL DO USUARIO
    private fun getCurrentLocation() {
        Log.d("MainActivity", "Obtendo localização atual do usuário.")
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("MainActivity", "Permissões de localização não concedidas.")
            return
        }

        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                Log.d("MainActivity", "Localização atual: $currentLatLng")

                // Adiciona marcador na localização atual
                mMap.addMarker(MarkerOptions().position(currentLatLng).title("Você está aqui!"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
            } else {
                Log.w("MainActivity", "Não foi possível obter a localização atual.")
                Toast.makeText(this, "Não foi possível obter sua localização.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Log.e("MainActivity", "Erro ao obter a localização: ${e.message}")
        }
    }
    ///// JÁ ESTÁ FUNCIONANDO O MAPA E PUXAR A LOCALIZAÇÃO ATUAL DO USUÁRIO
    ////APARTIR DAQUUI É SO IMPLEMENTOS





}
