package com.example.randomuselessfacts.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.randomuselessfacts.model.Fact

@Dao
interface FactDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertFact(fact: Fact)

    @Query("SELECT * FROM facts")
    fun getAllFacts(): LiveData<List<Fact>>

    @Delete
    suspend fun deleteFact(data: Fact)
}