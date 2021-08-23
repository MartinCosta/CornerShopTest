package com.cornershop.counterstest.koin

import com.cornershop.counterstest.model.db.CoreRoomDatabase
import com.cornershop.counterstest.model.db.CounterDbRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dbModules = module {
    single { CounterDbRepository(CoreRoomDatabase.getDatabase(androidContext()).counterDao()) }
}
