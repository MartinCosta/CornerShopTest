package com.cornershop.counterstest.model.repository

import com.cornershop.counterstest.model.data.Counter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Retrofit

class CountersRepositoryImp(retrofit: Retrofit): CountersRepository {
    private val service = retrofit.create(CountersAPI::class.java)

    override fun addCounter(counter: Counter): Flow<List<Counter>> {
        return flow {
            emit(service.addCounter(counter))
        }.flowOn(Dispatchers.IO)
    }

    override fun getCounters(): Flow<List<Counter>> {
        return flow {
            emit(service.getCounters())
        }.flowOn(Dispatchers.IO)
    }

    override fun incrementCounter(counter: Counter): Flow<List<Counter>> {
        return flow {
            emit(service.incrementCount(counter))
        }.flowOn(Dispatchers.IO)
    }

    override fun decrementCounter(counter: Counter): Flow<List<Counter>> {
        return flow {
            emit(service.decrementCount(counter))
        }.flowOn(Dispatchers.IO)
    }

    override fun deleteCounter(counter: Counter): Flow<List<Counter>> {
        return flow {
            emit(service.deleteCounter(counter))
        }.flowOn(Dispatchers.IO)
    }
}