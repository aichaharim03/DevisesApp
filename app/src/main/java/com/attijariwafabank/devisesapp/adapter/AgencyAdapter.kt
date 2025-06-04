package com.attijariwafabank.devisesapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.attijariwafabank.devisesapp.R
import com.attijariwafabank.devisesapp.data.Agency
import com.attijariwafabank.devisesapp.fragments.AgencyBottomSheetFragment

class AgencyAdapter(
    private var agencies: List<Agency>,
    private val context: Context
) : RecyclerView.Adapter<AgencyAdapter.AgencyViewHolder>() {

    inner class AgencyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.nomText)
        val distance: TextView = itemView.findViewById(R.id.agencydistance)
        val closestLabel: TextView = itemView.findViewById(R.id.textViewClosest)
        val addressText: TextView = itemView.findViewById(R.id.adresseText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgencyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.agency_item, parent, false)
        return AgencyViewHolder(view)
    }

    override fun onBindViewHolder(holder: AgencyViewHolder, position: Int) {
        val agency = agencies[position]
        holder.name.text = agency.nom
        holder.distance.text = agency.distance
        holder.addressText.text = agency.adresse

        holder.closestLabel.visibility = if (position == 0) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener {
            val activity = context as? FragmentActivity

            activity?.let {
                val bottomSheet = AgencyBottomSheetFragment(agency)
                bottomSheet.show(it.supportFragmentManager, bottomSheet.tag)
            }
        }
    }

    override fun getItemCount(): Int = agencies.size

    fun updateAgencies(newAgencies: List<Agency>) {
        agencies = newAgencies
        notifyDataSetChanged()
    }
}
