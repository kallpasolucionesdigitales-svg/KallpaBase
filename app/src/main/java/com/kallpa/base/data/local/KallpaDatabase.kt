package com.kallpa.base.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ClientEntity::class], version = 1, exportSchema = false)
abstract class KallpaDatabase : RoomDatabase() {
    abstract fun clientDao(): ClientDao

    companion object {
        const val DATABASE_NAME = "kallpa.db"
    }
}
