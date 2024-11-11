package com.example.nyttoday.di

import android.app.Application
import androidx.work.WorkManager
import com.example.nyttoday.BuildConfig
import com.example.nyttoday.data.NewsApi
import com.example.nyttoday.workmanager.HiltWorkerFactory
import com.example.nyttoday.workmanager.SyncWorker
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private val json = Json {
        ignoreUnknownKeys = true
    }


    @Provides
    @Singleton
    @Named("BaseUrl")
    fun provideBaseUrl(): String = "https://api.nytimes.com/svc/"

    @Provides
    @Singleton
    @Named("ApiKey")
    fun provideApiKey(): String {
        return BuildConfig.NYT_API_KEY
    }


    @Provides
    @Singleton
    fun provideOkHttpClient(@Named("ApiKey") apiKey: String): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val urlWithApiKey = originalRequest.url.newBuilder()
                    .addQueryParameter("api-key", apiKey)
                    .build()
                val newRequest = originalRequest.newBuilder().url(urlWithApiKey).build()
                chain.proceed(newRequest)
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient,
                        @Named("BaseUrl") baseUrl: String
    ): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): NewsApi {
        return retrofit.create(NewsApi::class.java)
    }


//    @OptIn(ExperimentalPagingApi::class)
//    @Provides
//    @Singleton
//    fun providePager(repository: Repository,storageService: StorageService) : Pager<String , FirebaseNewItem>{
//
//        return Pager(
//            config = PagingConfig(pageSize = 10),
//            remoteMediator = RemoteMediator(repository,storageService),
//            pagingSourceFactory = {
//                repository.getNewsFromRealtimeDatabase()
//            }
//        )
//    }



    @Provides
    @Singleton
    fun provideWorkManager(app: Application): WorkManager {
        return WorkManager.getInstance(app.applicationContext)
    }


    @Provides
    @Singleton
    fun provideWorkerFactory(
        syncWorkerFactory: SyncWorker.Factory
    ): HiltWorkerFactory {
        return HiltWorkerFactory(syncWorkerFactory)
    }


}