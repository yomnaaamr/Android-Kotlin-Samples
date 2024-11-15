package com.example.freespace.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

//    same as below

//    @Provides
//    @Singleton
//    fun provideFirestore(): FirebaseFirestore {
//        return FirebaseFirestore.getInstance()
//    }
//
//    @Provides
//    @Singleton
//    fun provideAuth(): FirebaseAuth {
//        return FirebaseAuth.getInstance()
//    }


    @Provides
    @Singleton
    fun auth(): FirebaseAuth = Firebase.auth

    @Provides
    @Singleton
    fun firestore(): FirebaseFirestore = Firebase.firestore

}