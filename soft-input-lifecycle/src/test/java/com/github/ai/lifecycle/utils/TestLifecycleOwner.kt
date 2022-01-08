package com.github.ai.lifecycle.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

class TestLifecycleOwner(initialState: State) : LifecycleOwner {

    private val lifecycle = LifecycleRegistry(this).apply {
        currentState = initialState
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycle
    }

    fun moveToState(state: State) {
        lifecycle.currentState = state
    }
}