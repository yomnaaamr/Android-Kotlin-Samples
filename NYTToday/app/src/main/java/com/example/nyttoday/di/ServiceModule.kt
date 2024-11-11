package com.example.nyttoday.di

import com.example.nyttoday.firebase.impl.AccountServiceImpl
import com.example.nyttoday.firebase.impl.StorageServiceImpl
import com.example.nyttoday.firebase.service.AccountService
import com.example.nyttoday.firebase.service.StorageService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule
{

    @Binds
    abstract fun provideAccountService(impl: AccountServiceImpl): AccountService


    @Binds
    abstract fun provideStorageService(impl: StorageServiceImpl): StorageService

}