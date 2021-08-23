package com.cornershop.counterstest.data.db

import androidx.annotation.WorkerThread
import com.cornershop.counterstest.model.data.Counter

class CounterDbRepository(private val dao: CounterDao) {

    @WorkerThread
    suspend fun insert(counter: List<Counter>) {
        dao.insert(counter)
    }

    @WorkerThread
    suspend fun update(counter: Counter) {
        dao.update(counter)
    }

    @WorkerThread
    suspend fun delete(counter: Counter) {
        dao.delete(counter)
    }

    @WorkerThread
    suspend fun getCounter(): List<Counter> {
        return dao.getCounter()
    }

    @WorkerThread
    suspend fun deleteAllCounters(){
        dao.deleteAll()
    }
}