package com.cornershop.counterstest.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cornershop.counterstest.databinding.FragmentAddCounterBinding
import com.cornershop.counterstest.helpers.EventObserver
import com.cornershop.counterstest.helpers.hideKeyboard
import com.cornershop.counterstest.helpers.showKeyboard
import com.cornershop.counterstest.viewModel.Actions
import com.cornershop.counterstest.viewModel.AddCounterViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.android.viewmodel.ext.android.viewModel


import android.text.style.UnderlineSpan

import android.text.Spanned

import android.text.style.ClickableSpan

import android.text.SpannableString
import android.text.method.LinkMovementMethod
import com.cornershop.counterstest.R


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
        setSpannableExampleText()
        waitForExampleResult()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewDataBinding = null
    }

    private fun setSpannableExampleText() {
        val examplesClickableUnderline = SpannableString(getString(R.string.create_counter_disclaimer))
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                navigateToItemExamples()
                requireActivity().hideKeyboard()
            }
        }
        examplesClickableUnderline.setSpan(clickableSpan, 36, 44, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        examplesClickableUnderline.setSpan(UnderlineSpan(), 36, 44, 0)
        viewDataBinding?.exampleDisclaimer?.text = examplesClickableUnderline
        viewDataBinding?.exampleDisclaimer?.movementMethod = LinkMovementMethod.getInstance()

    }

    private fun observeEvents() {
        viewModel.counterEvents.observe(viewLifecycleOwner, EventObserver {
            when (it.actions) {
                Actions.NavigateBackToMainFragment -> navigateBackToMainFragment(it.extras as Boolean)
                Actions.ErrorAddCounter -> errorAddCounterDialog()
            }
        })
    }

    private fun waitForExampleResult() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("send.example.name")?.observe(
            viewLifecycleOwner) { name ->
            viewDataBinding?.addCounterEditTxt?.clearComposingText()
            viewDataBinding?.addCounterEditTxt?.setText(name)
        }
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

    private fun navigateToItemExamples() {
        val action = AddCounterFragmentDirections.actionToItemExampleFragment()
        findNavController().navigate(action)
    }
}