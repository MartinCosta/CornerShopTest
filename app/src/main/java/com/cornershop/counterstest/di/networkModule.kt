package com.cornershop.counterstest.di

import com.cornershop.counterstest.BuildConfig
import com.cornershop.counterstest.data.repository.CountersRepository
import com.cornershop.counterstest.data.repository.CountersRepositoryImp
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    factory { provideOkHttpClient() }
    single { provideRetrofit(get()) }
    single { provideCountersApi(get()) }
}

fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder().baseUrl(BuildConfig.BASEURL).client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create()).build()
}

fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient().newBuilder().build()
}

fun provideCountersApi(retrofit: Retrofit): CountersRepository = CountersRepositoryImp(retrofit)
