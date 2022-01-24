package com.github.ai.lifecycle

import android.view.View
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.ai.fprovider.demo.Example
import com.github.ai.fprovider.demo.ExampleActivity
import com.github.ai.fprovider.demo.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import androidx.test.espresso.UiController

import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.hamcrest.*
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
@LargeTest
class SoftInputLifecycleOwnerTest {

    @get:Rule
    var activityRule: ActivityScenarioRule<ExampleActivity> = ActivityScenarioRule(
        ExampleActivity.newLaunchIntent(
            ApplicationProvider.getApplicationContext(),
            Example.INSTRUMENTATION_TEST
        )
    )

    @Test
    fun checkThatKeyboardIsShown() {
        onView(withId(R.id.editText))
            .check(matches(ViewMatchers.isDisplayed()))
            .check(matches(ViewMatchers.isFocused()))

        onView(withId(R.id.container))
            // wait for some time until keyboard will be shown
            .perform(waitFor(R.id.container, DELAY_BEFORE_KEYBOARD_WILL_APPEAR))
            // lets assume that `container` should take less than 70% (0.7) of the screen height
            // in case if keyboard was shown
            .check(matches(withHeightLimitedByScreenHeight(0.7f)))
    }

    private fun withHeightLimitedByScreenHeight(
        maxViewHeightToScreenHeightRatio: Float
    ): TypeSafeMatcher<View?> {
        return object : TypeSafeMatcher<View?>(View::class.java) {

            override fun describeTo(description: Description?) {
                description?.appendText("Maximum view height should be less than: " +
                    "$maxViewHeightToScreenHeightRatio * screenHeight")
            }

            override fun matchesSafely(view: View?): Boolean {
                if (view == null) {
                    return false
                }

                val screenHeight = view.context.resources.displayMetrics.heightPixels
                val viewHeight = view.height

                return viewHeight.toFloat() / screenHeight <= maxViewHeightToScreenHeightRatio
            }
        }
    }

    private fun waitFor(viewId: Int, millis: Long): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return withId(viewId)
            }

            override fun getDescription(): String {
                return "Wait for $millis milliseconds."
            }

            override fun perform(uiController: UiController, view: View) {
                uiController.loopMainThreadForAtLeast(millis)
            }
        }
    }

    companion object {
        private val DELAY_BEFORE_KEYBOARD_WILL_APPEAR = TimeUnit.SECONDS.toMillis(1)
    }
}