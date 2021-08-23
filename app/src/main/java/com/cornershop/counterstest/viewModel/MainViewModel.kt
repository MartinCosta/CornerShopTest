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
import com.cornershop.counterstest.data.db.CounterDbRepository
import com.cornershop.counterstest.data.repository.CountersRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(private val countersRepository: CountersRepository, private val counterDbRepository: CounterDbRepository): ViewModel() {

    private val _counterEvents: MutableLiveData<Event<CounterEvents>> = MutableLiveData()
    val counterEvents: LiveData<Event<CounterEvents>> = _counterEvents

    private val _searchText: MutableLiveData<String> = MutableLiveData()
    val searchText: LiveData<String> = _searchText

    private val _state: MutableLiveData<States> = MutableLiveData()
    val state: LiveData<States> = _state

    private val _screenState = MutableLiveData<ScreenStates>().apply { value = ScreenStates.MainScreen }
    val screenState: LiveData<ScreenStates> = _screenState

    private val _listOfCounters: MutableLiveData<List<Counter>> = MutableLiveData()
    val listOfCounters: LiveData<List<Counter>> = _listOfCounters

    private val _filteredListOfCounters : MutableLiveData<List<Counter>> = MutableLiveData()
    val filteredListOfCounters: LiveData<List<Counter>> = _filteredListOfCounters

    private val _numberOfSelectedItems: MutableLiveData<Int> = MutableLiveData()
    val numberOfSelectedItems: LiveData<Int> = _numberOfSelectedItems

    val listToDelete = mutableListOf<Counter>()

    private val _noSearchResultsIsVisible: MutableLiveData<Boolean> = MutableLiveData()
    val noSearchResultsIsVisible: LiveData<Boolean> = _noSearchResultsIsVisible

    init {
        getCounters()
    }

    fun updateSearchText(text: String) {
        _searchText.value = text
    }

    fun updateSearchList(searchString: String){
        _filteredListOfCounters.value = (_listOfCounters.value?.filter {
                counter ->
            counter.title.contains(searchString, ignoreCase = true)

        })
        val isNoResults : Boolean =
                    (searchString.isNotEmpty() &&
                    _filteredListOfCounters.value != null &&
                            _filteredListOfCounters.value!!.isEmpty())
        _noSearchResultsIsVisible.postValue(isNoResults)
    }

    fun getCounters() {
        viewModelScope.launch {
            countersRepository.getCounters()
                .onStart { _state.value = States.Loading }
                .catch { _state.value = States.Error
                checkDBList()}
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
                        counterDbRepository.deleteAllCounters()
                        counterDbRepository.insert(counters)
                    }
                }
            _counterEvents.value = Event(CounterEvents(Actions.StopSwipeRefreshing))
        }
    }

    private fun checkDBList(){
        viewModelScope.launch {
            val listFromDB = counterDbRepository.getCounter()

            if(listFromDB.isNotEmpty()) {
                _state.value = States.SuccessHasData
                _listOfCounters.value = listFromDB
                _filteredListOfCounters.value = listFromDB
            }
        }
    }

    fun navigateToAddFragment() {
        _counterEvents.value = Event(CounterEvents(Actions.NavigateAddFragment))
    }

    fun addItemToDelete(item: Counter) {
        listToDelete.add(item)
        _numberOfSelectedItems.value = listToDelete.size
    }

    fun removeItemToDelete(item: Counter) {
        listToDelete.remove(item)
        _numberOfSelectedItems.value = listToDelete.size
    }

    private fun clearDeleteList() {
        listToDelete.clear()
        _numberOfSelectedItems.value = listToDelete.size
    }

    fun exitEditingState() {
        listOfCounters.value?.forEach { it.isSelectedForDelete = false }
        clearDeleteList()
        setScreenState(ScreenStates.MainScreen)
    }

    fun exitSearchState() {
        setScreenState(ScreenStates.MainScreen)
    }

    fun openDeleteDialog() {
        _counterEvents.value = Event(CounterEvents(Actions.ConfirmDelete))
    }

    fun shareCounters() {
        _counterEvents.value = Event(CounterEvents(Actions.ShareCounters))
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

    fun incrementCounter(item: Counter) {
        viewModelScope.launch {
            countersRepository.incrementCounter(CounterId(item.id))
                .catch {
                    _counterEvents.value = Event(CounterEvents(Actions.ErrorIncrementCounter, item))
                }
                .collect {
                    _listOfCounters.value = it
                    if(screenState.value == ScreenStates.Search) {
                        _filteredListOfCounters.value = (_listOfCounters.value?.filter { counter ->
                            counter.title.contains(searchText.value.toString(), ignoreCase = true)
                        })
                    }
                }
        }
    }

    fun decrementCounter(item: Counter) {
        viewModelScope.launch {
            countersRepository.decrementCounter(CounterId(item.id))
                .catch {
                    _counterEvents.value = Event(CounterEvents(Actions.ErrorDecrementCounter, item))
                }
                .collect {
                    _listOfCounters.value = it
                    if(screenState.value == ScreenStates.Search) {
                        _filteredListOfCounters.value = (_listOfCounters.value?.filter { counter ->
                            counter.title.contains(searchText.value.toString(), ignoreCase = true)
                        })
                    }
                }
        }
    }
}