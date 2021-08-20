package com.cornershop.counterstest.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornershop.counterstest.helpers.Event
import kotlinx.coroutines.launch

class MainViewModel(): ViewModel() {
    val counterEditText = MutableLiveData<String>()

    private val _counterEvents: MutableLiveData<Event<CounterEvents>> = MutableLiveData()
    val counterEvents: LiveData<Event<CounterEvents>> = _counterEvents

    fun addCounter() {
        viewModelScope.launch {
            counterEditText.value?.let { name ->
                name.length
                //TODO api call to add counter
            }
        }
    }

    fun navigateBackToMainFragment() {
        _counterEvents.value = Event(CounterEvents(Actions.NavigateBackToMainFragment))
    }
}


sealed class Actions {
    object NavigateBackToMainFragment : Actions()
}

data class CounterEvents(
    var actions: Actions,
    var extras: Any? = null
)