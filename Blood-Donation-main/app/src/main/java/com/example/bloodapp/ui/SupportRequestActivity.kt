package com.example.bloodapp.ui

import android.os.Bundle
import android.widget.EditText
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.bloodapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import android.widget.Toast

class SupportRequestActivity : AppCompatActivity() {

    private lateinit var editTextSupportRequest: EditText
    private lateinit var buttonSubmitRequest: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userToken: String // Kullanıcının FCM token'ını saklamak için değişken

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support_request)

        // Gerekli bileşenleri tanımla
        editTextSupportRequest = findViewById(R.id.editTextSupportRequest)
        buttonSubmitRequest = findViewById(R.id.buttonSubmitRequest)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Kullanıcının cihazına özgü FCM token'ını al
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                userToken = task.result // Kullanıcının cihazına özgü token'ı al
            } else {
                Toast.makeText(this, "FCM tokeni alınamadı", Toast.LENGTH_SHORT).show()
            }
        }

        // Destek isteğini gönder butonuna tıklama olayı ekle
        buttonSubmitRequest.setOnClickListener {
            val supportRequest = editTextSupportRequest.text.toString()
            if (supportRequest.isNotEmpty()) {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    val email = currentUser.email
                    val userId = currentUser.uid
                    val request = hashMapOf(
                        "email" to email,
                        "message" to supportRequest,
                        "timestamp" to System.currentTimeMillis(),
                        "userToken" to userToken // Kullanıcının FCM token'ını ekle
                    )

                    // Firestore veritabanına destek talebini ekle
                    firestore.collection("supportRequests")
                        .add(request)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Destek talebiniz gönderildi.", Toast.LENGTH_LONG).show()
                            finish() // Aktiviteyi kapat
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Hata: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                } else {
                    Toast.makeText(this, "Kullanıcı girişi yapılmadı.", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Lütfen destek talebinizi girin.", Toast.LENGTH_LONG).show()
            }
        }
    }
}
