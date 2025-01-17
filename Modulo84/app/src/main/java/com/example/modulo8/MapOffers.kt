package com.example.modulo8

import android.Manifest
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback

class MapOffers : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fused: FusedLocationProviderClient
    private lateinit var bola: LocationRequest
    private lateinit var bola2: LocationCallback


    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        concebida -> if(concebida){
            ativarMinhaLocalizacao()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_map_offers)



    }

    fun ativarMinhaLocalizacao(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == 0) {
            mMap.isMyLocationEnabled = true
            fused.requestLocationUpdates(bola, bola2, mainLooper)

        }
    }

    override fun onMapReady(p0: GoogleMap) {

    }
}