package com.cornershop.counterstest.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cornershop.counterstest.databinding.FragmentWelcomeBinding

class WelcomeFragment: Fragment() {
    private var viewDataBinding: FragmentWelcomeBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewDataBinding = FragmentWelcomeBinding.inflate(inflater, container, false).apply { lifecycleOwner = viewLifecycleOwner }
        return viewDataBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewDataBinding = null
    }

    private fun setListeners() {
        viewDataBinding?.welcomeLayout?.buttonStart?.setOnClickListener {
            navigateToMainFragment()
        }
    }

    private fun navigateToMainFragment() {
        val action = WelcomeFragmentDirections.actionToMainFragment()
        findNavController().navigate(action)
    }
}