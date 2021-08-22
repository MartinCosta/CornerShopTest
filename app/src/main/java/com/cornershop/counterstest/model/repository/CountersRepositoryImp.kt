package com.cornershop.counterstest.model.repository

import com.cornershop.counterstest.model.data.Counter
import com.cornershop.counterstest.model.data.CounterId
import com.cornershop.counterstest.model.data.CounterTitle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Retrofit

class CountersRepositoryImp(retrofit: Retrofit): CountersRepository {
    private val service = retrofit.create(CountersAPI::class.java)

    override fun addCounter(counterTitle: CounterTitle): Flow<List<Counter>> {
        return flow {
            emit(service.addCounter(counterTitle))
        }.flowOn(Dispatchers.IO)
    }

    override fun getCounters(): Flow<List<Counter>> {
        return flow {
            emit(service.getCounters())
        }.flowOn(Dispatchers.IO)
    }

    override fun incrementCounter(counterId: CounterId): Flow<List<Counter>> {
        return flow {
            emit(service.incrementCount(counterId))
        }.flowOn(Dispatchers.IO)
    }

    override fun decrementCounter(counterId: CounterId): Flow<List<Counter>> {
        return flow {
            emit(service.decrementCount(counterId))
        }.flowOn(Dispatchers.IO)
    }

    override fun deleteCounter(counterId: CounterId): Flow<List<Counter>> {
        return flow {
            emit(service.deleteCounter(counterId))
        }.flowOn(Dispatchers.IO)
    }
}