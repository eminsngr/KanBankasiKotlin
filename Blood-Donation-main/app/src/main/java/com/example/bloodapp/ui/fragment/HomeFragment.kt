package com.example.bloodapp.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bloodapp.adapter.Useradapter
import com.example.bloodapp.databinding.FragmentHomeBinding
import com.example.bloodapp.model.UserModel
import com.example.bloodapp.utils.Config
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.collections.ArrayList
import android.widget.ToggleButton
import android.widget.CheckBox
import com.example.bloodapp.ui.auth.LoginActivity

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var list: ArrayList<UserModel>
    private lateinit var adapter: Useradapter

    private lateinit var toggleButton: ToggleButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Fragment'in layout dosyasını şişirir
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Kullanıcı listesini ve adaptörünü oluşturur
        list = ArrayList()
        adapter = Useradapter(this, list)

        // Firebase Firestore ve Auth örneklerini alır
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // ToggleButton'u bağlar
        toggleButton = binding.toggleButton

        // ToggleButton'a tıklandığında işlem yapar
        toggleButton.setOnClickListener {
            if (toggleButton.isChecked) {
                // Donör modu
                getDonor()
            } else {
                // Alıcı modu
                getRecipient()
            }
        }

        // Kullanıcı moduna göre verileri getirir
        if (toggleButton.text == "Alıcı") {
            getRecipient()
        } else {
            getDonor()
        }

        return binding.root
    }

    // Alıcıları getirir
    private fun getRecipient() {
        // Firestore'dan alıcı sonuçlarını getirir
        db.collection("DonorResults")
            .whereEqualTo("result", "accepted")
            .get()
            .addOnSuccessListener { recipientResults ->
                val recipientResultUserIds = recipientResults.documents.mapNotNull { it.getString("id") }

                if (recipientResultUserIds.isNotEmpty()) {
                    // Kabul edilmiş sonuçları getirir
                    db.collection("donationSettings")
                        .whereIn("userId", recipientResultUserIds)
                        .whereEqualTo("donationType", "Recipient")
                        .get()
                        .addOnSuccessListener { acceptedResults ->
                            val acceptedResultUserIds = acceptedResults.documents.mapNotNull { it.getString("userId") }

                            if (acceptedResultUserIds.isNotEmpty()) {
                                // Kullanıcıları getirir
                                db.collection("users")
                                    .whereIn("id", acceptedResultUserIds)
                                    .get()
                                    .addOnSuccessListener { userSnapshot ->
                                        val data = userSnapshot.toObjects(UserModel::class.java)
                                        list.clear()
                                        list.addAll(data)
                                        adapter.updateData(list)
                                        binding.userRecyclerView.adapter = adapter
                                        if (isAdded) {
                                            Config.hideDialog()
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        if (isAdded) {
                                            Config.hideDialog()
                                        }
                                        Log.e("TAG", "Kullanıcıları getirirken hata oluştu", exception)
                                    }
                            } else {
                                if (isAdded) {
                                    Config.hideDialog()
                                }
                            }
                        }
                        .addOnFailureListener { exception ->
                            if (isAdded) {
                                Config.hideDialog()
                            }
                            Log.e("TAG", "Kabul edilmiş sonuçları getirirken hata oluştu", exception)
                        }
                } else {
                    if (isAdded) {
                        Config.hideDialog()
                    }
                }
            }
            .addOnFailureListener { exception ->
                if (isAdded) {
                    Config.hideDialog()
                }
                Log.e("TAG", "Alıcı sonuçlarını getirirken hata oluştu", exception)
            }
    }

    // Donörleri getirir
    private fun getDonor() {
        // Firestore'dan donör sonuçlarını getirir
        db.collection("DonorResults")
            .whereEqualTo("result", "accepted")
            .get()
            .addOnSuccessListener { DonorResults ->
                val DonorResultUserIds = DonorResults.documents.mapNotNull { it.getString("id") }

                if (DonorResultUserIds.isNotEmpty()) {
                    // Kabul edilmiş sonuçları getirir
                    db.collection("donationSettings")
                        .whereIn("userId", DonorResultUserIds)
                        .whereEqualTo("donationType", "Donor")
                        .get()
                        .addOnSuccessListener { acceptedResultsD ->
                            val acceptedResultUserDIds = acceptedResultsD.documents.mapNotNull { it.getString("userId") }

                            if (acceptedResultUserDIds.isNotEmpty()) {
                                // Kullanıcıları getirir
                                db.collection("users")
                                    .whereIn("id", acceptedResultUserDIds)
                                    .get()
                                    .addOnSuccessListener { userSnapshotD ->
                                        val data = userSnapshotD.toObjects(UserModel::class.java)
                                        list.clear()
                                        list.addAll(data)
                                        adapter.updateData(list)
                                        binding.userRecyclerView.adapter = adapter
                                        if (isAdded) {
                                            Config.hideDialog()
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        if (isAdded) {
                                            Config.hideDialog()
                                        }
                                        Log.e("TAG", "Kullanıcıları getirirken hata oluştu", exception)
                                    }
                            } else {
                                if (isAdded) {
                                    Config.hideDialog()
                                }
                            }
                        }
                        .addOnFailureListener { exception ->
                            if (isAdded) {
                                Config.hideDialog()
                            }
                            Log.e("TAG", "Kabul edilmiş sonuçları getirirken hata oluştu", exception)
                        }
                } else {
                    if (isAdded) {
                        Config.hideDialog()
                    }
                }
            }
            .addOnFailureListener { exception ->
                if (isAdded) {
                    Config.hideDialog()
                }
                Log.e("TAG", "Donör sonuçlarını getirirken hata oluştu", exception)
            }
    }
}
