package com.example.bookreview.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.bookreview.BookReviewApplication
import com.example.bookreview.R
import com.example.bookreview.data.Book
import com.example.bookreview.databinding.FragmentBookDetailBinding
import com.example.bookreview.util.setRate
import com.example.bookreview.viewModel.BookReviewViewModelFactory
import com.example.bookreview.viewModel.BookViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class BookDetailFragment : Fragment() {

    private val viewModel : BookViewModel by activityViewModels {
        BookReviewViewModelFactory(
            (activity?.application as BookReviewApplication).database.bookDao()
        )
    }

    lateinit var book: Book

    private val navigationArgs : BookDetailFragmentArgs by navArgs()

    private var _binding : FragmentBookDetailBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBookDetailBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = navigationArgs.bookId

        viewModel.retrieveBook(id).observe(this.viewLifecycleOwner){selectedBook ->
            book = selectedBook
            bind(book)
        }
    }

    private fun bind(book: Book){
        binding.apply {
            IconImageView.setImageResource(setRate(book.bookRate))
            BookNameTV.text = book.bookName
            AuthorNameTV.text = getString(R.string.author_name_tv,book.bookAuthor)
            NumPagesTV.text = getString(R.string.number_of_pages_s,book.bookNumPage)
            ReviewTV.text = getString(R.string.review_s,book.bookReview)

            deleteAction.setOnClickListener { showConfirmationDialog() }
            editItem.setOnClickListener { editBook() }
        }
    }

    private fun deleteBook(){
        viewModel.deleteBook(book)
        findNavController().navigateUp()
    }

    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.dialog_alert_title))
            .setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) {_,_ ->}
            .setPositiveButton(getString(R.string.yes)) {_,_ ->
                deleteBook()
            }
            .show()
    }


    private fun editBook(){
        val action = BookDetailFragmentDirections.actionBookDetailFragmentToAddBookFragment(
            getString(R.string.edit_fragment_title),
            book.id
        )
        this.findNavController().navigate(action)
    }

    /**
    * Called when fragment is destroyed.
    */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}