package com.mraha.imagesearchapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mraha.imagesearchapp.data.UnsplashPhoto

@Dao
interface ModelDao {
    @Query("SELECT * FROM UnsplashPhoto")
     fun getAll(): List<UnsplashPhoto>

    @Insert
     fun insertAll(vararg users: UnsplashPhoto)
}