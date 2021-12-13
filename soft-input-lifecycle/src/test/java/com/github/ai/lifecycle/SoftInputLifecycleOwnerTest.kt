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

    @Before
    fun setUp() {
        ShadowLog.stream = System.out

        eventReceiver = mockk()
        eventLiveData = MutableLiveData()
        parentLifecycleOwner = TestLifecycleOwner()
    }

    @Test
    fun `should call observers if focus present and lifecycle in STARTED state`() {
        // arrange
        val windowFocus = MutableLiveData(true)
        val lifecycle = SoftInputLifecycleOwner(parentLifecycleOwner, windowFocus)

        every { eventReceiver.invoke(EVENT) }.returns(Unit)

        // act
        parentLifecycleOwner.moveToState(State.STARTED)
        eventLiveData.observe(lifecycle) { event ->
            eventReceiver.invoke(event)
        }
        eventLiveData.value = EVENT

        // assert
        verify(exactly = 1) { eventReceiver.invoke(EVENT) }
    }

    companion object {
        private const val EVENT = "event"
    }
}