package com.cornershop.counterstest.data.db

import androidx.room.*
import com.cornershop.counterstest.model.data.Counter

@Dao
interface CounterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(counter: List<Counter>)

    @Update
    suspend fun update(vararg counter: Counter)

    @Delete
    suspend fun delete(counter: Counter)

    @Query("SELECT * FROM counterItem")
    suspend fun getCounter(): List<Counter>

    @Query("DELETE FROM counterItem")
    suspend fun deleteAll()
}