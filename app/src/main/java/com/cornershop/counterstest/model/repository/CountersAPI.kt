package com.cornershop.counterstest.model.repository

import com.cornershop.counterstest.model.data.Counter
import retrofit2.http.*

interface CountersAPI {
    @POST("api/v1/counter")
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json")
    suspend fun addCounter(@Header("title") title: String) : List<Counter>

    @GET("api/v1/counters")
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json")
    suspend fun getCounters(): List<Counter>

    @POST("api/v1/counter/inc")
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json")
    suspend fun incrementCount(@Header("id") id: String) : List<Counter>

    @POST("api/v1/counter/dec")
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json")
    suspend fun decrementCount(@Header("id") id: String) : List<Counter>

    @DELETE("api/v1/counter")
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json")
    suspend fun deleteCounter(@Header("id") id: String) : List<Counter>
}