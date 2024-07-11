import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bloodapp.R
import com.google.firebase.firestore.FirebaseFirestore

class UserDetailActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String

    private lateinit var userName: TextView
    private lateinit var bloodGroup: TextView
    private lateinit var heightText: TextView
    private lateinit var weightText: TextView
    private lateinit var smokingFrequencyText: TextView
    private lateinit var alcoholFrequencyText: TextView
    private lateinit var sexualActivityText: TextView
    private lateinit var tattooText: TextView
    private lateinit var approveButton: Button
    private lateinit var rejectButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)

        // Firestore bağlantısını başlat
        db = FirebaseFirestore.getInstance()

        // Intent'ten kullanıcı ID'sini al
        userId = intent.getStringExtra("userId")!!

        // Gerekli bileşenleri tanımla
        userName = findViewById(R.id.userName)
        bloodGroup = findViewById(R.id.bloodGroup)
        heightText = findViewById(R.id.heightText)
        weightText = findViewById(R.id.weightText)
        smokingFrequencyText = findViewById(R.id.smokingFrequencyText)
        alcoholFrequencyText = findViewById(R.id.alcoholFrequencyText)
        sexualActivityText = findViewById(R.id.sexualActivityText)
        tattooText = findViewById(R.id.tattooText)
        approveButton = findViewById(R.id.approveButton)
        rejectButton = findViewById(R.id.rejectButton)

        // Kullanıcı detaylarını doldur
        fillUserDetails()

        // Onayla butonuna tıklanınca
        approveButton.setOnClickListener {
            updateDonorResult("accepted")
        }

        // Reddet butonuna tıklanınca
        rejectButton.setOnClickListener {
            updateDonorResult("denied")
        }
    }

    // Kullanıcı detaylarını doldurmak için Firestore'dan veri getirme işlemi
    private fun fillUserDetails() {
        db.collection("donationSettings").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    heightText.text = document.getString("height")
                    weightText.text = document.getString("weight")
                    smokingFrequencyText.text = document.getString("smokingFrequency")
                    alcoholFrequencyText.text = document.getString("alcoholFrequency")
                    sexualActivityText.text = document.getString("sexualActivity")
                    tattooText.text = document.getString("tattoo")

                    // Kullanıcının adını ve kan grubunu al
                    db.collection("users").document(userId)
                        .get()
                        .addOnSuccessListener { document2 ->
                            if (document2 != null) {
                                userName.text = document2.getString("name")
                                bloodGroup.text = document2.getString("blood")
                            }
                        }

                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Kullanıcı bilgileri getirilemedi", Toast.LENGTH_SHORT).show()
            }

    }

    // Kullanıcının bağış sonucunu güncelleme işlemi
    private fun updateDonorResult(result: String) {
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val userMap = document.data?.toMutableMap() ?: mutableMapOf()
                    userMap["result"] = result
                    db.collection("DonorResults").document(userId)
                        .set(userMap)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Sonuç güncellendi", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Sonuç güncellenemedi", Toast.LENGTH_SHORT).show()
                        }
                }
            }
    }
}
