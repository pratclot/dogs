package com.pratclot.dogs.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LikeEntity::class], version = 1, exportSchema = false)
abstract class LikeDb: RoomDatabase() {
    abstract fun getDao(): LikeDbDao
}