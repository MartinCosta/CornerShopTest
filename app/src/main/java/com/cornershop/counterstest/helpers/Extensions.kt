package com.cornershop.counterstest.helpers

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager

fun Activity.showKeyboard(){
    val inputManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val view = this.currentFocus
    view?.let{
        inputManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }
}

fun Activity.hideKeyboard() {
    val inputManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val view = this.currentFocus
    view?.let{
        inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}