package com.example.nyttoday.firebase

import com.example.nyttoday.data.ResultsItem

data class User(
    val id: String = "",
    val userName: String? = "",
    val profilePicture: String? = null,
    val news: List<ResultsItem> = emptyList(),
    val savedNews: List<ResultsItem> = emptyList()
)


data class SignInResult(
    val data: User?,
    val errorMessage: String?
)