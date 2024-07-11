package com.example.bloodapp.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.bloodapp.databinding.ActivityForgotBinding
import com.example.bloodapp.utils.Config
import com.google.firebase.auth.FirebaseAuth

// ForgotActivity sınıfı, şifremi unuttum ekranını yönetir
class ForgotActivity : AppCompatActivity() {

    lateinit var binding: ActivityForgotBinding // ViewBinding nesnesi
    lateinit var auth: FirebaseAuth // FirebaseAuth nesnesi

    // Aktivite oluşturulurken çağrılır
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide() // Üst çubuğu gizler

        auth = FirebaseAuth.getInstance() // FirebaseAuth örneğini alır

        // Geri gitme düğmesine tıklanıldığında, LoginActivity'ye yönlendirir
        binding.usergoBack.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // Şifre sıfırlama düğmesine tıklanıldığında çağrılır
        binding.forgotPassword.setOnClickListener {
            val email = binding.userEmail.text.toString()

            // E-posta alanı boşsa hata mesajı gösterir
            if (email.isEmpty()) {
                binding.userEmail.setError("Lütfen e-mail adresinizi girin.")
                binding.userEmail.requestFocus()
            } else {
                // Firebase ile şifre sıfırlama e-postası gönderir
                auth.sendPasswordResetEmail(email).addOnCompleteListener {
                    Config.showDialog(this) // Yükleniyor diyaloğunu gösterir
                    if (it.isSuccessful) {
                        Toast.makeText(this, "E-mail adresinizi kontrol edin.", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish() // Aktiviteyi bitirir
                    } else {
                        Config.hideDialog() // Yükleniyor diyaloğunu gizler
                        Toast.makeText(this, it.exception!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}