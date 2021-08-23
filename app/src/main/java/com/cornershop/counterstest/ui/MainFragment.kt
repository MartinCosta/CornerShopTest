package com.cornershop.counterstest.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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
import kotlinx.android.synthetic.main.custom_app_bar.*
import kotlinx.android.synthetic.main.fragment_main.*
import org.koin.android.viewmodel.ext.android.viewModel

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

        placeholderSearchBar.setOnClickListener{
            txtToolbarSearch.requestFocus()
            requireActivity().showKeyboard()
            viewModel.setScreenState(ScreenStates.Search)
        }
        searchBack.setOnClickListener{
            txtToolbarSearch.setText("")
            requireActivity().hideKeyboard()
            viewModel.exitSearchState()
        }
        imgCloseToolbarSearch.setOnClickListener{
            txtToolbarSearch.setText("")
        }

        txtToolbarSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.updateSearchText((s ?: "").toString())
                viewModel.updateSearchList((s ?: "").toString())
            }
        })

    }

    private fun observeEvents() {
        viewModel.counterEvents.observe(viewLifecycleOwner, EventObserver {
            when (it.actions) {
                Actions.ConfirmDelete -> deleteDialog()
                Actions.NavigateAddFragment -> navigateToAddCounter()
                Actions.ErrorDelete -> errorDeleteDialog()
                Actions.StopSwipeRefreshing -> stopRefreshing()
                Actions.ErrorIncrementCounter -> errorUpdatingCounterDialog(it.extras as Counter, isIncrement = true)
                Actions.ErrorDecrementCounter -> errorUpdatingCounterDialog(it.extras as Counter, isIncrement = false)
                else -> {}
            }
        })
    }

    private fun setObservers() {
        viewModel.listOfCounters.observe(viewLifecycleOwner, {
            submitList(it)
        })
        viewModel.filteredListOfCounters.observe(viewLifecycleOwner, {
            it?.let {
                viewDataBinding?.countersTotalCount?.text = String.format(getString(R.string.n_items), it.count())
                submitList(it) }
        })
        viewModel.numberOfSelectedItems.observe(viewLifecycleOwner, {
            it?.let {
                val selectedCounterText = String.format(getString(R.string.n_selected), it)
                txtSelectedCounters.text = selectedCounterText
            }
        })

        viewModel.screenState.observe(viewLifecycleOwner, {
            viewDataBinding?.swipeLayout?.isEnabled = it == ScreenStates.MainScreen
        })
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
                        txtToolbarSearch.setText("")
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