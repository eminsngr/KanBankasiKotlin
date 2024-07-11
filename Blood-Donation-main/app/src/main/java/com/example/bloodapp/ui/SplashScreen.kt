package com.example.bloodapp.ui

import android.app.TaskStackBuilder
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.bloodapp.R
import com.example.bloodapp.ui.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class SplashScreen : AppCompatActivity() {

    lateinit var preferences: SharedPreferences
    lateinit var editor: Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        supportActionBar?.hide()

        // Önceki aktiviteleri temizle ve splash ekranını gizle
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        // SharedPreferences örneğini al ve düzenleyiciyi başlat
        preferences = getSharedPreferences("splash", MODE_PRIVATE)
        editor = preferences.edit()

        // Belirli bir süre sonra işlemleri gerçekleştir
        Handler(Looper.myLooper()!!).postDelayed(Runnable {

            // Oturum açmış bir kullanıcı yoksa
            if (FirebaseAuth.getInstance().currentUser == null) {
                // Splash ekranı gösterildikten sonra login ekranına yönlendir
                preferences.getBoolean("isMain", true)
                startActivity(Intent(this, LoginActivity::class.java))
                finish() // Splash ekranı kapat
            } else {
                // Kullanıcı oturum açmışsa
                // "isMain" anahtarını true olarak ayarla ve SharedPreferences'e kaydet
                editor.putBoolean("isMain", true)
                editor.apply()

                // Main aktivitesini başlat
                // Geriye dönüş işlemleri için TaskStackBuilder kullan
                TaskStackBuilder.create(this)
                    .addNextIntentWithParentStack(Intent(this, MainActivity::class.java))
                    .startActivities()
            }

        }, 1000) // 1000 milisaniye (1 saniye) sonra işlemleri gerçekleştir
    }
}
