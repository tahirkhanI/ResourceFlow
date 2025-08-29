package com.example.resourceflow.adapter

import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.resourceflow.R
import com.example.resourceflow.model.User

class UserAdapter(
    private val users: List<User>,
    private val selectionListener: OnUserSelectionListener
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private val selectedIds = mutableSetOf<Int>()

    interface OnUserSelectionListener {
        fun onUserSelected(userId: Int)
        fun onUserDeselected(userId: Int)
    }

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textUserName: TextView = view.findViewById(R.id.textUserName)
        val textUserEmail: TextView = view.findViewById(R.id.textUserEmail)
        val textUserRole: TextView = view.findViewById(R.id.textUserRole)
        val rootView: View = view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_item, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.textUserName.text = user.name
        holder.textUserEmail.text = user.email
        holder.textUserRole.text = user.role.capitalize()
        // Highlight if selected
        val isSelected = selectedIds.contains(user.id)
        holder.rootView.setBackgroundResource(
            if (isSelected) R.color.gray_light else android.R.color.transparent
        )

        holder.rootView.setOnClickListener {
            val selected = selectedIds.contains(user.id)
            if (selected) {
                selectedIds.remove(user.id)
                selectionListener.onUserDeselected(user.id)
            } else {
                selectedIds.add(user.id)
                selectionListener.onUserSelected(user.id)
            }
            notifyItemChanged(position)
        }
    }

    fun clearSelection() {
        selectedIds.clear()
        notifyDataSetChanged()
    }
}
