package com.imran.currencyconverter

import android.view.View
import android.widget.EditText
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.imran.currencyconverter.ui.home.HomeActivity
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

//test to input some value and check conversion rate for it
@RunWith(AndroidJUnit4::class)
@LargeTest
class UITest {

    private lateinit var input: String

    @get:Rule
    var activityRule: ActivityScenarioRule<HomeActivity>
            = ActivityScenarioRule(HomeActivity::class.java)

    @Before
    fun initValidString() {
        // Specify a valid string.
        input = "100"
    }

    @Test
    fun changeText_sameActivity() {
        // Type text and then press the button.
        onView(withId(R.id.etValue))
            .perform(typeText(input))

        onView(isRoot()).perform(waitFor(5000))

        val text = getText(onView(withId(R.id.etValue)))
        check(text == "1$input")

    }
}

fun getText(matcher: ViewInteraction): String {
    var text = String()
    matcher.perform(object : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return isAssignableFrom(EditText::class.java)
        }

        override fun getDescription(): String {
            return "Text of the view"
        }

        override fun perform(uiController: UiController, view: View) {
            val tv = view as EditText
            text = tv.text.toString()
        }
    })

    return text
}
fun waitFor(delay: Long): ViewAction {
    return object : ViewAction {
        override fun getConstraints(): Matcher<View> = isRoot()
        override fun getDescription(): String = "wait for $delay milliseconds"
        override fun perform(uiController: UiController, v: View?) {
            uiController.loopMainThreadForAtLeast(delay)
        }
    }
}
