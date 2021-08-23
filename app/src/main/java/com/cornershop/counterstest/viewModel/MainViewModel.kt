package com.cornershop.counterstest.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornershop.counterstest.helpers.Event
import com.cornershop.counterstest.helpers.ScreenStates
import com.cornershop.counterstest.helpers.States
import com.cornershop.counterstest.model.data.Counter
import com.cornershop.counterstest.model.data.CounterId
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

    private val _screenState = MutableLiveData<ScreenStates>().apply { value = ScreenStates.MainScreen }
    val screenState: LiveData<ScreenStates> = _screenState

    private val _listOfCounters: MutableLiveData<List<Counter>> = MutableLiveData()
    val listOfCounters: LiveData<List<Counter>> = _listOfCounters

    private val _filteredListOfCounters : MutableLiveData<List<Counter>> = MutableLiveData()
    val filteredListOfCounters: LiveData<List<Counter>> = _filteredListOfCounters

    val listToDelete = mutableListOf<Counter>()

    init {
        getCounters()
    }

    fun updateSearchList(searchString: String){
        _filteredListOfCounters.postValue(_listOfCounters.value?.filter {
                counter -> counter.title.contains(searchString, ignoreCase = true)
        })
    }

    fun getCounters() {
        viewModelScope.launch {
            countersRepository.getCounters()
                .onStart { _state.value = States.Loading }
                .catch { _state.value = States.Error  }
                .collect { counters ->
                    //Added small delay. So we can give time to animation transition to finish before render recycler list
                    delay(400)
                    if(counters.isEmpty()) {
                        _state.value = States.SuccessEmpty
                    }
                    else{
                        _state.value = States.SuccessHasData
                        _listOfCounters.value = counters
                        _filteredListOfCounters.value = counters
                    }
                }
            _counterEvents.value = Event(CounterEvents(Actions.StopSwipeRefreshing))
        }
    }

    fun navigateToAddFragment() {
        _counterEvents.value = Event(CounterEvents(Actions.NavigateAddFragment))
    }

    fun addItemToDelete(item: Counter) {
        listToDelete.add(item)
    }

    fun removeItemToDelete(item: Counter) {
        listToDelete.remove(item)
    }

    private fun clearDeleteList() {
        listToDelete.clear()
    }

    fun exitEditingState() {
        listOfCounters.value?.forEach { it.isSelectedForDelete = false }
        clearDeleteList()
        setScreenState(ScreenStates.MainScreen)
    }

    fun openDeleteDialog() {
        _counterEvents.value = Event(CounterEvents(Actions.ConfirmDelete))
    }

    fun deleteCounters() {
        var showErrorDialog = true
        viewModelScope.launch {
            listToDelete.forEach { counter ->
                countersRepository.deleteCounter(CounterId(counter.id))
                    .catch {
                        if(showErrorDialog) {
                            showErrorDialog = false
                            _counterEvents.value = Event(CounterEvents(Actions.ErrorDelete))
                        }
                    }
                    .collect { counters ->
                        if(counters.isEmpty()) _state.value = States.SuccessEmpty
                        else{
                            _listOfCounters.value = counters
                        }
                    }
            }
            exitEditingState()
        }
    }

    fun setScreenState(state: ScreenStates) {
        _screenState.value = state
    }

    fun incrementCounter(id: String) {
        viewModelScope.launch {
            countersRepository.incrementCounter(CounterId(id))
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

    fun decrementCounter(id: String) {
        viewModelScope.launch {
            countersRepository.decrementCounter(CounterId(id))
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