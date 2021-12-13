package com.github.ai.lifecycle.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner

class TestLifecycleOwner : LifecycleOwner {

    private val lifecycle = TestLifecycle(this)

    override fun getLifecycle(): Lifecycle {
        return lifecycle
    }

    fun handleLifecycleEvents(vararg events: Event) {
        events.forEach {
            lifecycle.handleLifecycleEvent(it)
        }
    }

    fun moveToState(state: State) {
        val events = getEventsBetween(lifecycle.currentState, state)
        events.forEach {
            lifecycle.handleLifecycleEvent(it)
        }
    }

    private class TestLifecycle(
        private val owner: LifecycleOwner
    ) : Lifecycle() {

        private var currentState: State = State.INITIALIZED
        private val observers = mutableListOf<LifecycleObserver>()

        override fun addObserver(observer: LifecycleObserver) {
            observers.add(observer)
        }

        override fun removeObserver(observer: LifecycleObserver) {
            observers.remove(observer)
        }

        override fun getCurrentState(): State {
            return currentState
        }

        fun handleLifecycleEvent(event: Event) {
            currentState = when (event) {
                Event.ON_CREATE -> State.CREATED
                Event.ON_START -> State.STARTED
                Event.ON_RESUME -> State.RESUMED
                Event.ON_PAUSE -> State.STARTED
                Event.ON_STOP -> State.CREATED
                Event.ON_DESTROY -> State.DESTROYED
                else -> throw IllegalArgumentException()
            }

            notifyObservers(event)
        }

        private fun notifyObservers(event: Event) {
            for (observer in observers) {
                if (observer is LifecycleEventObserver) {
                    observer.onStateChanged(owner, event)
                } else {
                    throw IllegalStateException()
                }
            }
        }
    }
}