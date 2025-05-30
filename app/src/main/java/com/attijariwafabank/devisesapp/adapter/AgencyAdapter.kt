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

class AgencyAdapter(
    private var agencies: List<Agency>,
    private val context: Context
) : RecyclerView.Adapter<AgencyAdapter.AgencyViewHolder>() {

    inner class AgencyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.nomText)
        val distance: TextView = itemView.findViewById(R.id.agencydistance)
        val closestLabel: TextView = itemView.findViewById(R.id.textViewClosest) // add this
        val adresseText: TextView = itemView.findViewById(R.id.adresseText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgencyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.agency_item, parent, false)
        return AgencyViewHolder(view)
    }

    override fun onBindViewHolder(holder: AgencyViewHolder, position: Int) {
        val agency = agencies[position]
        holder.name.text = agency.nom
        holder.distance.text = agency.distance
        holder.adresseText.text = agency.adresse


        // Show "Closest" label only for the first item
        if (position == 0) {
            holder.closestLabel.visibility = View.VISIBLE
        } else {
            holder.closestLabel.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            val horaires = listOf(
                agency.horaire1, agency.horaire2, agency.horaire3,
                agency.horaire4, agency.horaire5, agency.horaire6
            ).filter { it.trim().isNotEmpty() }.joinToString("\n")

            AlertDialog.Builder(context)
                .setTitle(agency.nom)
                .setMessage("📍 Adresse: ${agency.adresse}\n📞 Téléphone: ${agency.telephone1}\n🕐 Horaires:\n$horaires")
                .setPositiveButton("Fermer", null)
                .show()
        }
    }

    override fun getItemCount(): Int = agencies.size

    fun updateAgencies(newAgencies: List<Agency>) {
        agencies = newAgencies
        notifyDataSetChanged()
    }
}



