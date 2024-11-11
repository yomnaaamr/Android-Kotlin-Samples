package com.example.freespace.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.example.freespace.data.FreeSpaceDatabase
import com.example.freespace.data.NoteDao
import com.example.freespace.data.NotesRepository
import com.example.freespace.data.RepositoryImpl
import com.example.freespace.firebase.service.StorageService
import com.example.freespace.workmanager.HiltWorkerFactory
import com.example.freespace.workmanager.SyncWorker
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NoteModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application): FreeSpaceDatabase {
        return Room.databaseBuilder(
            app,
            FreeSpaceDatabase::class.java, "note_datebase"
        )
            .fallbackToDestructiveMigration()
            .build()
    }


    @Provides
    @Singleton
    fun provideNoteDao(database: FreeSpaceDatabase): NoteDao {
        return database.noteDao()
    }


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


    @Provides
    @Singleton
    fun provideRepository(
//        database: FreeSpaceDatabase,
        noteDao: NoteDao,
//        firestore: FirebaseFirestore,
        workManager: WorkManager,
//        auth: AccountService,
        context: Context,
        storageService: StorageService
    )
            : NotesRepository {

        return RepositoryImpl(
//            database.noteDao(),
            noteDao,
//            firestore,
            workManager,
//            auth,
            context,
            storageService

        )
    }


    @Provides
    @Singleton
    fun provideApplicationContext(application: Application): Context {
        return application.applicationContext
    }


    @Provides
    @Singleton
    fun provideSignInClient(@ApplicationContext context: Context): SignInClient {
        return Identity.getSignInClient(context)
    }


}