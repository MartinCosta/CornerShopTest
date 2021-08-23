package com.cornershop.counterstest.data.repository

import com.cornershop.counterstest.model.data.Counter
import com.cornershop.counterstest.model.data.CounterId
import com.cornershop.counterstest.model.data.CounterTitle
import kotlinx.coroutines.flow.Flow

interface CountersRepository {
    fun addCounter(counterTitle: CounterTitle): Flow<List<Counter>>
    fun getCounters(): Flow<List<Counter>>
    fun incrementCounter(counterId: CounterId): Flow<List<Counter>>
    fun decrementCounter(counterId: CounterId): Flow<List<Counter>>
    fun deleteCounter(counterId: CounterId): Flow<List<Counter>>
}