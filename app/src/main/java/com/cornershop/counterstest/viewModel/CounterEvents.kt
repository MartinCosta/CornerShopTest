package com.cornershop.counterstest.viewModel

sealed class Actions {
    object NavigateBackToMainFragment : Actions()
}

data class CounterEvents(
    var actions: Actions,
    var extras: Any? = null
)