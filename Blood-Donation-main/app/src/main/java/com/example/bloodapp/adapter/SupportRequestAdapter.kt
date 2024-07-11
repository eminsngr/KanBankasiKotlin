package com.example.bloodapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bloodapp.databinding.ItemSupportRequestBinding
import com.example.bloodapp.model.SupportRequestModel

// SupportRequestAdapter sınıfı, RecyclerView.Adapter sınıfını genişletir ve SupportRequestViewHolder kullanır
class SupportRequestAdapter(
    private val supportRequests: List<SupportRequestModel>, // Destek isteklerinin listesi
    private val onItemClick: (SupportRequestModel) -> Unit // Bir öğeye tıklandığında çalıştırılacak lambda fonksiyonu
) : RecyclerView.Adapter<SupportRequestAdapter.SupportRequestViewHolder>() {

    // ViewHolder oluşturulurken çağrılır, görünüm bağlama işlemini yapar
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupportRequestViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSupportRequestBinding.inflate(inflater, parent, false)
        return SupportRequestViewHolder(binding)
    }

    // Belirtilen pozisyondaki veriyi ViewHolder'a bağlar
    override fun onBindViewHolder(holder: SupportRequestViewHolder, position: Int) {
        val supportRequest = supportRequests[position]
        holder.bind(supportRequest)
    }

    // Listede bulunan öğe sayısını döndürür
    override fun getItemCount(): Int = supportRequests.size

    // İç sınıf olan SupportRequestViewHolder, RecyclerView.ViewHolder sınıfını genişletir
    inner class SupportRequestViewHolder(private val binding: ItemSupportRequestBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        private val textViewEmail: TextView = binding.textViewEmail // Email TextView öğesi
        private val textViewMessage: TextView = binding.textViewMessage // Mesaj TextView öğesi

        // ViewHolder başlatılırken tıklama dinleyicisi ekler
        init {
            itemView.setOnClickListener(this)
        }

        // Veriyi ViewHolder ile bağlar
        fun bind(supportRequest: SupportRequestModel) {
            textViewEmail.text = supportRequest.email
            textViewMessage.text = supportRequest.message
        }

        // Öğeye tıklandığında çağrılır
        override fun onClick(view: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                onItemClick(supportRequests[adapterPosition])
            }
        }
    }
}
