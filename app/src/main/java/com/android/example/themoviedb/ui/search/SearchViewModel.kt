
package com.android.example.themoviedb.ui.search

import android.arch.lifecycle.*
import android.arch.lifecycle.Observer
import com.android.example.themoviedb.repository.MovieRepository
import com.android.example.themoviedb.testing.OpenForTesting
import com.android.example.themoviedb.vo.Movie
import com.android.example.themoviedb.vo.Resource
import com.android.example.themoviedb.vo.Status
import java.util.*
import javax.inject.Inject

@OpenForTesting
class SearchViewModel @Inject constructor(movieRepository: MovieRepository) : ViewModel() {

    private val query = MutableLiveData<String>()
    private val nextPageHandler = NextPageHandler(movieRepository)

    val results: LiveData<Resource<List<Movie>>> = Transformations
        .switchMap(query) { search ->
            movieRepository.search(search)
//            if (search.isNullOrBlank()) {
//                AbsentLiveData.create()
//            } else {
//                movieRepository.search(search)
//            }
        }

    val loadMoreStatus: LiveData<LoadMoreState>
        get() = nextPageHandler.loadMoreState

    fun setQuery(originalInput: String) {
        val input = originalInput.toLowerCase(Locale.getDefault()).trim()
        if (input == query.value) {
            return
        }
        nextPageHandler.reset()
        query.value = input
    }

    fun loadNextPage() {
        query.value?.let {
            nextPageHandler.queryNextPage(it)
        }
    }

    fun refresh() {
        query.value?.let {
            query.value = it
        }
    }

    class LoadMoreState(val isRunning: Boolean, val errorMessage: String?) {
        private var handledError = false

        val errorMessageIfNotHandled: String?
            get() {
                if (handledError) {
                    return null
                }
                handledError = true
                return errorMessage
            }
    }

    class NextPageHandler(private val repository: MovieRepository) : Observer<Resource<Boolean>> {
        private var nextPageLiveData: LiveData<Resource<Boolean>>? = null
        val loadMoreState = MutableLiveData<LoadMoreState>()
        private var query: String? = null
        private var _hasMore: Boolean = false
        val hasMore
            get() = _hasMore

        init {
            reset()
        }

        fun queryNextPage(query: String) {
            if (this.query == query) {
                return
            }
            unregister()
            this.query = query
            nextPageLiveData = repository.searchNextPage(query)
            loadMoreState.value = LoadMoreState(
                isRunning = true,
                errorMessage = null
            )
            nextPageLiveData?.observeForever(this)
        }

        override fun onChanged(result: Resource<Boolean>?) {
            if (result == null) {
                reset()
            } else {
                when (result.status) {
                    Status.SUCCESS -> {
                        _hasMore = result.data == true
                        unregister()
                        loadMoreState.setValue(
                            LoadMoreState(
                                isRunning = false,
                                errorMessage = null
                            )
                        )
                    }
                    Status.ERROR -> {
                        _hasMore = true
                        unregister()
                        loadMoreState.setValue(
                            LoadMoreState(
                                isRunning = false,
                                errorMessage = result.message
                            )
                        )
                    }
                    Status.LOADING -> {
                        // ignore
                    }
                }
            }
        }

        private fun unregister() {
            nextPageLiveData?.removeObserver(this)
            nextPageLiveData = null
            if (_hasMore) {
                query = null
            }
        }

        fun reset() {
            unregister()
            _hasMore = true
            loadMoreState.value = LoadMoreState(
                isRunning = false,
                errorMessage = null
            )
        }
    }
}
