package com.attijariwafabank.devisesapp.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.attijariwafabank.devisesapp.R
import com.attijariwafabank.devisesapp.data.Agency

class AgencyAdapter(private val agencies: List<Agency>, private val context: Context) :
    RecyclerView.Adapter<AgencyAdapter.AgencyViewHolder>() {

    inner class AgencyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomText: TextView = itemView.findViewById(R.id.nomText)
        val adresseText: TextView = itemView.findViewById(R.id.adresseText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgencyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.agency_item, parent, false)
        return AgencyViewHolder(view)
    }

    override fun onBindViewHolder(holder: AgencyViewHolder, position: Int) {
        val agence = agencies[position]
        holder.nomText.text = agence.nom
        holder.adresseText.text = agence.adresse

        holder.itemView.setOnClickListener {
            val horaires = listOf(
                agence.horaire1, agence.horaire2, agence.horaire3,
                agence.horaire4, agence.horaire5, agence.horaire6
            ).filter { it.trim().isNotEmpty() }.joinToString("\n")

            AlertDialog.Builder(context)
                .setTitle(agence.nom)
                .setMessage("📍 Adresse: ${agence.adresse}\n📞 Téléphone: ${agence.telephone1}\n🕐 Horaires:\n$horaires")
                .setPositiveButton("Fermer", null)
                .show()
        }
    }

    override fun getItemCount() = agencies.size
}
