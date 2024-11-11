package com.example.nyttoday.firebase.impl

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.example.nyttoday.R
import com.example.nyttoday.firebase.SignInResult
import com.example.nyttoday.firebase.User
import com.example.nyttoday.firebase.service.AccountService
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class AccountServiceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val context: Context,
//    private val weakContext: WeakReference<Context>,
    private val oneTapClient: SignInClient,
    private val db: FirebaseDatabase
) : AccountService {


    override val currentUserId: String
        get() = auth.currentUser?.uid.orEmpty()


    override val hasUser: Boolean
        get() = auth.currentUser != null
    override suspend fun authenticate(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun sendRecoveryEmail(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    override suspend fun signUp(email: String, password: String, displayName: String) {
        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        val user = authResult.user
        if (user != null) {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()

            user.updateProfile(profileUpdates).await()

        } else {
            throw FirebaseAuthException("", "User is null after sign-up")
        }
    }

    override suspend fun deleteAccount() {
        auth.currentUser!!.delete().await()
//        oneTapClient.signOut().await()
//        auth.signOut()
        signOut()
        // Clears cached data
        db.purgeOutstandingWrites()
    }

    override suspend fun signOut() {
        oneTapClient.signOut().await()
        auth.signOut()
    }

    override suspend fun signInWithIntent(intent: Intent): SignInResult {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)

        return try {
            val user = auth.signInWithCredential(googleCredentials).await().user
            SignInResult(
                data = user?.run {
                    User(
                        id = uid,
                        userName = displayName,
                        profilePicture = photoUrl?.toString()
                    )
                },
                errorMessage = null
            )
        }catch(e: Exception) {
            e.printStackTrace()
            if(e is CancellationException) throw e
            SignInResult(
                data = null,
                errorMessage = e.message
            )
        }
    }

    override suspend fun signInWithGoogle(): IntentSender? {
        val result = try {
            oneTapClient.beginSignIn(
                buildSignInRequest()
            ).await()
        } catch(e: Exception) {
            e.printStackTrace()
            if(e is CancellationException) throw e
            null
        }
        return result?.pendingIntent?.intentSender
    }


    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(
                        context.applicationContext.getString(R.string.web_client_id)
//                        weakContext.get()?.applicationContext?.getString(R.string.web_client_id)!!
                    )
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }
}