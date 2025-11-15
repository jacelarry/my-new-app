package com.example.smsautomationapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smsautomationapp.databinding.ItemCampaignBinding
import com.example.smsautomationapp.model.Campaign

class CampaignsAdapter(
    private val onClick: (Campaign) -> Unit
) : ListAdapter<Campaign, CampaignsAdapter.Holder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(
            ItemCampaignBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onClick
        )

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    class Holder(
        private val binding: ItemCampaignBinding,
        private val onClick: (Campaign) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(campaign: Campaign) {
            binding.tvCampaignTitle.text = campaign.title
            binding.tvCampaignMeta.text =
                "Sent: ${campaign.sent} • Delivered: ${campaign.delivered} • Failed: ${campaign.failed}"
            binding.pbCampaign.progress = campaign.progress
            binding.root.setOnClickListener { onClick(campaign) }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Campaign>() {
        override fun areItemsTheSame(oldItem: Campaign, newItem: Campaign) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Campaign, newItem: Campaign) = oldItem == newItem
    }
}

