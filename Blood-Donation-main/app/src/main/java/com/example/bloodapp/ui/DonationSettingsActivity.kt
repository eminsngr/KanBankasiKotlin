package com.example.bloodapp.ui.fragment

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bloodapp.R
import com.example.bloodapp.databinding.ActivityDonationSettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DonationSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDonationSettingsBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDonationSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase Firestore ve Authentication örneklerini al
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Kaydet düğmesine tıklama olayını tanımla
        binding.saveButton.setOnClickListener {
            saveDonationSettings()
        }
    }

    // Bağış ayarlarını kaydetme işlemi
    private fun saveDonationSettings() {
        // Geçerli kullanıcının kimliğini al
        val currentUserId = auth.currentUser!!.uid
        // Bağış türünü belirle (donör veya alıcı)
        val donationType = when (binding.donationTypeGroup.checkedRadioButtonId) {
            R.id.donorOption -> "Donor" // Donör seçeneği seçildiyse
            R.id.recipientOption -> "Recipient" // Alıcı seçeneği seçildiyse
            else -> "" // Hiçbiri seçilmediyse
        }

        // Kullanıcının girdiği diğer bilgileri al
        val height = binding.heightInput.text.toString()
        val weight = binding.weightInput.text.toString()
        val smokingFrequency = binding.smokingFrequencySpinner.selectedItem.toString()
        val alcoholFrequency = binding.alcoholFrequencySpinner.selectedItem.toString()
        val sexualActivity = binding.sexualActivitySpinner.selectedItem.toString()
        val tattoo = binding.tattooSpinner.selectedItem.toString()

        // Eğer herhangi bir bilgi eksikse, kullanıcıyı bilgilendir ve işlemi sonlandır
        if (donationType.isEmpty() || height.isEmpty() || weight.isEmpty() || smokingFrequency.isEmpty() ||
            alcoholFrequency.isEmpty() || sexualActivity.isEmpty() || tattoo.isEmpty()) {
            Toast.makeText(this, "Lütfen tüm bilgileri doldurun", Toast.LENGTH_SHORT).show()
            return
        }

        // Bağış ayarlarını Firestore'a kaydet
        val donationSettings = hashMapOf(
            "userId" to currentUserId,
            "donationType" to donationType,
            "height" to height,
            "weight" to weight,
            "smokingFrequency" to smokingFrequency,
            "alcoholFrequency" to alcoholFrequency,
            "sexualActivity" to sexualActivity,
            "tattoo" to tattoo
        )

        db.collection("donationSettings").document(currentUserId).set(donationSettings)
            .addOnSuccessListener {
                Toast.makeText(this, "Bilgiler kaydedildi", Toast.LENGTH_SHORT).show()
                finish() // Activity'i sonlandır
            }
            .addOnFailureListener {
                Toast.makeText(this, "Bilgiler kaydedilemedi", Toast.LENGTH_SHORT).show()
            }
    }
}
