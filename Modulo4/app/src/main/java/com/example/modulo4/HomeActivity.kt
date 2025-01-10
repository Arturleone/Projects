package com.example.modulo4

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment

import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        val framelayout = findViewById<FrameLayout>(R.id.container)

        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val layout = layoutInflater.inflate(R.layout.activity_home_fragment, null)
                    framelayout.removeAllViews()
                    framelayout.addView(layout)
                }
                R.id.nav_roteiro_personalizado -> {
                    val layout = layoutInflater.inflate(R.layout.activity_roteiro, null)
                    framelayout.removeAllViews()
                    framelayout.addView(layout)
//                    loadFragment(RoteiroFragment())
                }
                R.id.nav_viajante_gamificado -> {
                    val layout = layoutInflater.inflate(R.layout.activity_viajante, null)
                    framelayout.removeAllViews()
                    framelayout.addView(layout)
                }
                R.id.nav_configuracoes -> {
                    val layout = layoutInflater.inflate(R.layout.activity_configuracoes, null)
                    framelayout.removeAllViews()
                    framelayout.addView(layout)
                }
                R.id.nav_sair -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }
//
//    // Função para carregar os Fragments dinamicamente
//    private fun loadFragment(fragment: Fragment) {
//        // Trocar o conteúdo do container de acordo com o fragmento
//        val transaction = supportFragmentManager.beginTransaction()
//        transaction.replace(R.id.container, fragment) // Substituir o conteúdo do FrameLayout
//        transaction.addToBackStack(null) // Adicionar à pilha de navegação, para que o usuário possa voltar
//        transaction.commit()
//    }
}
