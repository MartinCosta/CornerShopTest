package com.cornershop.counterstest.model.repository

import com.cornershop.counterstest.model.data.Counter
import kotlinx.coroutines.flow.Flow

interface CountersRepository {
    fun addCounter(counter: Counter): Flow<List<Counter>>
    fun getCounters(): Flow<List<Counter>>
    fun incrementCounter(counter: Counter): Flow<List<Counter>>
    fun decrementCounter(counter: Counter): Flow<List<Counter>>
    fun deleteCounter(counter: Counter): Flow<List<Counter>>
}