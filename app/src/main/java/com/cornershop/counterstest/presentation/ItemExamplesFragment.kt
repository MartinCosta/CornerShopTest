package com.cornershop.counterstest.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cornershop.counterstest.databinding.FragmentItemExampleBinding
import com.cornershop.counterstest.R
import com.cornershop.counterstest.presentation.adapters.ExamplesAdapter

class ItemExamplesFragment: Fragment() {

    private var viewDataBinding: FragmentItemExampleBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewDataBinding = FragmentItemExampleBinding.inflate(inflater, container, false)
        return viewDataBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding?.closeExample?.setOnClickListener { requireActivity().onBackPressed() }
        setExampleAdapter()
    }

    private fun setExampleAdapter() {
        val adapterDrinks = ExamplesAdapter(resources.getStringArray(R.array.drinks_array)) {
            navigateBackToAddCounter(it)
        }
        viewDataBinding?.drinksExampleRv?.adapter = adapterDrinks

        val adapterFood = ExamplesAdapter(resources.getStringArray(R.array.food_array)) {
            navigateBackToAddCounter(it)
        }
        viewDataBinding?.foodExampleRv?.adapter = adapterFood

        val adapterMisc = ExamplesAdapter(resources.getStringArray(R.array.misc_array)) {
            navigateBackToAddCounter(it)
        }
        viewDataBinding?.miscExampleRv?.adapter = adapterMisc
    }

    private fun navigateBackToAddCounter(name: String) {
        findNavController().previousBackStackEntry?.savedStateHandle?.set("send.example.name", name)
        requireActivity().onBackPressed()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        viewDataBinding = null
    }
}