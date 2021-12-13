package com.github.ai.lifecycle.utils

import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.Lifecycle.State
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LifecycleHelperTest {

    @Test
    fun `check lifecycle events from INITIALIZED to DESTRYED`() {
        val transitions = listOf(
            State.INITIALIZED to State.DESTROYED
        )
        val expectedEvents = listOf(
            listOf(
                Event.ON_CREATE,
                Event.ON_START,
                Event.ON_RESUME,
                Event.ON_PAUSE,
                Event.ON_STOP,
                Event.ON_DESTROY,
            )
        )

        assertLifecyclePath(transitions, expectedEvents)
    }

    @Test
    fun `check lifecycle events from `() {
        val transitions = listOf(
            State.INITIALIZED to State.CREATED,
            State.CREATED to State.STARTED,
            State.STARTED to State.RESUMED,
            State.RESUMED to State.STARTED,
            State.STARTED to State.CREATED,
            State.CREATED to State.DESTROYED
        )

        val expectedEvents = listOf(
            listOf(Event.ON_CREATE),
            listOf(Event.ON_START),
            listOf(Event.ON_RESUME),
            listOf(Event.ON_PAUSE),
            listOf(Event.ON_STOP),
            listOf(Event.ON_DESTROY),
        )

        assertLifecyclePath(transitions, expectedEvents)
    }

    private fun assertLifecyclePath(
        transitions: List<Pair<State, State>>,
        expectedEvents: List<List<Event>>
    ) {
        var idx = 0
        while (idx < transitions.size) {
            val transition = transitions[idx]
            val expectedResult = expectedEvents[idx]

            assertThat(getEventsBetween(transition.first, transition.second))
                .isEqualTo(expectedResult)

            idx++
        }
    }
}