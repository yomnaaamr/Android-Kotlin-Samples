package com.example.topstories.domain

data class AppNewsModel(
    val title : String?,
    val subTitle : String?,
    val url : String,
    val byLine : String?,
    val updatedDate : String?,
    val imageUrl : String?,
    val copyRight : String?
)