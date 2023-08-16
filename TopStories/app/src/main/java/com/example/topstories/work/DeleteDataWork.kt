package com.example.topstories.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.topstories.database.NewsDatabase.Companion.getDatabase
import com.example.topstories.repository.NewsRepository
import retrofit2.HttpException

class DeleteDataWork(appContext: Context, params : WorkerParameters) : CoroutineWorker(appContext, params){

    companion object{
        const val WORK_NAME = "DeleteDataWorker"
    }
    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repository = NewsRepository(database)
        return try {
            repository.deleteOldNews()
            Result.success()
        }catch (e:HttpException){
            Result.failure()
        }
    }

}