package com.example.freespace.di

import com.example.freespace.firebase.impl.AccountServiceImpl
import com.example.freespace.firebase.impl.StorageServiceImpl
import com.example.freespace.firebase.service.AccountService
import com.example.freespace.firebase.service.StorageService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {

    @Binds
    abstract fun provideAccountService(impl: AccountServiceImpl): AccountService


    @Binds
    abstract fun provideStorageService(impl: StorageServiceImpl): StorageService


}