package com.cornershop.counterstest.model.repository

import com.cornershop.counterstest.model.data.Counter
import retrofit2.http.*

interface CountersAPI {
    @POST("api/v1/counter")
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json")
    suspend fun addCounter(@Body counter: Counter) : List<Counter>

    @GET("api/v1/counters")
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json")
    suspend fun getCounters(): List<Counter>

    @POST("api/v1/counter/inc")
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json")
    suspend fun incrementCount(@Body counter: Counter) : List<Counter>

    @POST("api/v1/counter/dec")
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json")
    suspend fun decrementCount(@Body counter: Counter) : List<Counter>

    @DELETE("api/v1/counter")
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json")
    suspend fun deleteCounter(@Body counter: Counter) : List<Counter>
}