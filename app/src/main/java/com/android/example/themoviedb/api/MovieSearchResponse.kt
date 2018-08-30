package com.android.example.themoviedb.api


import com.android.example.themoviedb.vo.Movie
import com.google.gson.annotations.SerializedName

/**
 * Simple object to hold movie search responses. This is different from the Entity in the database
 * because I´m keeping a search result in 1 row and denormalizing list of results into a single
 * column.
 */
data class MovieSearchResponse(
        @SerializedName("page")
        var page: Int = 1,
        @SerializedName("total_results")
        val totalResults: Int = 0,
        @SerializedName("total_pages")
        val totalPages: Int = 0,
        @SerializedName("results")
        val items: List<Movie>
)
