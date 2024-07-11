package com.example.bloodapp.ui

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bloodapp.R
import com.example.bloodapp.adapter.SupportRequestAdapter
import com.example.bloodapp.model.SupportRequestModel
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class AdminFAQActivity : AppCompatActivity() {

    private lateinit var recyclerViewSupportRequests: RecyclerView
    private lateinit var editTextResponse: EditText
    private lateinit var buttonSubmitResponse: Button
    private lateinit var firestore: FirebaseFirestore

    private var selectedRequest: SupportRequestModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_faq)

        // Gerekli bileşenlerin referanslarını al
        recyclerViewSupportRequests = findViewById(R.id.recyclerViewSupportRequests)
        editTextResponse = findViewById(R.id.editTextResponse)
        buttonSubmitResponse = findViewById(R.id.buttonSubmitResponse)
        firestore = FirebaseFirestore.getInstance()

        // RecyclerView için düzen belirle (lineer düzen)
        recyclerViewSupportRequests.layoutManager = LinearLayoutManager(this)

        // Yardım taleplerini getir
        fetchSupportRequests()

        // Cevap gönderme butonunun tıklama olayını tanımla
        buttonSubmitResponse.setOnClickListener {
            val response = editTextResponse.text.toString()
            if (selectedRequest != null && response.isNotEmpty()) {
                respondToRequest(selectedRequest!!, response)
            } else {
                Toast.makeText(this, "Lütfen bir talep seçin ve bir cevap girin.", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Yardım taleplerini Firestore'dan getirme işlemi
    private fun fetchSupportRequests() {
        firestore.collection("supportRequests")
            .whereEqualTo("response", "")
            .get()
            .addOnSuccessListener { result ->
                val supportRequests = result.map { document ->
                    document.toObject(SupportRequestModel::class.java).apply { id = document.id }
                }
                // RecyclerView için SupportRequestAdapter kullanarak talepleri listele
                recyclerViewSupportRequests.adapter = SupportRequestAdapter(supportRequests) { request ->
                    selectedRequest = request
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Hata: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    // Talebe cevap gönderme işlemi
    private fun respondToRequest(request: SupportRequestModel, response: String) {
        val responseTimestamp = System.currentTimeMillis()
        val updates = mapOf(
            "response" to response,
            "responseTimestamp" to responseTimestamp
        )
        firestore.collection("supportRequests").document(request.id).update(updates)
            .addOnSuccessListener {
                // Kullanıcıya bildirim gönderme
                sendNotificationToUser(request.userToken, response)
                Toast.makeText(this, "Cevap başarıyla gönderildi.", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Hata: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    // Kullanıcıya bildirim gönderme işlemi
    private fun sendNotificationToUser(userToken: String, response: String) {
        val client = OkHttpClient()

        val json = JSONObject()
        val notification = JSONObject()
        notification.put("title", "Destek Cevabı")
        notification.put("body", response)
        json.put("to", userToken)
        json.put("notification", notification)

        val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("https://fcm.googleapis.com/fcm/send")
            .post(requestBody)
            .addHeader("Authorization", "key=AIzaSyAc33vqKSEXKIVirkXHWN7L-Kl2sRtw4L0")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@AdminFAQActivity, "Bildirim gönderilemedi: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("FCM", "Bildirim gönderilemedi", e)
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AdminFAQActivity, "Bildirim başarıyla gönderildi.", Toast.LENGTH_LONG).show()
                    } else {
                        val responseBody = response.body?.string()
                        Toast.makeText(this@AdminFAQActivity, "Bildirim gönderilemedi: ${response.message}", Toast.LENGTH_LONG).show()
                        Log.e("FCM", "Bildirim gönderilemedi: ${response.message}\n$responseBody")
                    }
                }
            }
        })
    }
}
