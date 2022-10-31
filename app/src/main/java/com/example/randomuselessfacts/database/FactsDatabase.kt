package com.example.randomuselessfacts.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.randomuselessfacts.model.Fact

@Database(
    entities = [Fact::class],
    version = 1,
    exportSchema = false
)
abstract class FactsDatabase : RoomDatabase() {
    abstract fun getDao(): FactDao
}