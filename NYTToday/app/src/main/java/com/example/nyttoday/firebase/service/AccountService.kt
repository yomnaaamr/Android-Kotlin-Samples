package com.example.nyttoday.firebase.service

import android.content.Intent
import android.content.IntentSender
import com.example.nyttoday.firebase.SignInResult

interface AccountService {



    val currentUserId: String
    val hasUser: Boolean
    suspend fun authenticate(email: String, password: String)
    suspend fun sendRecoveryEmail(email: String)
    suspend fun signUp(email: String, password: String, displayName: String)
    suspend fun deleteAccount()
    suspend fun signOut()

    suspend fun signInWithIntent(intent: Intent): SignInResult

    suspend fun signInWithGoogle(): IntentSender?


}