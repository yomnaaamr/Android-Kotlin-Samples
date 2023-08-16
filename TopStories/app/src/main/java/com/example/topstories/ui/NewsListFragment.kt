package com.example.topstories.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.topstories.adapter.NewsListAdapter
import com.example.topstories.databinding.FragmentNewsListBinding
import com.example.topstories.viewModels.NewsViewModel


class NewsListFragment : Fragment() {

    private val newsViewModel : NewsViewModel by viewModels ()

    private var _binding : FragmentNewsListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNewsListBinding.inflate(inflater)

        binding.apply {
            lifecycleOwner = this@NewsListFragment
            viewModel = newsViewModel
            recyclerView.adapter = NewsListAdapter {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.url))
                startActivity(intent)

            }
        }

        newsViewModel.eventNetworkError.observe(viewLifecycleOwner) { isNetworkError ->
            if (isNetworkError) onNetworkError()
        }


        return binding.root
    }

    /**
     * Method for displaying a Toast error message for network errors.
     */
    private fun onNetworkError() {
        if(!newsViewModel.isNetworkErrorShown.value!!) {
            Toast.makeText(activity, "Network Error", Toast.LENGTH_LONG).show()
            newsViewModel.onNetworkErrorShown()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}