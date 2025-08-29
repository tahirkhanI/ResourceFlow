package com.example.resourceflow.ui.classroom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.resourceflow.R
import com.example.resourceflow.ui.classroom.model.ClassroomUi

class ClassroomAdapter(
    private var classrooms: List<ClassroomUi>,
    private val onBookClick: (ClassroomUi) -> Unit
) : RecyclerView.Adapter<ClassroomAdapter.ClassroomViewHolder>() {

    inner class ClassroomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRoomName: TextView = itemView.findViewById(R.id.tvRoomName)
        val tvAvailable: TextView = itemView.findViewById(R.id.tvAvailable)
        val tvCapacity: TextView = itemView.findViewById(R.id.tvCapacity)
        val tvPower: TextView = itemView.findViewById(R.id.tvPower)
        val tvTables: TextView = itemView.findViewById(R.id.tvTables)
        val tvChairs: TextView = itemView.findViewById(R.id.tvChairs)
        val icProjector: ImageView = itemView.findViewById(R.id.icProjector)
        val icAC: ImageView = itemView.findViewById(R.id.icAC)
        val icFan: ImageView = itemView.findViewById(R.id.icFan)
        val icWifi: ImageView = itemView.findViewById(R.id.icWifi)
        val btnBook: Button = itemView.findViewById(R.id.btnBook)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassroomViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_classroom, parent, false)
        return ClassroomViewHolder(v)
    }

    override fun onBindViewHolder(holder: ClassroomViewHolder, position: Int) {
        val c = classrooms[position]

        holder.tvRoomName.text = c.name
        // If status not used: holder.tvAvailable.isVisible = false

        holder.tvCapacity.text = "Capacity: ${c.capacity} students"
        holder.tvPower.text = "Plugs: ${c.powerOutlets}"
        holder.tvTables.text = "Tables: ${c.tables}"
        holder.tvChairs.text = "Chairs: ${c.chairs}"

        holder.icProjector.isVisible = c.hasProjector
        holder.icAC.isVisible = c.hasAc
        holder.icFan.isVisible = c.hasFan
        holder.icWifi.isVisible = c.hasWifi

        holder.btnBook.setOnClickListener { onBookClick(c) }
    }

    override fun getItemCount(): Int = classrooms.size

    fun updateList(newList: List<ClassroomUi>) {
        classrooms = newList
        notifyDataSetChanged()
    }
}
