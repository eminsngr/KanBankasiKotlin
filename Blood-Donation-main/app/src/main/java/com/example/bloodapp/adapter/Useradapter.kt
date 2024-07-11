package com.example.bloodapp.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.example.bloodapp.databinding.ItemUserBinding
import com.example.bloodapp.model.UserModel
import com.example.bloodapp.ui.fragment.HomeFragment
import java.util.ArrayList

// Useradapter sınıfı, RecyclerView.Adapter sınıfını genişletir ve UserViewHolder kullanır
class Useradapter(val context: HomeFragment, var list: ArrayList<UserModel>) :
    RecyclerView.Adapter<Useradapter.UserViewHolder>() {

    // ViewHolder sınıfı, item_user.xml dosyasındaki bileşenlere erişim sağlar
    class UserViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)

    // ViewHolder oluşturulurken çağrılır, görünüm bağlama işlemini yapar
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    // Veri güncelleme işlevi, yeni verilerle adaptörü günceller
    fun updateData(dataItem: ArrayList<UserModel>) {
        list = dataItem
        notifyDataSetChanged()
    }

    // Belirtilen pozisyondaki veriyi ViewHolder'a bağlar
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.binding.userName.text = list[position].name.toString()
        holder.binding.userBlood.text = list[position].blood.toString()
        holder.binding.userDivision.text = list[position].division.toString()
        holder.binding.userDistrict.text = list[position].district.toString()
        holder.binding.userPhone.text = list[position].phone.toString()

        // Kullanıcı telefon numarasına tıklandığında, telefon arama ekranını açar
        holder.binding.animationView.setOnClickListener {
            val phone = list[position].phone.toString()
            val intent = Intent()
            intent.action = Intent.ACTION_DIAL
            intent.data = Uri.parse("tel:$phone")
            context.startActivity(intent)
        }

        // Animasyonu başlatır
        setAnimation(holder.binding.root)
    }

    // Görünüme animasyon ekler
    fun setAnimation(view: View) {
        val animation: Animation = AnimationUtils.loadAnimation(context.requireContext(), android.R.anim.slide_in_left)
        view.animation = animation
    }

    // Listede bulunan öğe sayısını döndürür
    override fun getItemCount() = list.size
}
