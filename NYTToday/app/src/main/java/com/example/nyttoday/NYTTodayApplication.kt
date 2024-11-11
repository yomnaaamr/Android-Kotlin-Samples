package com.example.nyttoday

import android.app.Application
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.nyttoday.workmanager.HiltWorkerFactory
import com.example.nyttoday.workmanager.SyncWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@HiltAndroidApp
class NYTTodayApplication: Application(), Configuration.Provider  {


    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var workManager: WorkManager

    private lateinit var refreshRequest: PeriodicWorkRequest


    override fun onCreate() {
        super.onCreate()
        // Enable disk persistence
//        FirebaseApp.initializeApp(this)
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true)


        scheduleNewsRefresh()
    }


    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }


    private fun scheduleNewsRefresh() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

         refreshRequest = PeriodicWorkRequestBuilder<SyncWorker>(12, TimeUnit.HOURS)
            .setConstraints(constraints).build()

        workManager.enqueueUniquePeriodicWork(
            "NewsRefreshWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            refreshRequest
        )
    }


    fun cancelNewsRefresh() {
        workManager.cancelWorkById(refreshRequest.id)
    }


    override fun onTerminate() {
        super.onTerminate()
        // Cancel all work on application termination to prevent leaks
        workManager.cancelAllWork()
    }


}