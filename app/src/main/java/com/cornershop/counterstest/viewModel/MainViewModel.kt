package com.cornershop.counterstest.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornershop.counterstest.helpers.Event
import com.cornershop.counterstest.helpers.States
import com.cornershop.counterstest.model.data.Counter
import com.cornershop.counterstest.model.repository.CountersRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MainViewModel(private val countersRepository: CountersRepository): ViewModel() {

    private val _counterEvents: MutableLiveData<Event<CounterEvents>> = MutableLiveData()
    val counterEvents: LiveData<Event<CounterEvents>> = _counterEvents

    private val _state: MutableLiveData<States> = MutableLiveData()
    val state: LiveData<States> = _state

    private val _listOfCounters: MutableLiveData<List<Counter>> = MutableLiveData()
    val listOfCounters: LiveData<List<Counter>> = _listOfCounters

    init {
        getCounters()
    }

    fun getCounters() {
        viewModelScope.launch {
            countersRepository.getCounters()
                .onStart { _state.value = States.Loading }
                .catch {  }
                .collect { counters ->
                    //Added small delay. So we can give time to animation transition to finish before render recycler list
                    delay(300)
                    if(counters.isEmpty()) {
                        _state.value = States.SuccessEmpty
                    }
                    else{
                        _state.value = States.SuccessHasData
                        _listOfCounters.value = counters
                    }
                }
        }
    }

    fun incrementCounter(item: Counter) {
        viewModelScope.launch {
            countersRepository.incrementCounter(item)
                .catch {
                    if(it is HttpException){
                        it
                    }
                }
                .collect {
                    _listOfCounters.value = it
                }
        }
    }

    fun decrementCounter(item: Counter) {
        viewModelScope.launch {
            countersRepository.decrementCounter(item)
                .catch {
                    if(it is HttpException){
                        it
                    }
                }
                .collect {
                    _listOfCounters.value = it
                }
        }
    }
}