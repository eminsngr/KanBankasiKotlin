package com.example.bloodapp.ui.auth

import android.content.Intent
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bloodapp.databinding.ActivityLoginBinding
import com.example.bloodapp.ui.MainActivity
import com.example.bloodapp.ui.auth.ForgotActivity
import com.example.bloodapp.ui.auth.RegstionActivity
import com.example.bloodapp.utils.Config
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.content.SharedPreferences
import com.google.firebase.messaging.FirebaseMessaging
import android.util.Log
import com.google.firebase.firestore.SetOptions

// LoginActivity sınıfı, kullanıcı girişini yönetir
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding // ViewBinding nesnesi
    private lateinit var auth: FirebaseAuth // FirebaseAuth nesnesi
    private lateinit var sharedPreferences: SharedPreferences // SharedPreferences nesnesi
    private lateinit var firestore: FirebaseFirestore // FirebaseFirestore nesnesi
    private var userToken: String = "" // Kullanıcının FCM token'ını saklamak için değişken

    // Aktivite oluşturulurken çağrılır
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide() // Üst çubuğu gizler

        auth = FirebaseAuth.getInstance() // FirebaseAuth örneğini alır
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE) // SharedPreferences örneğini alır
        firestore = FirebaseFirestore.getInstance() // FirebaseFirestore örneğini alır

        // Kullanıcı daha önce oturum açtıysa ve oturumu hala açıksa, MainActivity'e yönlendirir
        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Kullanıcının cihazına özgü FCM token'ını alır
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                userToken = task.result // Kullanıcının cihazına özgü token'ı alır
                Log.d("UserToken", "Token: $userToken")
            } else {
                Log.e("UserToken", "Tokene ulaşılamadı: ${task.exception}")
            }
        }

        // Şifremi unuttum ekranına yönlendirir
        binding.userForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotActivity::class.java))
        }

        // Kayıt ekranına yönlendirir
        binding.userRegistion.setOnClickListener {
            startActivity(Intent(this, RegstionActivity::class.java))
        }

        // Giriş yapma düğmesine tıklanıldığında çağrılır
        binding.signIn.setOnClickListener {
            val email = binding.userEmail.text.toString()
            val password = binding.userPassword.text.toString()

            if (email.isEmpty()) {
                binding.userEmail.error = "Lütfen e-mail adresinizi girin."
                binding.userEmail.requestFocus()
            } else if (password.isEmpty()) {
                binding.userPassword.error = "Lütfen şifrenizi girin."
                binding.userPassword.requestFocus()
            } else {
                // Firebase ile e-posta ve şifreyle giriş yapar
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { signInTask ->
                    if (signInTask.isSuccessful) {
                        val currentUser = auth.currentUser
                        val userEmail = currentUser?.email

                        // Firestore tablosunda kullanıcının e-posta adresini kontrol eder
                        val adminRef = firestore.collection("users")
                        adminRef.whereEqualTo("email", userEmail).get().addOnSuccessListener { adminSnapshot ->
                            if (!adminSnapshot.isEmpty) {
                                // Eğer 'admin' tablosunda kullanıcının e-posta adresi varsa, userType'ı alır
                                val userType = adminSnapshot.documents[0]["userType"] as String
                                val userId = adminSnapshot.documents[0]["id"] as String
                                // Oturum durumunu saklar ve userToken'ı Firestore'da saklar
                                saveSessionState(userType, userId)
                                saveUserToken(userId)

                                // Kullanıcının oturum açma işlemi başarılı olduğunda MainActivity'ye yönlendirir
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                                Toast.makeText(this, "Giriş başarılı", Toast.LENGTH_SHORT).show()
                            } else {
                                // Eğer 'admin' tablosunda kullanıcının e-posta adresi yoksa, varsayılan olarak "user" olarak ayarlar
                                val userType = "admin"
                                val userId = "admin"
                                // Oturum durumunu saklar ve userToken'ı Firestore'da saklar
                                saveSessionState(userType, userId)
                                saveUserToken(userId)

                                // Kullanıcının oturum açma işlemi başarılı olduğunda MainActivity'ye yönlendirir
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                                Toast.makeText(this, "Giriş başarılı", Toast.LENGTH_SHORT).show()
                            }
                        }.addOnFailureListener { adminException ->
                            Toast.makeText(this, "Admin tablosuna erişirken hata oluştu: ${adminException.message}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, signInTask.exception!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // Oturum durumunu saklayan fonksiyon
    private fun saveSessionState(userType: String, userId: String) {
        val editor = sharedPreferences.edit()
        editor.putString("userType", userType)
        editor.putString("userId", userId)
        editor.apply()

        // Giriş başarılı olduğunda kullanıcı tipi ve kullanıcı kimliğini log'a yazdırır
        Log.d("UserSession", "UserType: $userType, UserID: $userId")
    }

    // Kullanıcıya özgü FCM token'ını Firestore'da saklayan fonksiyon
    private fun saveUserToken(userId: String) {
        val userRef = firestore.collection("users").document(userId)
        val data = hashMapOf("userToken" to userToken) // Kullanıcı token'ını Firestore'a kaydeder
        userRef.set(data, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("UserToken", "User token saved successfully.")
            }
            .addOnFailureListener { e ->
                Log.e("UserToken", "Error saving user token: $e")
            }
    }
}