
package com.android.example.themoviedb.ui.search

import android.arch.lifecycle.MutableLiveData
import android.databinding.DataBindingComponent
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import androidx.navigation.NavController
import com.android.example.themoviedb.R
import com.android.example.themoviedb.binding.FragmentBindingAdapters
import com.android.example.themoviedb.testing.SingleFragmentActivity
import com.android.example.themoviedb.util.*
import com.android.example.themoviedb.vo.Movie
import com.android.example.themoviedb.vo.Resource
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*

@RunWith(AndroidJUnit4::class)
class SearchFragmentTest {
    @Rule
    @JvmField
    val activityRule = ActivityTestRule(SingleFragmentActivity::class.java, true, true)
    @Rule
    @JvmField
    val executorRule = TaskExecutorWithIdlingResourceRule()
    @Rule
    @JvmField
    val countingAppExecutors = CountingAppExecutorsRule()
    @Rule
    @JvmField
    val dataBindingIdlingResourceRule = DataBindingIdlingResourceRule(activityRule)

    private lateinit var mockBindingAdapter: FragmentBindingAdapters
    private lateinit var viewModel: SearchViewModel
    private val results = MutableLiveData<Resource<List<Movie>>>()
    private val loadMoreStatus = MutableLiveData<SearchViewModel.LoadMoreState>()
    private val searchFragment = TestSearchFragment()

    @Before
    fun init() {
        viewModel = mock(SearchViewModel::class.java)
        doReturn(loadMoreStatus).`when`(viewModel).loadMoreStatus
        `when`(viewModel.results).thenReturn(results)

        mockBindingAdapter = mock(FragmentBindingAdapters::class.java)

        searchFragment.appExecutors = countingAppExecutors.appExecutors
        searchFragment.viewModelFactory = ViewModelUtil.createFor(viewModel)
        searchFragment.dataBindingComponent = object : DataBindingComponent {
            override fun getFragmentBindingAdapters(): FragmentBindingAdapters {
                return mockBindingAdapter
            }
        }
        activityRule.activity.setFragment(searchFragment)
        EspressoTestUtil.disableProgressBarAnimations(activityRule)
    }

    @Test
    fun search() {
        onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())))
        onView(withId(R.id.input)).perform(
            typeText("foo")
        )
        verify(viewModel).setQuery("foo")
        results.postValue(Resource.loading(null))
        onView(withId(R.id.progress_bar)).check(matches(isDisplayed()))
    }

    @Test
    fun loadResults() {
        val movie = TestUtil.createMovie("foo", "bar", "desc")
        results.postValue(Resource.success(arrayListOf(movie)))
        onView(listMatcher().atPosition(0)).check(matches(hasDescendant(withText("foo/bar"))))
        onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())))
    }

    @Test
    fun dataWithLoading() {
        val movie = TestUtil.createMovie("foo", "bar", "desc")
        results.postValue(Resource.loading(arrayListOf(movie)))
        onView(listMatcher().atPosition(0)).check(matches(hasDescendant(withText("foo/bar"))))
        onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())))
    }

    @Test
    fun error() {
        results.postValue(Resource.error("failed to load", null))
        onView(withId(R.id.error_msg)).check(matches(isDisplayed()))
    }

    @Test
    fun loadMore() {
        val movies = TestUtil.createMovies(50, "foo", "barr")
        results.postValue(Resource.success(movies))
        val action = RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(49)
        onView(withId(R.id.movie_list)).perform(action)
        onView(listMatcher().atPosition(49)).check(matches(isDisplayed()))
        verify(viewModel).loadNextPage()
    }

    @Test
    fun loadMoreProgress() {
        loadMoreStatus.postValue(SearchViewModel.LoadMoreState(true, null))
        onView(withId(R.id.load_more_bar)).check(matches(isDisplayed()))
        loadMoreStatus.postValue(SearchViewModel.LoadMoreState(false, null))
        onView(withId(R.id.load_more_bar)).check(matches(not(isDisplayed())))
    }

    @Test
    fun loadMoreProgressError() {
        loadMoreStatus.postValue(SearchViewModel.LoadMoreState(true, "QQ"))
        onView(withText("QQ")).check(
            matches(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)
            )
        )
    }

    private fun listMatcher(): RecyclerViewMatcher {
        return RecyclerViewMatcher(R.id.movie_list)
    }

    class TestSearchFragment : SearchFragment() {
        val navController = mock<NavController>()
        override fun navController() = navController
    }
}