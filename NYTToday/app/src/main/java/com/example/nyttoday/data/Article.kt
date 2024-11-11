package com.example.nyttoday.data

import androidx.compose.runtime.Immutable


@Immutable
data class Article(
    val title: String? = "",
    val abstract: String? = "",
    val uri: String? = "",
    val url: String? = "",
    val byline: String? = "",
    val imageUrl: String? = "",
    val copyright: String? = "",
)