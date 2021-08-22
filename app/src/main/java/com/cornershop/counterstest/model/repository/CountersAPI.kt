package com.cornershop.counterstest.model.repository

import android.R.attr
import com.cornershop.counterstest.model.data.Counter
import com.cornershop.counterstest.model.data.CounterId
import com.cornershop.counterstest.model.data.CounterTitle
import retrofit2.http.*
import android.R.attr.path




interface CountersAPI {
    @POST("api/v1/counter")
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json")
    suspend fun addCounter(@Body counterTitle: CounterTitle) : List<Counter>

    @GET("api/v1/counters")
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json")
    suspend fun getCounters(): List<Counter>

    @POST("api/v1/counter/inc")
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json")
    suspend fun incrementCount(@Body counterId: CounterId) : List<Counter>

    @POST("api/v1/counter/dec")
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json")
    suspend fun decrementCount(@Body counterId: CounterId) : List<Counter>

    @HTTP(method = "DELETE", path = "api/v1/counter", hasBody = true)
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json")
    suspend fun deleteCounter(@Body counterId: CounterId) : List<Counter>
}