package com.example.topstories.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.topstories.databinding.ItemListBinding
import com.example.topstories.domain.AppNewsModel


class NewsListAdapter (private val onItemClicked : (AppNewsModel) -> Unit):
    ListAdapter<AppNewsModel, NewsListAdapter.NewsViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NewsViewHolder {

        val viewHolder = NewsViewHolder(
            ItemListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,false
            )
        )
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            onItemClicked(getItem(position))
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    class NewsViewHolder (
        private var binding : ItemListBinding
    ) : RecyclerView.ViewHolder(binding.root){

        fun bind(news: AppNewsModel){
            binding.item = news
            binding.executePendingBindings()

        }

    }


    companion object DiffCallback : DiffUtil.ItemCallback<AppNewsModel>() {
        override fun areItemsTheSame(oldItem: AppNewsModel, newItem: AppNewsModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: AppNewsModel, newItem: AppNewsModel): Boolean {
            return  oldItem.url == newItem.url
        }

    }
}