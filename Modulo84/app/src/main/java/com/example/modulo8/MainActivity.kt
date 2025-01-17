package com.example.modulo8

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    private lateinit var stateSpinner: Spinner
    private lateinit var citySpinner: Spinner
    private lateinit var statesList: List<String>
    private lateinit var citiesMap: Map<String, List<String>>
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textView = findViewById<TextView>(R.id.textView)
        textView.setOnClickListener{
            startActivity(Intent(this, ListOffer::class.java))
            finish()
        }


        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
        val imageList = listOf(R.drawable.fachadatacaruna, R.drawable.maxresdefault, R.drawable.fachadatacaruna)
        viewPager.adapter = CarouselAdapter(imageList)
        TabLayoutMediator(tabLayout, viewPager) { tab, _ ->
        }.attach()

        startAutoSlide(imageList.size)

        stateSpinner = findViewById(R.id.spinner)
        citySpinner = findViewById(R.id.spinner2)

        // Carregar dados do JSON
        val estados = carregarEstadosECidades()

        // Processar os dados para estados e cidades
        val (states, cities) = parseStatesAndCities(estados)

        // Preencher o Spinner de estados
        val stateAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, states)
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        stateSpinner.adapter = stateAdapter

        // Ação ao selecionar um estado
        stateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: android.view.View?, position: Int, id: Long) {
                val selectedState = parentView.getItemAtPosition(position) as String
                val citiesForState = cities[selectedState] ?: emptyList()
                updateCitySpinner(citiesForState)
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // Do nothing
            }
        }
    }

    // Função para carregar os estados e cidades do arquivo JSON
    fun carregarEstadosECidades(): List<Estado> {
        // Abrir o arquivo JSON localizado na pasta res/raw
        val inputStream = resources.openRawResource(R.raw.cidadesestados)
        val reader = InputStreamReader(inputStream)

        // Usar o Gson para deserializar o JSON para o objeto EstadoWrapper
        val estadoWrapper = Gson().fromJson(reader, EstadoWrapper::class.java)
        reader.close()

        // Retornar a lista de estados
        return estadoWrapper.estados
    }

    private fun parseStatesAndCities(estados: List<Estado>): Pair<List<String>, Map<String, List<String>>> {
        val states = mutableListOf<String>()
        val cities = mutableMapOf<String, MutableList<String>>()

        // Iterar sobre os estados
        for (estado in estados) {
            states.add(estado.nome)

            // Iterar sobre as cidades, que agora são objetos Cidade
            val citiesForState = mutableListOf<String>()
            for (cidade in estado.cidades) {
                citiesForState.add(cidade.cidade) // Acessando o nome da cidade
            }

            cities[estado.nome] = citiesForState
        }

        return Pair(states, cities)
    }

    private fun updateCitySpinner(citiesList: List<String>) {
        val cityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, citiesList)
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        citySpinner.adapter = cityAdapter
    }

    private fun startAutoSlide(itemCount: Int) {
        val runnable = object : Runnable {
            override fun run() {
                val nextItem = (viewPager.currentItem + 1) % itemCount
                viewPager.setCurrentItem(nextItem, true)
                handler.postDelayed(this, 5000) // 5 seconds
            }
        }
        handler.postDelayed(runnable, 5000) // Start auto-slide
    }

}
