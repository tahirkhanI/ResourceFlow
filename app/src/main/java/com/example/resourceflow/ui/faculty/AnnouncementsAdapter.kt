package com.example.resourceflow.ui.faculty

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.resourceflow.R

class AnnouncementsAdapter(
    private val announcements: List<Announcement>
) : RecyclerView.Adapter<AnnouncementsAdapter.AnnouncementViewHolder>() {

    class AnnouncementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvTag: TextView = itemView.findViewById(R.id.tvTag)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val tvTimeAgo: TextView = itemView.findViewById(R.id.tvTimeAgo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnouncementViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_announcement, parent, false)
        return AnnouncementViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnnouncementViewHolder, position: Int) {
        val announcement = announcements[position]
        holder.tvTitle.text = announcement.title
        holder.tvTag.text = announcement.tag
        holder.tvDescription.text = announcement.description
        holder.tvTimeAgo.text = announcement.timeAgo
    }

    override fun getItemCount() = announcements.size
}
