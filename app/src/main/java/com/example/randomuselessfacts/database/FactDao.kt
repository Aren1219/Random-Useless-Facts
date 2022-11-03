package com.example.randomuselessfacts.database

import androidx.room.*
import com.example.randomuselessfacts.model.Fact
import kotlinx.coroutines.flow.Flow

@Dao
interface FactDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFact(fact: Fact)

    @Query("SELECT * FROM facts")
    fun getAllFacts(): Flow<List<Fact>>

    @Delete
    suspend fun deleteFact(data: Fact)

    @Query("SELECT EXISTS(SELECT * FROM facts WHERE id = :id)")
    fun isFactSaved(id : String) : Boolean
}