package com.android.example.themoviedb.util

import android.support.test.InstrumentationRegistry
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import com.android.example.themoviedb.testing.SingleFragmentActivity
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AutoClearedValueTest {

    @Rule
    @JvmField
    val activityRule = ActivityTestRule(SingleFragmentActivity::class.java, true, true)

    private lateinit var testFragment: TestFragment

    @Before
    fun init() {
        testFragment = TestFragment()
        activityRule.activity.setFragment(testFragment)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    }

    @Test
    fun clearOnReplace() {
        testFragment.testValue = "foo"
        activityRule.activity.replaceFragment(TestFragment())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        try {
            testFragment.testValue
            Assert.fail("should throw if accessed when not set")
        } catch (ex: IllegalStateException) {
        }
    }

    @Test
    fun dontClearForChildFragment() {
        testFragment.testValue = "foo"
        testFragment.childFragmentManager.beginTransaction()
            .add(Fragment(), "foo").commit()
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        assertThat(testFragment.testValue, `is`("foo"))
    }

    @Test
    fun dontClearForDialog() {
        testFragment.testValue = "foo"
        val dialogFragment = DialogFragment()
        dialogFragment.show(testFragment.fragmentManager!!, "dialog")
        dialogFragment.dismiss()
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        assertThat(testFragment.testValue, `is`("foo"))
    }

    class TestFragment : Fragment() {
        var testValue by autoCleared<String>()
    }
}