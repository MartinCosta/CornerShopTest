package com.cornershop.counterstest.model.repository

import com.cornershop.counterstest.model.data.Counter
import kotlinx.coroutines.flow.Flow

interface CounterRepository {
    fun addCounter(title: String): Flow<List<Counter>>
    fun getCounters(): Flow<List<Counter>>
    fun incrementCounter(id: String): Flow<List<Counter>>
    fun decrementCounter(id: String): Flow<List<Counter>>
    fun deleteCounter(id: String): Flow<List<Counter>>
}