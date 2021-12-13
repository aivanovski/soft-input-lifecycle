package com.github.ai.lifecycle

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.LiveData
import com.github.ai.lifecycle.utils.getEventsBetween

class SoftInputLifecycleOwner(
    parentLifecycleOwner: LifecycleOwner,
    private val windowFocus: LiveData<Boolean>,
    private val isDebug: Boolean = DEBUG
) : LifecycleOwner, LifecycleEventObserver {

    private var isParentAlreadyCreated = false
    private val parentLifecycle = parentLifecycleOwner.lifecycle
    private val registry = LifecycleRegistry(this)

    init {
        parentLifecycle.addObserver(this)
        windowFocus.observe(parentLifecycleOwner) { hasFocus ->
            onDataChanged(hasFocus, parentLifecycle.currentState)
        }
    }

    override fun getLifecycle(): Lifecycle {
        return registry
    }

    override fun onStateChanged(source: LifecycleOwner, event: Event) {
        val parentState = parentLifecycle.currentState

        onDataChanged(windowFocus.value ?: false, parentState)

        if (parentState == State.CREATED) {
            isParentAlreadyCreated = true
        }

        if (parentState == State.DESTROYED) {
            parentLifecycle.removeObserver(this)
        }
    }

    private fun onDataChanged(hasFocus: Boolean, parentLifecycleState: State) {
        val newState = determineLifecycleState(hasFocus, parentLifecycleState)

        if (isDebug) {
            Log.d(
                TAG,
                String.format(
                    "onDataChanged: hasFocus=%s, parentState=%s, newState=%s",
                    hasFocus,
                    parentLifecycle.currentState,
                    newState
                )
            )
        }

        if (registry.currentState != newState) {
            val events = getEventsBetween(registry.currentState, newState)

            events.forEach {
                registry.handleLifecycleEvent(it)
            }
        }
    }

    private fun determineLifecycleState(
        hasWindowFocus: Boolean,
        parentState: State
    ): State {
        return when {
            hasWindowFocus && parentState.isAtLeast(State.STARTED) -> {
                parentState
            }
            hasWindowFocus -> {
                parentState
            }
            parentState.isAtLeast(State.CREATED) -> {
                State.CREATED
            }
            else -> { // if no windowFocus
                parentState
            }
        }
    }

    companion object {
        private val TAG = SoftInputLifecycleOwner::class.java.simpleName
        private const val DEBUG = true
    }
}