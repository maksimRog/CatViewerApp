package com.mraha.imagesearchapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mraha.imagesearchapp.data.UnsplashPhoto

@Database(entities = [UnsplashPhoto::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDao(): ModelDao
}