package com.example.bookreview.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.bookreview.BookReviewApplication
import com.example.bookreview.R
import com.example.bookreview.adapter.BookListAdapter
import com.example.bookreview.databinding.FragmentBookListBinding
import com.example.bookreview.viewModel.BookReviewViewModelFactory
import com.example.bookreview.viewModel.BookViewModel


class BookListFragment : Fragment() {

    private val viewModel : BookViewModel by activityViewModels {
        BookReviewViewModelFactory(
            (activity?.application as BookReviewApplication).database.bookDao()
        )
    }

    private var _binding : FragmentBookListBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBookListBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = BookListAdapter{
            val action = BookListFragmentDirections.actionBookListFragmentToBookDetailFragment(it.id)
            this.findNavController().navigate(action)
        }

        binding.recyclerView.adapter = adapter
        viewModel.allBooks.observe(this.viewLifecycleOwner){books ->
            books.let {
                adapter.submitList(it)
            }
        }


        binding.floatingActionButton.setOnClickListener {
            val action = BookListFragmentDirections.actionBookListFragmentToAddBookFragment(
                getString(R.string.add_fragment_title)
            )
            this.findNavController().navigate(action)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}