package com.example.topstories.util


import android.view.View
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.topstories.R
import com.example.topstories.adapter.NewsListAdapter
import com.example.topstories.domain.AppNewsModel


@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data : List<AppNewsModel>?){
    val adapter = recyclerView.adapter as NewsListAdapter
    adapter.submitList(data)
}


@BindingAdapter("imageUrl")
fun bindImage(imgView : ImageView, imgUrl : String?){

    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions()
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.ic_broken_image))
            .into(imgView)
    }
}




/**
 * Binding adapter used to hide the spinner once data is available.
 */
@BindingAdapter("isNetworkError", "news")
fun hideIfNetworkError(view: View, isNetWorkError: Boolean, news: Any?) {
    view.visibility = if (news != null) View.GONE else View.VISIBLE

    if(isNetWorkError) {
        view.visibility = View.GONE
    }
}

