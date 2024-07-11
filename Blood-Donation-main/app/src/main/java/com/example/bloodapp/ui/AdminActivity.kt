package com.example.bloodapp.ui

import UserDetailActivity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.bloodapp.R

import com.google.firebase.firestore.FirebaseFirestore

class AdminActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var userListLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        db = FirebaseFirestore.getInstance()
        userListLayout = findViewById(R.id.userListLayout)

        // Kullanıcıları getir ve listele
        fetchAndDisplayUsers()
    }

    private fun fetchAndDisplayUsers() {
        // DonorResults tablosundaki userId'leri çek
        db.collection("DonorResults")
            .get()
            .addOnSuccessListener { donorResults ->
                val donorResultUserIds = donorResults.documents.map { it.getString("id") }.toSet()

                // Users tablosundaki kullanıcıları çek
                db.collection("users")
                    .get()
                    .addOnSuccessListener { users ->
                        for (user in users) {
                            val userId = user.id

                            // Eğer userId DonorResults tablosunda yoksa
                            if (!donorResultUserIds.contains(userId)) {
                                val userName = user.getString("name") ?: ""
                                val bloodGroup = user.getString("blood") ?: ""

                                // Her kullanıcı için bir label oluştur
                                val userLabel = TextView(this)
                                userLabel.text = "$userName - $bloodGroup"
                                userLabel.textSize = 16f
                                userLabel.setTextColor(Color.BLUE)
                                userLabel.setPadding(0, 8, 0, 8)

                                // Label'a tıklanınca kullanıcı detaylarını gösteren bir aktiviteyi başlat
                                userLabel.setOnClickListener {
                                    val intent = Intent(this, UserDetailActivity::class.java)
                                    intent.putExtra("userId", userId)
                                    startActivity(intent)
                                }

                                // Kullanıcı listesine label'ı ekle
                                userListLayout.addView(userLabel)
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("AdminActivity", "Kullanıcılar getirilemedi: ", exception)
                    }
            }
            .addOnFailureListener { exception ->
                Log.e("AdminActivity", "DonorResults getirilemedi: ", exception)
            }
    }
}
