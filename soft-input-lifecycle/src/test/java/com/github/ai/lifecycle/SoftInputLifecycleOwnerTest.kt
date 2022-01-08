package com.github.ai.lifecycle

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.MutableLiveData
import com.github.ai.lifecycle.utils.TestLifecycleOwner
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class SoftInputLifecycleOwnerTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var eventReceiver: (Any) -> Unit
    private lateinit var eventLiveData: MutableLiveData<Any>
    private lateinit var parentLifecycleOwner: TestLifecycleOwner
    private lateinit var windowFocus: MutableLiveData<Boolean>

    @Before
    fun setUp() {
        ShadowLog.stream = System.out
    }

    @Test
    fun `observer should be called if focus present and lifecycle in STARTED`() {
        // arrange
        setupTestData(
            hasWindowFocus = true,
            initialLifecycleState = State.INITIALIZED
        )
        verifyObserverNotCalled()

        // act
        moveParentLifecycleTo(State.STARTED)
        sendEvent()

        // assert
        verifyObserverCalled(times = 1)
    }

    @Test
    fun `observer should be called if focus appeared and lifecycle in STARTED`() {
        // arrange
        setupTestData(
            hasWindowFocus = false,
            initialLifecycleState = State.INITIALIZED
        )
        verifyObserverNotCalled()

        // act
        moveParentLifecycleTo(State.STARTED)
        sendEvent()

        verifyObserverNotCalled()
        changeWindowFocus(true)

        // assert
        verifyObserverCalled(times = 1)
    }

    @Test
    fun `observer should be called if focus present and lifecycle moved to STARTED`() {
        // arrange
        setupTestData(
            hasWindowFocus = true,
            initialLifecycleState = State.INITIALIZED
        )
        verifyObserverNotCalled()

        // act
        sendEvent()

        verifyObserverNotCalled()
        moveParentLifecycleTo(State.STARTED)

        // assert
        verifyObserverCalled(times = 1)
    }

    @Test
    fun `observer should not be called if no focus and lifecycle in STARTED`() {
        // arrange
        setupTestData(
            hasWindowFocus = false,
            initialLifecycleState = State.INITIALIZED
        )
        verifyObserverNotCalled()

        // act
        moveParentLifecycleTo(State.STARTED)
        sendEvent()

        // assert
        verifyObserverNotCalled()
    }

    @Test
    fun `observer should not be called if focus present and lifecycle below STARTED`() {
        // arrange
        setupTestData(
            hasWindowFocus = true,
            initialLifecycleState = State.INITIALIZED
        )
        verifyObserverNotCalled()

        // act
        moveParentLifecycleTo(State.CREATED)
        sendEvent()

        // assert
        verifyObserverNotCalled()
    }

    @Test
    fun `observer should not be called if no focus and lifecycle below STARTED`() {
        // arrange
        setupTestData(
            hasWindowFocus = false,
            initialLifecycleState = State.INITIALIZED
        )
        verifyObserverNotCalled()

        // act
        moveParentLifecycleTo(State.CREATED)
        sendEvent()

        // assert
        verifyObserverNotCalled()
    }

    @Test
    fun `observer should be called if focus present and lifecycle above STARTED`() {
        // arrange
        setupTestData(
            hasWindowFocus = true,
            initialLifecycleState = State.INITIALIZED
        )
        verifyObserverNotCalled()

        // act
        moveParentLifecycleTo(State.RESUMED)
        sendEvent()

        // assert
        verifyObserverCalled(times = 1)
    }

    @Test
    fun `observer should not be called if focus present and lifecycle in DESTROYED`() {
        // arrange
        setupTestData(
            hasWindowFocus = true,
            initialLifecycleState = State.INITIALIZED
        )
        verifyObserverNotCalled()

        // act
        moveParentLifecycleTo(State.CREATED)
        moveParentLifecycleTo(State.DESTROYED)
        sendEvent()

        // assert
        verifyObserverNotCalled()
    }

    private fun setupTestData(
        hasWindowFocus: Boolean,
        initialLifecycleState: State
    ) {
        windowFocus = MutableLiveData(hasWindowFocus)
        parentLifecycleOwner = TestLifecycleOwner(initialLifecycleState)
        eventLiveData = MutableLiveData<Any>()
        eventReceiver = mockk()

        every { eventReceiver.invoke(EVENT) }.returns(Unit)

        val softInputLifecycle = SoftInputLifecycleOwner(parentLifecycleOwner, windowFocus)

        eventLiveData.observe(softInputLifecycle) { event ->
            eventReceiver.invoke(event)
        }
    }

    private fun moveParentLifecycleTo(state: State) {
        parentLifecycleOwner.moveToState(state)
    }

    private fun changeWindowFocus(hasWindowFocus: Boolean) {
        windowFocus.value = hasWindowFocus
    }

    private fun sendEvent() {
        eventLiveData.value = EVENT
    }

    private fun verifyObserverNotCalled() = verifyObserverCalled(times = 0)

    private fun verifyObserverCalled(times: Int) {
        verify(exactly = times) { eventReceiver.invoke(EVENT) }
    }

    companion object {
        private const val EVENT = "event"
    }
}