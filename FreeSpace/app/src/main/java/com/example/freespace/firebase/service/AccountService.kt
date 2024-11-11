package com.example.freespace.firebase.service

import android.content.Intent
import android.content.IntentSender
import com.example.freespace.firebase.SignInResult
import com.example.freespace.firebase.User
import kotlinx.coroutines.flow.Flow

interface AccountService {

    val currentUserId: String
    val hasUser: Boolean
    val currentUser: Flow<User>

    suspend fun authenticate(email: String, password: String)
    suspend fun sendRecoveryEmail(email: String)
    suspend fun signUp(email: String, password: String, displayName: String)
    suspend fun deleteAccount()
    suspend fun signOut()

//    suspend fun linkAccount(email: String, password: String)
    suspend fun signInWithIntent(intent: Intent): SignInResult

    suspend fun signInWithGoogle(): IntentSender?


}