package com.pratclot.dogs.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pratclot.dogs.databinding.FavouritesItemBinding
import com.pratclot.dogs.domain.LikedBreed

class LikeListAdapter(
    val clickListener: LikeClickListener
) : ListAdapter<LikedBreed, LikeListAdapter.ViewHolder>(
    LikeDiffCallback()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LikeListAdapter.ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: LikeListAdapter.ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
    }

    class ViewHolder(private val binding: FavouritesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LikedBreed, clickListener: LikeClickListener) {
            binding.likedBreed = item
            binding.listener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FavouritesItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class LikeDiffCallback : DiffUtil.ItemCallback<LikedBreed>() {
    override fun areItemsTheSame(oldItem: LikedBreed, newItem: LikedBreed): Boolean {
        return oldItem.breed == newItem.breed
    }

    override fun areContentsTheSame(oldItem: LikedBreed, newItem: LikedBreed): Boolean {
        return areItemsTheSame(oldItem, newItem)
    }
}

class LikeClickListener(val clickListener: (likedBreed: LikedBreed) -> Unit) {
    fun onClick(likedBreed: LikedBreed) {
        clickListener(likedBreed)
    }
}
