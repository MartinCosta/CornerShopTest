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
import java.lang.reflect.Type

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
        }
        searchBack.setOnClickListener{
            txtToolbarSearch.setText("")
            txtToolbarSearch.clearFocus()
            requireActivity().hideKeyboard()
        }
        imgCloseToolbarSearch.setOnClickListener{
            txtToolbarSearch.setText("")
        }

        txtToolbarSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.updateSearchList((s ?: "").toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        txtToolbarSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) viewModel.setScreenState(ScreenStates.Search)
        }
    }

    private fun observeEvents() {
        viewModel.counterEvents.observe(viewLifecycleOwner, EventObserver {
            when (it.actions) {
                Actions.ConfirmDelete -> deleteDialog()
                Actions.NavigateAddFragment -> navigateToAddCounter()
                Actions.ErrorDelete -> errorDeleteDialog()
                Actions.StopSwipeRefreshing -> stopRefreshing()
            }
        })
    }

    private fun setObservers() {
        viewModel.listOfCounters.observe(viewLifecycleOwner, {
            submitList(it)
        })

        viewModel.filteredListOfCounters.observe(viewLifecycleOwner, {
//            submitList(it)
        })
    }

    private fun waitForAddCounterResult() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("should.update")?.observe(
            viewLifecycleOwner) { update ->
            if(update) viewModel.getCounters()
        }
    }

    private fun setSwipeToRefresh() {
        swipeLayout.setColorSchemeColors(ContextCompat.getColor(requireContext(), R.color.orange))
        swipeLayout.setOnRefreshListener {
            viewModel.getCounters()
        }
    }

    private fun stopRefreshing() {
        swipeLayout.isRefreshing = false
    }

    private fun customBackPressed() {
        val callback = object : OnBackPressedCallback(true /* enabled by default */) {
            override fun handleOnBackPressed() {
                if(viewModel.screenState.value == ScreenStates.Editing) viewModel.exitEditingState()
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


    private fun submitList(list: List<Counter>) {
        swipeLayout.isRefreshing = false
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