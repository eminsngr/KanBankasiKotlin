package com.example.bloodapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bloodapp.model.UserModel
import com.example.bloodapp.databinding.ActivityRegstionBinding
import com.example.bloodapp.ui.MainActivity
import com.example.bloodapp.utils.AddressUtils
import com.example.bloodapp.utils.Config
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// RegstionActivity sınıfı, kullanıcı kaydını yönetir
class RegstionActivity : AppCompatActivity() {

    lateinit var binding: ActivityRegstionBinding // ViewBinding nesnesi
    lateinit var db: FirebaseFirestore // FirebaseFirestore nesnesi
    lateinit var auth: FirebaseAuth // FirebaseAuth nesnesi

    lateinit var blood: String // Kan grubu
    lateinit var division: String // Bölge
    lateinit var districts: String // İlçe

    // Aktivite oluşturulurken çağrılır
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegstionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide() // Üst çubuğu gizler

        db = FirebaseFirestore.getInstance() // FirebaseFirestore örneğini alır
        auth = FirebaseAuth.getInstance() // FirebaseAuth örneğini alır

        // Giriş yapma ekranına yönlendirir
        binding.userLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // Kan grubu seçeneklerini spinner'a yükler
        val bloodTypes = arrayOf("Kan grubunuzu seçin.", "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")
        binding.spinnerBloodGroup.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, bloodTypes)
        )

        // Kan grubu seçildiğinde çağrılır
        binding.spinnerBloodGroup.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                blood = binding.spinnerBloodGroup.getSelectedItem().toString() // Seçilen kan grubunu alır
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        // Bölge seçeneklerini spinner'a yükler
        val divisions = AddressUtils.getDivisions()
        binding.spinnerDivsion.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, divisions)
        )

        // Bölge seçildiğinde çağrılır
        binding.spinnerDivsion.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                division = binding.spinnerDivsion.getSelectedItem().toString() // Seçilen bölgeyi alır
                // İlçe seçeneklerini spinner'a yükler
                binding.spinnerDistricts.setAdapter(
                    ArrayAdapter(this@RegstionActivity, android.R.layout.simple_spinner_dropdown_item, AddressUtils.getDistrict(division))
                )
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        // İlçe seçildiğinde çağrılır
        binding.spinnerDistricts.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                districts = binding.spinnerDistricts.getSelectedItem().toString() // Seçilen ilçeyi alır
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        // Kayıt düğmesine tıklandığında çağrılır
        binding.signIn.setOnClickListener {
            val name = binding.userName.text.toString()
            val phone = binding.userPhone.text.toString()
            val email = binding.userEmail.text.toString()
            val password = binding.userPassword.text.toString()
            val userType = "user" // Kullanıcı tipi

            // Giriş alanları doğrulandığında kayıt işlemi gerçekleştirilir
            if (name.isEmpty()) {
                binding.userName.setError("Lütfen adınızı girin.")
                binding.userName.requestFocus()
            } else if (phone.isEmpty()) {
                binding.userPhone.setError("Lütfen telefon numarası girin.")
                binding.userPhone.requestFocus()
            } else if (email.isEmpty()) {
                binding.userEmail.setError("Lütfen e-mail adresinizi girin.")
                binding.userEmail.requestFocus()
            } else if (password.isEmpty()) {
                binding.userPassword.setError("Lütfen bir şifre girin.")
                binding.userPassword.requestFocus()
            } else if (blood.equals("Kan grubunuzu seçin.")) {
                Toast.makeText(this, "Lütfen kan grubunuzu seçin.", Toast.LENGTH_SHORT).show()
            } else if (division.equals("Select Division")) {
                Toast.makeText(this, "Lütfen bulunduğunuz şehri girin.", Toast.LENGTH_SHORT).show()
            } else if (districts.equals("Select District")) {
                Toast.makeText(this, "Lütfen bulunduğunuz ilçeyi girin.", Toast.LENGTH_SHORT).show()
            } else {
                // Firebase ile yeni kullanıcı oluşturur
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Config.showDialog(this)
                        val currentUserId = auth.currentUser!!.uid
                        val data = UserModel(
                            currentUserId,
                            name,
                            phone,
                            blood,
                            division,
                            districts,
                            email,
                            userType
                        )

                        // Firestore'a kullanıcı verilerini kaydeder
                        db.collection("users").document(currentUserId).set(data)
                            .addOnCompleteListener {
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                                Toast.makeText(this, "Kayıt başarılı.", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Config.hideDialog()
                        Toast.makeText(this, it.exception!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}






