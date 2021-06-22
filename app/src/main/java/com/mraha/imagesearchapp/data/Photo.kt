package com.mraha.imagesearchapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Photo(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    var id: String? = null,
    var url: String? = null,
)