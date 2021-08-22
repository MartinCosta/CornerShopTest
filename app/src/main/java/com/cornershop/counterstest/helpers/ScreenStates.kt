package com.cornershop.counterstest.helpers

sealed class ScreenStates {
    object MainScreen: ScreenStates()
    object Editing: ScreenStates()
    object Search: ScreenStates()
}
