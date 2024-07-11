package com.example.bloodapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import com.example.bloodapp.R
import com.example.bloodapp.databinding.ActivityMainBinding
import com.example.bloodapp.ui.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase Authentication örneğini al
        auth = FirebaseAuth.getInstance()

        // Kullanıcı oturum kontrolü
        val currentUser = auth.currentUser
        if (currentUser == null) {
            // Kullanıcı oturum açmamışsa, LoginActivity'ye yönlendir
            startActivity(Intent(this, LoginActivity::class.java))
            finish()  // Bu aktiviteyi kapat
            return
        }

        // NavController'ı ve ilgili fragmentları bul
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
        val navController = navHostFragment!!.findNavController()

        // Alt menüyü oluştur ve NavController ile ilişkilendir
        val popupMenu = PopupMenu(this, null)
        popupMenu.inflate(R.menu.bottom_menu)
        binding.bottomBar.setupWithNavController(popupMenu.menu, navController)

        // NavController dinleyicisi ekleyerek başlık güncellemeleri yap
        navController.addOnDestinationChangedListener(object :
            NavController.OnDestinationChangedListener {
            override fun onDestinationChanged(
                controller: NavController,
                destination: NavDestination,
                arguments: Bundle?,
            ) {
                // Hedef fragmenta göre başlık güncelle
                title = when (destination.id) {
                    R.id.homeFragment -> "Anasayfa"
                    R.id.profileFragment -> "Profil"
                    R.id.feedbackFragment -> "Sıkça Sorulan Sorular"
                    R.id.mapFragment -> "Harita"
                    else -> "Kan Bağışı"
                }
            }
        })
    }
}