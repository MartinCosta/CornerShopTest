package com.cornershop.counterstest.helpers

sealed class States {
    object Success: States()
    object SuccessHasData: States()
    object SuccessEmpty: States()
    object Loading: States()
    object Error: States()
}
