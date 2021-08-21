package com.cornershop.counterstest.koin

import com.cornershop.counterstest.model.repository.CountersRepository
import com.cornershop.counterstest.model.repository.CountersRepositoryImp
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "http://10.0.2.2:3000/"

val networkModule = module {
    factory { provideOkHttpClient() }
    single { provideRetrofit(get()) }
    single { provideCountersApi(get()) }
}

fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder().baseUrl(BASE_URL).client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create()).build()
}

fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient().newBuilder().build()
}

fun provideCountersApi(retrofit: Retrofit): CountersRepository = CountersRepositoryImp(retrofit)
