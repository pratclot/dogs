package com.pratclot.dogs.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pratclot.dogs.databinding.ListItemBinding
import com.pratclot.dogs.domain.Breed

class BreedListAdapter(
    val clickListener: ClickListener
) : ListAdapter<Breed, BreedListAdapter.ViewHolder>(
    DiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(
            parent
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
    }

    class ViewHolder(private val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Breed, clickListener: ClickListener) {
            binding.breed = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(
                    binding
                )
            }
        }
    }
}

class DiffCallback : DiffUtil.ItemCallback<Breed>() {
    override fun areItemsTheSame(oldItem: Breed, newItem: Breed): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Breed, newItem: Breed): Boolean {
        return areItemsTheSame(oldItem, newItem)
    }
}

class ClickListener(val clickListener: (breed: Breed) -> Unit) {
    fun onClick(breed: Breed) {
        clickListener(breed)
    }
}
