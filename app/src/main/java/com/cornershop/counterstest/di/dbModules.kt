package com.cornershop.counterstest.di

import com.cornershop.counterstest.data.db.CoreRoomDatabase
import com.cornershop.counterstest.data.db.CounterDbRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dbModules = module {
    single { CounterDbRepository(CoreRoomDatabase.getDatabase(androidContext()).counterDao()) }
}
