
package com.android.example.themoviedb.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.android.example.themoviedb.api.*
import com.android.example.themoviedb.api.TmdbService.Companion.API_KEY
import com.android.example.themoviedb.api.TmdbService.Companion.DISCOVER_SORTING
import com.android.example.themoviedb.db.MoviesDb
import com.android.example.themoviedb.vo.MovieSearchResult
import com.android.example.themoviedb.vo.Resource
import java.io.IOException

/**
 * A task that reads the search result in the database and fetches the page page, if it has one.
 */
class FetchNextSearchPageTask constructor(
        private val query: String,
        private val tmdbService: TmdbService,
        private val db: MoviesDb
) : Runnable {
    private val _liveData = MutableLiveData<Resource<Boolean>>()
    val liveData: LiveData<Resource<Boolean>> = _liveData

    override fun run() {
        val current = db.movieDao().findSearchResult(query)
        if (current == null) {
            _liveData.postValue(null)
            return
        }
        val nextPage = current.page?.plus(1)
        if (nextPage == null || nextPage > current.totalPages) {
            _liveData.postValue(Resource.success(false))
            return
        }
        val newValue = try {
            val response =
                    if (query.isNullOrBlank()) {
                        tmdbService.discoverMovies(API_KEY, DISCOVER_SORTING, nextPage).execute()
                    } else {
                        tmdbService.searchMovies(API_KEY, query, nextPage).execute()
                    }
            val apiResponse = ApiResponse.create(response)
            when (apiResponse) {
                is ApiSuccessResponse -> {
                    // merge all movie ids into 1 list so that it is easier to fetch the
                    // result list.
                    val ids = arrayListOf<Int>()
                    ids.addAll(current.movieIds)

                    ids.addAll(apiResponse.body.items.map { it.id })
                    val merged = MovieSearchResult(query, ids, apiResponse.body.totalResults,
                            apiResponse.body.totalPages, apiResponse.body.page
                    )
                    try {
                        db.beginTransaction()
                        db.movieDao().insert(merged)
                        db.movieDao().insertMovies(apiResponse.body.items)
                        db.setTransactionSuccessful()
                    } finally {
                        db.endTransaction()
                    }
                    Resource.success(apiResponse.body.page < apiResponse.body.totalPages)
                }
                is ApiEmptyResponse -> {
                    Resource.success(false)
                }
                is ApiErrorResponse -> {
                    Resource.error(apiResponse.errorMessage, true)
                }
            }

        } catch (e: IOException) {
            Resource.error(e.message!!, true)
        }
        _liveData.postValue(newValue)
    }
}
