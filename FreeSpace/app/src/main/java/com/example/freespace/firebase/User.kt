package com.example.freespace.firebase

data class User(
    val id: String = "",
    val userName: String? = "",
    val profilePicture: String? = null,
    val notes: List<FirebaseNote> = emptyList()
)


data class SignInResult(
    val data: User?,
    val errorMessage: String?
)