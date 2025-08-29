package com.example.resourceflow.ui.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.resourceflow.R
import com.example.resourceflow.ui.admin.Report

class ReportsAdapter(
    private var reports: List<Report>,
    private val onLongPress: (Report) -> Unit
) : RecyclerView.Adapter<ReportsAdapter.ReportViewHolder>() {

    inner class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconType: ImageView = itemView.findViewById(R.id.iconType)
        val textTitle: TextView = itemView.findViewById(R.id.textTitle)
        val textDescription: TextView = itemView.findViewById(R.id.textDescription)
        val textTimestamp: TextView = itemView.findViewById(R.id.textTimestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_report, parent, false)
        return ReportViewHolder(view)
    }

    override fun getItemCount(): Int = reports.size

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = reports[position]

        val iconRes = when (report.status.lowercase()) {
            "pending" -> android.R.drawable.ic_menu_recent_history
            "in progress" -> android.R.drawable.ic_popup_sync
            "completed", "solved" -> android.R.drawable.ic_menu_agenda
            else -> android.R.drawable.ic_dialog_info
        }
        holder.iconType.setImageResource(iconRes)

        holder.textTitle.text = "Classroom: ${report.classroomNumber}"
        holder.textDescription.text = "${report.problemDescription} (by ${report.reporterName})"
        holder.textTimestamp.text = "5 days ago" // You can improve date formatting here

        holder.itemView.setOnLongClickListener {
            onLongPress(report)
            true
        }
    }

    fun updateList(newList: List<Report>) {
        reports = newList
        notifyDataSetChanged()
    }
}
