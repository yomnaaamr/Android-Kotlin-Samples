package com.example.bookreview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bookreview.data.Book
import com.example.bookreview.databinding.ItemListBookBinding
import com.example.bookreview.util.setRate

class BookListAdapter(private val onBookClicked : (Book) -> Unit ) :
      ListAdapter<Book,BookListAdapter.BookViewHolder>(DiffCallback){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BookViewHolder {
        return BookViewHolder(
            ItemListBookBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    class BookViewHolder(private var binding : ItemListBookBinding) :
          RecyclerView.ViewHolder(binding.root) {
              fun bind(book: Book){
                  binding.apply {
                      bookNameText.text = book.bookName
                      imageViewIcon.setImageResource(setRate(book.bookRate))
                  }
              }

    }

    override fun onBindViewHolder(holder:BookViewHolder, position: Int) {
         val current = getItem(position)
        holder.itemView.setOnClickListener {
            onBookClicked(current)
        }
        holder.bind(current)
    }

    companion object{
        private val DiffCallback = object : DiffUtil.ItemCallback<Book>(){
            override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
                return oldItem.bookName == newItem.bookName
            }

        }
    }
}