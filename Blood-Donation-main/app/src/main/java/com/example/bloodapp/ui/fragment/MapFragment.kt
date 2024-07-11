package com.example.bloodapp.ui.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.bloodapp.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Marker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URLEncoder
import java.net.URL

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val apiKey = "AIzaSyB-bUjILjMRv4JcotyzniAu0TIDERFz044" // Google Haritalar API anahtarı
    private val auth = FirebaseAuth.getInstance() // Firebase kimlik doğrulama örneği
    private val db = FirebaseFirestore.getInstance() // Firestore örneği
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001 // Konum izni isteme kodu

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Fragment'ın görünümünü oluştur
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        // Konum sağlayıcı istemciyi başlat
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // Harita nesnesini bağlamaya çalış
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        // Harita hazır olduğunda çağrılır
        mMap = googleMap
        // Kullanıcının konumunu al ve haritaya yerleştir
        fetchUserLocationAndSetMap()
        // Kan bağışı merkezlerini ara ve haritaya işaretle
        searchDonationCenters()

        // İşaretçilere tıklama olayını dinle
        mMap.setOnMarkerClickListener { marker ->
            // Kullanıcının son konumunu al ve yol tarifi oluştur
            getUserLastLocation(marker)
            true
        }
    }

    private fun fetchUserLocationAndSetMap() {
        // Konum izni kontrolü yap
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Konum izni yoksa, izin iste
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Konum izni varsa, kullanıcının konumunu al ve haritaya yerleştir
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        // Konum bulunduğunda haritada göster
                        mMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(location.latitude, location.longitude), 15f
                            )
                        )
                    } else {
                        // Konum bulunamadığında hata mesajı göster
                        Log.e("MapFragment", "User location not available")
                        Toast.makeText(
                            requireContext(),
                            "User location not available",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .addOnFailureListener { e ->
                    // Konum alınamadığında hata mesajı göster
                    Log.e("MapFragment", "Failed to get user location: ${e.message}")
                    Toast.makeText(
                        requireContext(),
                        "Failed to get user location: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun searchDonationCenters() {
        // Kullanıcının konumunu al
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            // Konum bilgisini kontrol et
            if (location != null) {
                // Kullanıcının bulunduğu bölgeye göre sorgu oluştur
                val query = "Kızılay Kan Bağış Merkezi in ${location.latitude},${location.longitude}"
                // Sorguyu URL formatına çevir
                val urlString =
                    "https://maps.googleapis.com/maps/api/place/textsearch/json?" +
                            "query=${URLEncoder.encode(query, "UTF-8")}" +
                            "&key=$apiKey"

                // Sorguyu yapmak için Coroutine kullan
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        // URL'den verileri oku
                        val result = URL(urlString).readText()
                        val jsonObject = JSONObject(result)
                        val results = jsonObject.getJSONArray("results")

                        // Ana thread'e dön ve haritaya işaretçileri ekle
                        withContext(Dispatchers.Main) {
                            for (i in 0 until results.length()) {
                                val place = results.getJSONObject(i)
                                val location = place.getJSONObject("geometry").getJSONObject("location")
                                val lat = location.getDouble("lat")
                                val lng = location.getDouble("lng")
                                val name = place.getString("name")

                                val latLng = LatLng(lat, lng)
                                mMap.addMarker(MarkerOptions().position(latLng).title(name))
                            }
                        }
                    } catch (e: Exception) {
                        // Hata durumunda log kaydı oluştur
                        Log.e("MapFragment", "Error fetching donation centers", e)
                    }
                }
            } else {
                // Konum bulunamadığında hata mesajı göster
                Log.e("MapFragment", "User location not available")
                Toast.makeText(
                    requireContext(),
                    "User location not available",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun getUserLastLocation(marker: Marker) {
        // Kullanıcının son konumunu al
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            // Konum bilgisini kontrol et
            if (location != null) {
                // Kullanıcının konumunu ve işaretçinin konumunu kullanarak yol tarifi oluştur
                val intentUri = "https://www.google.com/maps/dir/?api=1" +
                        "&origin=${location.latitude},${location.longitude}" +
                        "&destination=${marker.position.latitude},${marker.position.longitude}" +
                        "&travelmode=driving" // Yol tarifi için kullanılan seyahat modu (örneğin, driving, walking)
                // Intent'i başlatmak için ACTION_VIEW Intent'i oluşturun
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(intentUri))

                // Harita uygulamasını başlatın
                startActivity(intent)
            } else {
                // Konum bulunamadığında hata mesajı göster
                Log.e("MapFragment", "Kullanıcı konumu erişilebilir değil.")
                Toast.makeText(
                    requireContext(),
                    "Kullanıcı konumu erişilebilir değil.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // İzin verildiyse, işlem yap
                fetchUserLocationAndSetMap()
            } else {
                // İzin reddedildiyse, kullanıcıyı bilgilendir
                Toast.makeText(
                    requireContext(),
                    "Konum izni reddedildi.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}