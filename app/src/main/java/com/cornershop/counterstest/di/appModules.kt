package com.cornershop.counterstest.di

import com.cornershop.counterstest.viewModel.AddCounterViewModel
import com.cornershop.counterstest.viewModel.MainViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModules = module {
    viewModel { MainViewModel(get(), get()) }
    viewModel { AddCounterViewModel(get()) }
}
