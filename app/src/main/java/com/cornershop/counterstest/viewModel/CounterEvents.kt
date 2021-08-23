package com.cornershop.counterstest.viewModel

sealed class Actions {
    object NavigateBackToMainFragment : Actions()
    object NavigateAddFragment : Actions()
    object ConfirmDelete : Actions()
    object ShareCounters : Actions()
    object ErrorDelete : Actions()
    object ErrorAddCounter : Actions()
    object StopSwipeRefreshing : Actions()
    object ErrorIncrementCounter : Actions()
    object ErrorDecrementCounter : Actions()
}

data class CounterEvents(
    var actions: Actions,
    var extras: Any? = null
)