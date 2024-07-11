package com.example.bloodapp.ui.fragment

import com.example.bloodapp.ui.AdminFAQActivity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ExpandableListView
import androidx.fragment.app.Fragment
import com.example.bloodapp.R
import com.example.bloodapp.model.UserModel
import com.example.bloodapp.ui.AdminActivity
import com.example.bloodapp.ui.SupportRequestActivity
import com.example.bloodapp.utils.Config
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FaqsFragment : Fragment() {

    // Gerekli değişkenleri tanımlar
    private lateinit var expandableListView: ExpandableListView
    private lateinit var listAdapter: CustomExpandableListAdapter
    private lateinit var listDataHeader: List<String>
    private lateinit var listDataChild: HashMap<String, List<String>>
    private lateinit var buttonSupport: Button
    private lateinit var buttonAdmin: Button

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_feedback, container, false)

        // Görünümleri bağlar
        expandableListView = view.findViewById(R.id.expandableListView)
        buttonSupport = view.findViewById(R.id.buttonSupport)
        buttonAdmin = view.findViewById(R.id.buttonAdmin)

        // Liste verilerini hazırlar
        prepareListData()

        // Liste adaptörünü oluşturur ve ayarlar
        listAdapter = CustomExpandableListAdapter(requireContext(), listDataHeader, listDataChild)
        expandableListView.setAdapter(listAdapter)

        // "Destek Talebi Gönder" düğmesine tıklanınca Destek Talebi aktivitesini başlatır
        buttonSupport.setOnClickListener {
            val intent = Intent(requireContext(), SupportRequestActivity::class.java)
            startActivity(intent)
        }

        // Firestore ve Auth örneklerini alır
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Mevcut kullanıcı kimliğini alır
        val currentUserId = auth.currentUser?.uid

        // Kullanıcı kimliği varsa Firestore'dan kullanıcıyı kontrol eder
        currentUserId?.let { userId ->
            db.collection("users").document(userId).get().addOnSuccessListener { userSnapshot ->
                if (!userSnapshot.exists()) {
                    // Kullanıcı yoksa ve admin değilse, Admin FAQ aktivitesini başlatır
                    buttonAdmin.visibility = View.VISIBLE
                    buttonSupport.visibility = View.INVISIBLE
                    buttonAdmin.setOnClickListener {
                        val intent = Intent(activity, AdminFAQActivity::class.java)
                        startActivity(intent)
                    }
                }

                // Yükleniyor iletişim kutusunu gizler
                Config.hideDialog()
            }.addOnFailureListener { exception ->
                // Yükleniyor iletişim kutusunu gizler
                Config.hideDialog()
            }
        }

        return view
    }

    // Liste verilerini hazırlar
    private fun prepareListData() {
        listDataHeader = listOf(
            getString(R.string.question1),
            getString(R.string.question2),
            getString(R.string.question3),
            getString(R.string.question4)
        )

        listDataChild = HashMap()
        listDataChild[listDataHeader[0]] = listOf(getString(R.string.answer1))
        listDataChild[listDataHeader[1]] = listOf(getString(R.string.answer2))
        listDataChild[listDataHeader[2]] = listOf(getString(R.string.answer3))
        listDataChild[listDataHeader[3]] = listOf(getString(R.string.answer4))
    }
}
