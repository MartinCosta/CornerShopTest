package com.cornershop.counterstest.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cornershop.counterstest.R
import com.cornershop.counterstest.databinding.FragmentMainBinding
import com.cornershop.counterstest.helpers.EventObserver
import com.cornershop.counterstest.helpers.ScreenStates
import com.cornershop.counterstest.helpers.hideKeyboard
import com.cornershop.counterstest.helpers.showKeyboard
import com.cornershop.counterstest.model.data.Counter
import com.cornershop.counterstest.ui.adapters.CountersAdapter
import com.cornershop.counterstest.viewModel.Actions
import com.cornershop.counterstest.viewModel.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import android.content.Intent




class MainFragment: Fragment() {

    private val viewModel: MainViewModel by viewModel()
    private var viewDataBinding: FragmentMainBinding? = null
    private var adapter: CountersAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewDataBinding = FragmentMainBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
            lifecycleOwner = viewLifecycleOwner }
        return viewDataBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
        observeEvents()
        customBackPressed()
        setSwipeToRefresh()
        waitForAddCounterResult()
        searchToolbarListener()
    }

    private fun searchToolbarListener() {
        viewDataBinding?.let {
            with(viewDataBinding!!){
                val searchEditText = customAppBar.txtToolbarSearch
                placeholderSearchBar.setOnClickListener{
                    viewModel.setScreenState(ScreenStates.Search)
                    lifecycleScope.launch {
                        delay(100)
                        searchEditText.requestFocus()
                        requireActivity().showKeyboard()
                    }
                }

                customAppBar.searchBack.setOnClickListener{
                    searchEditText.setText("")
                    requireActivity().hideKeyboard()
                    viewModel.exitSearchState()
                }

                customAppBar.imgCloseToolbarSearch.setOnClickListener{
                    searchEditText.setText("")
                }

                searchEditText.addTextChangedListener {
                    viewModel.updateSearchText((it ?: "").toString())
                    viewModel.updateSearchList((it ?: "").toString())
                }
            }
        }
    }

    private fun shareIntent() {
        val listOfSharingCounter = mutableListOf<String>()
        viewModel.listToDelete.map {
            listOfSharingCounter.add(String.format(getString(R.string.n_per_counter_name), it.count, it.title))
        }
        val titleToShare = listOfSharingCounter.toString().replace("[", "").replace("]", "");
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, titleToShare)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun observeEvents() {
        viewModel.counterEvents.observe(viewLifecycleOwner, EventObserver {
            when (it.actions) {
                Actions.ConfirmDelete -> deleteDialog()
                Actions.NavigateAddFragment -> navigateToAddCounter()
                Actions.ErrorDelete -> errorDeleteDialog()
                Actions.StopSwipeRefreshing -> stopRefreshing()
                Actions.ShareCounters -> shareIntent()
                Actions.ErrorIncrementCounter -> errorUpdatingCounterDialog(it.extras as Counter, isIncrement = true)
                Actions.ErrorDecrementCounter -> errorUpdatingCounterDialog(it.extras as Counter, isIncrement = false)
                else -> {}
            }
        })
    }

    private fun setObservers() {
        viewModel.listOfCounters.observe(viewLifecycleOwner, {
            submitList(it)
            if(viewModel.screenState.value != ScreenStates.Search) setTotalCountText(it)
        })
        viewModel.filteredListOfCounters.observe(viewLifecycleOwner, {
            it?.let {
                submitList(it)
                if(viewModel.screenState.value == ScreenStates.Search) setTotalCountText(it)
            }
        })
        viewModel.numberOfSelectedItems.observe(viewLifecycleOwner, {
            it?.let {
                val selectedCounterText = String.format(getString(R.string.n_selected), it)
                viewDataBinding?.customAppBar?.txtSelectedCounters?.text = selectedCounterText
            }
        })

        viewModel.screenState.observe(viewLifecycleOwner, {
            viewDataBinding?.swipeLayout?.isEnabled = it == ScreenStates.MainScreen
        })
    }

    private fun setTotalCountText(list: List<Counter>) {
        viewDataBinding?.countersTotalCount?.text = String.format(getString(R.string.n_items), list.count())
        var totalCount = 0
        list.map { totalCount += it.count }
        viewDataBinding?.countersTotalTimes?.text = String.format(getString(R.string.n_times), totalCount)
    }

    private fun waitForAddCounterResult() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("should.update")?.observe(
            viewLifecycleOwner) { update ->
            if(update) viewModel.getCounters()
        }
    }

    private fun setSwipeToRefresh() {
        viewDataBinding?.swipeLayout?.setColorSchemeColors(ContextCompat.getColor(requireContext(), R.color.orange))
        viewDataBinding?.swipeLayout?.setOnRefreshListener {
            viewModel.getCounters()
        }
    }

    private fun stopRefreshing() {
        viewDataBinding?.swipeLayout?.isRefreshing = false
    }

    private fun customBackPressed() {
        val callback = object : OnBackPressedCallback(true /* enabled by default */) {
            override fun handleOnBackPressed() {
                when(viewModel.screenState.value) {
                    ScreenStates.Editing -> viewModel.exitEditingState()
                    ScreenStates.Search -> {
                        viewDataBinding?.customAppBar?.txtToolbarSearch?.setText("")
                        viewModel.exitSearchState()
                    }
                    ScreenStates.MainScreen -> {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
                isEnabled = false
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun deleteDialog() {
         val title = if(viewModel.listToDelete.count() == 1) viewModel.listToDelete.first().title
         else getString(R.string.delete_x_bulk, viewModel.listToDelete.count().toString())
        MaterialAlertDialogBuilder(requireContext(), R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog)
            .setPositiveButton(getString(R.string.delete)) { dialog, _ ->
                dialog.dismiss()
                viewModel.deleteCounters()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
                viewModel.exitEditingState()
            }
            .setTitle(getString(R.string.delete_x_question, title))
            .show()
    }

    private fun errorDeleteDialog() {
        val title = if(viewModel.listToDelete.count() == 1) getString(R.string.error_deleting_counter_title)
        else getString(R.string.error_deleting_bulk_counter_title)
        MaterialAlertDialogBuilder(requireContext(), R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog)
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
                viewModel.exitEditingState()
            }
            .setMessage(getString(R.string.connection_error_description))
            .setTitle(title)
            .show()
    }

    private fun errorUpdatingCounterDialog(item: Counter, isIncrement: Boolean) {
        val count = if(isIncrement) item.count + 1 else item.count - 1
        val title = getString(R.string.error_updating_counter_title, item.title, count)
        MaterialAlertDialogBuilder(requireContext(), R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog)
            .setPositiveButton(getString(R.string.dismiss)) { dialog, _ ->
                dialog.dismiss()
                viewModel.exitEditingState()
            }
            .setNegativeButton(getString(R.string.retry)) { dialog, _ ->
                dialog.dismiss()
                if(isIncrement) viewModel.incrementCounter(item) else viewModel.decrementCounter(item)
            }
            .setMessage(getString(R.string.connection_error_description))
            .setTitle(title)
            .show()
    }

    private fun submitList(list: List<Counter>) {
        if(adapter == null) {
            adapter = CountersAdapter(viewModel)
        }
        viewDataBinding?.counterRv?.adapter = adapter
        adapter?.submitList(list)
    }

    private fun navigateToAddCounter() {
        val action = MainFragmentDirections.actionToAddCounterFragment()
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewDataBinding = null
    }
}