package com.example.bookreview.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.bookreview.BookReviewApplication
import com.example.bookreview.R
import com.example.bookreview.data.Book
import com.example.bookreview.databinding.FragmentAddBookBinding
import com.example.bookreview.viewModel.BookReviewViewModelFactory
import com.example.bookreview.viewModel.BookViewModel


class AddBookFragment : Fragment() {

    private val viewModel : BookViewModel by activityViewModels {
        BookReviewViewModelFactory(
            (activity?.application as BookReviewApplication).database.bookDao()
        )
    }

    lateinit var book: Book

    private var rate = 0

    private val navigationArgs : BookDetailFragmentArgs by navArgs()

    private var _binding : FragmentAddBookBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddBookBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val clickListener = View.OnClickListener { view ->
            when(view.id){
                R.id.imageView  -> rate = 0
                R.id.imageView2 -> rate = 1
                R.id.imageView3 -> rate = 2
                R.id.imageView4 -> rate = 3
                R.id.imageView5 -> rate = 4
                R.id.imageView6 -> rate = 5
            }
        }

        binding.imageView.setOnClickListener (clickListener)
        binding.imageView2.setOnClickListener (clickListener)
        binding.imageView3.setOnClickListener (clickListener)
        binding.imageView4.setOnClickListener (clickListener)
        binding.imageView5.setOnClickListener (clickListener)
        binding.imageView6.setOnClickListener (clickListener)


        val id = navigationArgs.bookId
        if(id > 0){
            viewModel.retrieveBook(id).observe(this.viewLifecycleOwner){selectedBook ->
                book = selectedBook
                bind(book)
            }
        }else{
            binding.saveAction.setOnClickListener { addNewBook() }
        }

        binding.saveAction.setOnClickListener { addNewBook() }


    }

    private fun isEntryValid() : Boolean{
        return viewModel.isEntryValid(
            binding.bookName.text.toString(),
            binding.bookAuthor.text.toString(),
            binding.bookNumPage.text.toString(),
            binding.bookReview.text.toString(),
            bookRate = rate
        )
    }


    private fun addNewBook(){
        if(isEntryValid()){
            viewModel.addNewBook(
                binding.bookName.text.toString(),
                binding.bookAuthor.text.toString(),
                binding.bookNumPage.text.toString(),
                binding.bookReview.text.toString(),
                bookRate = rate

            )
            val action = AddBookFragmentDirections.actionAddBookFragmentToBookListFragment()
            findNavController().navigate(action)
        }else{
           Toast.makeText(requireContext(),"Fill All Entries",Toast.LENGTH_LONG).show()
        }
    }


    private fun bind(book: Book){
        binding.apply {
            bookName.setText(book.bookName,TextView.BufferType.SPANNABLE)
            bookAuthor.setText(book.bookAuthor,TextView.BufferType.SPANNABLE)
            bookNumPage.setText(book.bookNumPage,TextView.BufferType.SPANNABLE)
            bookReview.setText(book.bookReview,TextView.BufferType.SPANNABLE)

            saveAction.setOnClickListener { updateBook() }

        }
    }

    private fun updateBook() {
        if(isEntryValid()){
            viewModel.updateBook(
                this.navigationArgs.bookId,
                this.binding.bookName.text.toString(),
                this.binding.bookAuthor.text.toString(),
                this.binding.bookNumPage.text.toString(),
                this.binding.bookReview.text.toString(),
                bookRate = rate
            )
            val action = AddBookFragmentDirections.actionAddBookFragmentToBookListFragment()
            findNavController().navigate(action)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}