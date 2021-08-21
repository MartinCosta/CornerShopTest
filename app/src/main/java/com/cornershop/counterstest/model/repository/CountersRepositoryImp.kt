package com.cornershop.counterstest.model.repository

import com.cornershop.counterstest.model.data.Counter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Retrofit

class CountersRepositoryImp(retrofit: Retrofit): CountersRepository {
    private val service = retrofit.create(CountersAPI::class.java)

    override fun addCounter(title: String): Flow<List<Counter>> {
        return flow {
            emit(service.addCounter(title))
        }.flowOn(Dispatchers.IO)
    }

    override fun getCounters(): Flow<List<Counter>> {
        return flow {
            emit(service.getCounters())
        }.flowOn(Dispatchers.IO)
    }

    override fun incrementCounter(id: String): Flow<List<Counter>> {
        return flow {
            emit(service.incrementCount(id))
        }.flowOn(Dispatchers.IO)
    }

    override fun decrementCounter(id: String): Flow<List<Counter>> {
        return flow {
            emit(service.decrementCount(id))
        }.flowOn(Dispatchers.IO)
    }

    override fun deleteCounter(id: String): Flow<List<Counter>> {
        return flow {
            emit(service.deleteCounter(id))
        }.flowOn(Dispatchers.IO)
    }
}