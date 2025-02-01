package com.example.denemefinal1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class ScientistAdapter(private val scientistList: List<Scientist>) :
    RecyclerView.Adapter<ScientistAdapter.ScientistViewHolder>() {

    class ScientistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val scientistImage: ImageView = itemView.findViewById(R.id.scientistImage)
        val scientistName: TextView = itemView.findViewById(R.id.scientistName)
        val scientistBirthPlace: TextView = itemView.findViewById(R.id.scientistBirthPlace)
        val scientistBirthDate: TextView = itemView.findViewById(R.id.scientistBirthDate)
        val scientistDeathDate: TextView = itemView.findViewById(R.id.scientistDeathDate)
        val scientistDetails: TextView = itemView.findViewById(R.id.scientistDetails)
        val userEmail: TextView = itemView.findViewById(R.id.userEmail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScientistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_scientist, parent, false)
        return ScientistViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScientistViewHolder, position: Int) {
        val scientist = scientistList[position]

        // Bilim insanı bilgilerini doldurma
        holder.scientistName.text = "${scientist.name} ${scientist.surname}"
        holder.scientistBirthPlace.text = "Doğum Yeri: ${scientist.birthPlace}"
        holder.scientistBirthDate.text = "Doğum Tarihi: ${scientist.birthDate}"
        holder.scientistDeathDate.text = "Ölüm Tarihi: ${scientist.deathDate}"
        holder.scientistDetails.text = "Katkıları: ${scientist.contribution}"
        holder.userEmail.text = "Ekleyen: ${scientist.userEmail ?: "Bilinmeyen Kullanıcı"}"

        // Resmi yükleme (Picasso)
        Picasso.get()
            .load(scientist.imageUrl)
            .placeholder(R.drawable.ic_launcher_background) // Yer tutucu resim
            .error(R.drawable.ic_launcher_foreground) // Yükleme hatasında gösterilecek resim
            .into(holder.scientistImage)

        // Resme tıklanabilirlik ekleme
        holder.scientistImage.setOnClickListener {
            Toast.makeText(
                holder.itemView.context,
                "${scientist.name} hakkında detayları görüntülemek için bir ekran açılabilir.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun getItemCount(): Int {
        return scientistList.size
    }
}
