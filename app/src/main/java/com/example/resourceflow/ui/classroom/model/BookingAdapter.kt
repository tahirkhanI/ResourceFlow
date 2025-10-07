package com.example.resourceflow.ui.classroom.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.resourceflow.network.ClassroomDto
import com.example.resourceflow.R

class BookingAdapter(
    private val classrooms: List<ClassroomDto>,
    private val onItemClick: (ClassroomDto) -> Unit
) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    inner class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvClassroomName: TextView = itemView.findViewById(R.id.tvClassroomName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_classroom, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val classroom = classrooms[position]
        holder.tvClassroomName.text = classroom.name

        holder.itemView.setOnClickListener {
            onItemClick(classroom)
        }
    }

    override fun getItemCount(): Int = classrooms.size
}
