package com.nk.mathpractice.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nk.mathpractice.R
import com.nk.mathpractice.databinding.ItemHistoryBinding
import com.nk.mathpractice.models.HistoryItem

class HistoryAdapter : ListAdapter<HistoryItem, HistoryAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HistoryItem) {
            val context = binding.root.context
            binding.tvExpression.text = item.expression
            binding.tvUserAnswer.text = "${context.getString(R.string.your_answer_)} ${item.userAnswer}"
            binding.tvCorrectAnswer.text = "${context.getString(R.string.correct_)} ${item.correctAnswer}"

            if (item.isCorrect) {
                binding.tvUserAnswer.setTextColor(Color.parseColor("#4CAF50"))
                binding.tvCorrectAnswer.visibility = View.GONE
            } else {
                binding.tvUserAnswer.setTextColor(Color.RED)
                binding.tvCorrectAnswer.visibility = View.VISIBLE
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<HistoryItem>() {
        override fun areItemsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean =
            oldItem == newItem
        override fun areContentsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean =
            oldItem == newItem
    }
}
