package com.example.smsautomationapp

import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class RecipientStatusAdapter : ListAdapter<DashboardViewModel.RecipientStatus, RecipientStatusAdapter.Holder>(DIFF) {
    companion object {
        val DIFF = object : DiffUtil.ItemCallback<DashboardViewModel.RecipientStatus>() {
            override fun areItemsTheSame(o: DashboardViewModel.RecipientStatus, n: DashboardViewModel.RecipientStatus) = o.number == n.number
            override fun areContentsTheSame(o: DashboardViewModel.RecipientStatus, n: DashboardViewModel.RecipientStatus) = o == n
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val card = MaterialCardView(parent.context).apply {
            layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply { setMargins(0,8,0,8) }
            radius = 16f
            preventCornerOverlap = true
            useCompatPadding = true
            setContentPadding(24,16,24,16)
        }
        val tv = TextView(parent.context).apply { textSize = 14f }
        card.addView(tv)
        return Holder(card, tv)
    }
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = getItem(position)
        val err = item.error?.let { " ($it)" } ?: ""
        holder.text.text = "${item.number} • ${item.state}$err".trim()
    }
    class Holder(v: MaterialCardView, val text: TextView) : RecyclerView.ViewHolder(v)
}
