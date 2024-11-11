package com.example.nyttoday.workmanager

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.nyttoday.repository.Repository
import com.example.nyttoday.ui.login.isNetworkAvailable
import com.google.firebase.auth.FirebaseAuth
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Inject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: Repository
//    context.applicationContext instead if context to avoid memory leaks
) : CoroutineWorker(context.applicationContext, params) {


//    private val weakContext: WeakReference<Context> = WeakReference(context)


    @Inject
    lateinit var auth: FirebaseAuth // Inject FirebaseAuth to check user state


    override suspend fun doWork(): Result {
        return try {
            if (auth.currentUser == null) {
                return Result.success() // No need to refresh if not logged in
            }
            if (!isNetworkAvailable(context = applicationContext)) {
                return Result.retry()
            }
            repository.refreshNewsEvery12Hours()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }


    @AssistedFactory
    interface Factory {
        fun create(context: Context, params: WorkerParameters): SyncWorker
    }
}