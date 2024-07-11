package com.example.bloodapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bloodapp.databinding.FragmentProfileBinding
import com.example.bloodapp.model.UserModel
import com.example.bloodapp.ui.AdminActivity
import com.example.bloodapp.ui.auth.LoginActivity
import com.example.bloodapp.utils.Config
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)

        // Firebase Firestore ve Auth örneklerini al
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Mevcut kullanıcı ID'sini al
        val currentUserId = auth.currentUser?.uid

        // Eğer kullanıcı mevcutsa, verilerini al ve görüntüle
        currentUserId?.let { userId ->
            db.collection("users").document(userId).get().addOnSuccessListener { userSnapshot ->
                if (userSnapshot.exists()) {
                    // Kullanıcı veritabanında varsa, verileri görüntüle
                    db.collection("users").document(currentUserId).get()
                        .addOnSuccessListener { result ->
                            val data = result.toObject(UserModel::class.java)
                            binding.userName.setText(data!!.name.toString())
                            binding.userPhone.setText(data!!.phone.toString())
                            binding.userEmail.setText(data!!.email.toString())
                            binding.userBlood.setText(data!!.blood).toString()

                            // Yüklenen verilerin ardından diyalog kutusunu gizle
                            Config.hideDialog()
                        }
                } else {
                    // Kullanıcı veritabanında mevcut değilse, admin olarak kabul edilir
                    binding.apply {
                        userName.setText(" " ?: "")
                        userPhone.setText(" " ?: "")
                        userEmail.setText(" " ?: "")
                        userBlood.setText(" " ?: "")
                        donationSettingsLink.visibility = View.GONE
                        adminButton.visibility = View.VISIBLE
                    }
                }
                // Veriler yüklendiğinde diyalog kutusunu gizle
                Config.hideDialog()
            }.addOnFailureListener { exception ->
                // Hata durumunda diyalog kutusunu gizle
                Config.hideDialog()
                // Hata durumunda kullanıcıya bilgi ver
                // Log.d(TAG, "Error getting user details: ", exception)
            }
        }

        // Bağış ayarlarına yönlendirme butonunun tıklanma olayı
        binding.donationSettingsLink.setOnClickListener {
            val intent = Intent(activity, DonationSettingsActivity::class.java)
            startActivity(intent)
        }

        // Çıkış yap butonunun tıklanma olayı
        binding.logoutButton.setOnClickListener {
            // FirebaseAuth üzerinden çıkış yap
            auth.signOut()
            // Giriş ekranına yönlendir
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish() // Bu fragmentı kapat
        }

        // Admin paneline yönlendirme butonunun tıklanma olayı
        binding.adminButton.setOnClickListener {
            val intent = Intent(activity, AdminActivity::class.java)
            startActivity(intent)
        }

        // Oluşturulan görünümü döndür
        return binding.root
    }
}
