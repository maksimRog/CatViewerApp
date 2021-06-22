package com.mraha.imagesearchapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mraha.imagesearchapp.data.Photo

@Dao
interface ModelDao {
    @Query("SELECT * FROM Photo")
     fun getAll(): LiveData<List<Photo>>

    @Insert
    suspend fun insertAll(vararg users: Photo)
}