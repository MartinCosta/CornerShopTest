package com.cornershop.counterstest.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cornershop.counterstest.databinding.FragmentMainBinding
import com.cornershop.counterstest.helpers.EventObserver
import com.cornershop.counterstest.model.data.Counter
import com.cornershop.counterstest.ui.adapters.CountersAdapter
import com.cornershop.counterstest.viewModel.Actions
import com.cornershop.counterstest.viewModel.MainViewModel
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
        viewDataBinding?.buttonAddCounter?.setOnClickListener {
            navigateToAddCounter()
        }
    }

    private fun observeEvents() {
        viewModel.counterEvents.observe(viewLifecycleOwner, EventObserver {
            when (it.actions) {

            }
        })
    }

    private fun setObservers() {
        viewModel.listOfCounters.observe(viewLifecycleOwner, {
            submitList(it)
        })
    }

    private fun submitList(list: List<Counter>) {
        if(adapter == null) {
            adapter = CountersAdapter(list, viewModel)
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