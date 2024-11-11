package com.example.freespace.workmanager

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.freespace.data.RepositoryImpl
import com.example.freespace.ui.login.isNetworkAvailable
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: RepositoryImpl
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            if (!isNetworkAvailable(context = applicationContext)) {
                return Result.retry()
            }
            repository.syncNotes()
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