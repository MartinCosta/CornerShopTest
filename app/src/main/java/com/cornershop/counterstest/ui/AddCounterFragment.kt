package com.cornershop.counterstest.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cornershop.counterstest.R
import com.cornershop.counterstest.databinding.FragmentAddCounterBinding
import com.cornershop.counterstest.helpers.EventObserver
import com.cornershop.counterstest.helpers.hideKeyboard
import com.cornershop.counterstest.helpers.showKeyboard
import com.cornershop.counterstest.viewModel.Actions
import com.cornershop.counterstest.viewModel.AddCounterViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.android.viewmodel.ext.android.viewModel

class AddCounterFragment: Fragment() {

    private var viewDataBinding: FragmentAddCounterBinding? = null
    private val viewModel: AddCounterViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewDataBinding = FragmentAddCounterBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
            lifecycleOwner = viewLifecycleOwner }
        return viewDataBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEditTextFocus()
        observeEvents()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewDataBinding = null
    }

    private fun observeEvents() {
        viewModel.counterEvents.observe(viewLifecycleOwner, EventObserver {
            when (it.actions) {
                Actions.NavigateBackToMainFragment -> navigateBackToMainFragment(it.extras as Boolean)
                Actions.ErrorAddCounter -> errorAddCounterDialog()
            }
        })
    }

    private fun errorAddCounterDialog() {
        MaterialAlertDialogBuilder(requireContext(), R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog)
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
            }
            .setMessage(getString(R.string.connection_error_description))
            .setTitle(getString(R.string.error_creating_counter_title))
            .show()
    }

    private fun setEditTextFocus() {
        viewDataBinding?.addCounterEditTxt?.requestFocus()
        requireActivity().showKeyboard()
    }

    private fun navigateBackToMainFragment(updateList: Boolean) {
        findNavController().previousBackStackEntry?.savedStateHandle?.set("should.update", updateList)
        requireActivity().hideKeyboard()
        requireActivity().onBackPressed()
    }
}