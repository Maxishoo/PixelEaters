package com.example.pixeleaters.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pixeleaters.data.model.Reminder
import com.example.pixeleaters.databinding.ItemReminderBinding

class MainAdapter(
    private val onItemClick: (String) -> Unit
) : ListAdapter<Reminder, MainAdapter.ReminderViewHolder>(ReminderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val binding = ItemReminderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReminderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ReminderViewHolder(
        private val binding: ItemReminderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(reminder: Reminder) {
            binding.apply {
                textViewTitle.text = reminder.title
                textViewDate.text = reminder.date
                textViewTime.text = reminder.time
                textViewDescription.text = reminder.description
                textViewContext.text = reminder.context ?: ""
                textViewTag.text = reminder.tag ?: ""

                // Скрываем необязательные поля, если пустые
                textViewContext.isVisible = !reminder.context.isNullOrBlank()
                textViewTag.isVisible = !reminder.tag.isNullOrBlank()

                root.setOnClickListener {
                    onItemClick(reminder.id)
                }
            }
        }
    }
}

class ReminderDiffCallback : DiffUtil.ItemCallback<Reminder>() {
    override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
        return oldItem == newItem
    }
}