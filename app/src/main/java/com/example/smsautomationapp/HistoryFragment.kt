package com.example.smsautomationapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smsautomationapp.model.Campaign
import com.example.smsautomationapp.ui.CampaignsAdapter
import com.google.android.material.snackbar.Snackbar

class HistoryFragment : Fragment() {
    private lateinit var adapter: CampaignsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val rv = view.findViewById<RecyclerView>(R.id.rvCampaigns)
        val empty = view.findViewById<TextView>(R.id.tvEmptyCampaigns)

        adapter = CampaignsAdapter { c ->
            Snackbar.make(view, "Selected ${'$'}{c.title}", Snackbar.LENGTH_SHORT).show()
        }
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        val data = listOf(
            Campaign(1, "Promo Offer", sent = 122, delivered = 118, failed = 4, totalRecipients = 122),
            Campaign(2, "Holiday Greeting", sent = 500, delivered = 480, failed = 20, totalRecipients = 500),
            Campaign(3, "Renewal Reminder", sent = 60, delivered = 55, failed = 5, totalRecipients = 60)
        )
        adapter.submitList(data)
        empty.visibility = if (data.isEmpty()) View.VISIBLE else View.GONE
    }
}
