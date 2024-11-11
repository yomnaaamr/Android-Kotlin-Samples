package com.example.freespace.firebase.impl

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.example.freespace.R
import com.example.freespace.firebase.SignInResult
import com.example.freespace.firebase.User
import com.example.freespace.firebase.service.AccountService
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class AccountServiceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val context: Context,
    private val oneTapClient: SignInClient
) : AccountService {

    override val currentUserId: String
        get() = auth.currentUser?.uid.orEmpty()


    override val hasUser: Boolean
        get() = auth.currentUser != null

    override val currentUser: Flow<User>
        get() = callbackFlow {
            val listener = FirebaseAuth.AuthStateListener { auth ->
                this.trySend(auth.currentUser?.let {
                    User(
                        it.uid,
                        it.displayName ?: "Unknown",
                        profilePicture = it.photoUrl.toString()
                    )
                } ?: User())
            }

            auth.addAuthStateListener(listener)
            awaitClose { auth.removeAuthStateListener(listener) }
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
                    .setServerClientId(context.getString(R.string.web_client_id))
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }




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
                // You can also set other profile information here if needed
                // .setPhotoUri(Uri.parse("https://example.com/photo.jpg"))
                .build()

            user.updateProfile(profileUpdates).await()

//        auth.createUserWithEmailAndPassword(email,password).await()
        } else {
            throw FirebaseAuthException("", "User is null after sign-up")
        }

    }


    override suspend fun deleteAccount() {
        auth.currentUser!!.delete().await()
    }


    override suspend fun signOut() {
        oneTapClient.signOut().await()
        auth.signOut()
    }

}