package com.android.example.themoviedb.api

import android.arch.lifecycle.LiveData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * REST API access points
 */
interface TmdbService {

    companion object {
        const val BASE_URL = "https://api.themoviedb.org/3/"
        const val API_KEY = "93aea0c77bc168d8bbce3918cefefa45"
        const val IMAGES_URL = "http://image.tmdb.org/t/p/w154"
        const val DISCOVER_SORTING = "popularity.desc"
    }

    @GET("discover/movie")
    fun discoverMovies(@Query("api_key") apiKey: String,
                       @Query("sort_by") sortBy: String): LiveData<ApiResponse<MovieSearchResponse>>

    @GET("discover/movie")
    fun discoverMovies(@Query("api_key") apiKey: String,
                       @Query("sort_by") sortBy: String,
                       @Query("page") page: Int): Call<MovieSearchResponse>

    @GET("search/movie")
    fun searchMovies(@Query("api_key") apiKey: String,
                     @Query("query") query: String): LiveData<ApiResponse<MovieSearchResponse>>

    @GET("search/movie")
    fun searchMovies(@Query("api_key") apiKey: String,
                     @Query("query") query: String,
                     @Query("page") page: Int): Call<MovieSearchResponse>
}
