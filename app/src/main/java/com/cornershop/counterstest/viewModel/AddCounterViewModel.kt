package com.cornershop.counterstest.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornershop.counterstest.helpers.Event
import com.cornershop.counterstest.helpers.States
import com.cornershop.counterstest.model.data.Counter
import com.cornershop.counterstest.model.data.CounterTitle
import com.cornershop.counterstest.model.repository.CountersRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import retrofit2.HttpException

class AddCounterViewModel(private val countersRepository: CountersRepository): ViewModel() {
    val counterEditText = MutableLiveData<String>()

    private val _counterEvents: MutableLiveData<Event<CounterEvents>> = MutableLiveData()
    val counterEvents: LiveData<Event<CounterEvents>> = _counterEvents

    private val _state: MutableLiveData<States> = MutableLiveData()
    val state: LiveData<States> = _state

    fun addCounter() {
        viewModelScope.launch {
            counterEditText.value?.let { name ->
                countersRepository.addCounter(CounterTitle(title = name))
                    .onStart { _state.value = States.Loading }
                    .catch {
                        _state.value = States.Error
                        _counterEvents.value = Event(CounterEvents(Actions.ErrorAddCounter))
                    }
                    .collect {
                        _state.value = States.Success
                        navigateBackToMainFragment(true)
                    }
            }
        }
    }

    fun navigateBackToMainFragment(updateList: Boolean) {
        _counterEvents.value = Event(CounterEvents(Actions.NavigateBackToMainFragment, updateList))
    }
}
