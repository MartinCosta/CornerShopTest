package com.cornershop.counterstest.viewModel

sealed class Actions {
    object NavigateBackToMainFragment : Actions()
    object NavigateAddFragment : Actions()
    object ConfirmDelete : Actions()
    object ErrorDelete : Actions()
}

data class CounterEvents(
    var actions: Actions,
    var extras: Any? = null
)