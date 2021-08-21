package com.cornershop.counterstest.koin

import com.cornershop.counterstest.viewModel.MainViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModules = module {
    viewModel { MainViewModel(get()) }
}
