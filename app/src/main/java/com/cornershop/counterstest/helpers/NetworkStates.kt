package com.cornershop.counterstest.helpers

import com.cornershop.counterstest.viewModel.Actions

sealed class States {
    object Success: States()
    object SuccessHasData: States()
    object SuccessEmpty: States()
    object Loading: States()
    object Error: States()
}
