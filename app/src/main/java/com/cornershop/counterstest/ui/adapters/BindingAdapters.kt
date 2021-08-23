package com.cornershop.counterstest.ui.adapters

import android.view.View
import androidx.databinding.BindingAdapter
import com.cornershop.counterstest.helpers.ScreenStates


@BindingAdapter("app:screenState", "app:searchText")
    fun updateVisibility(view: View, state: ScreenStates?, text: String? ) {
        state?.let {
            text?.let {
                view.visibility = if (state == ScreenStates.Search && text.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }
