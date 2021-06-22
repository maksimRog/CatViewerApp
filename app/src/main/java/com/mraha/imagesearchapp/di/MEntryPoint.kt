package com.mraha.imagesearchapp.di

import com.mraha.imagesearchapp.database.AppDatabase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface MEntryPoint {
    fun getDataBase(): AppDatabase
}