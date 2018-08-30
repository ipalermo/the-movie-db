package com.android.example.themoviedb.util

import com.android.example.themoviedb.vo.Movie
import java.util.*

object TestUtil {

    fun createMovies(count: Int, title: String, overview: String): List<Movie> {
        return (0 until count).map {
            createMovie(
                    title = title + it,
                    overview = overview + it,
                    posterPath = "/path$it"
            )
        }
    }

    fun createMovie(title: String, overview: String, posterPath: String) = createMovie(
            id = Movie.UNKNOWN_ID,
            title = title,
            overview = overview,
            posterPath = posterPath,
            releaseDate = Date()
    )

    fun createMovie(id: Int, title: String, overview: String, posterPath: String, releaseDate: Date) = Movie(
            id = id,
            title = title,
            overview = overview,
            posterPath = posterPath,
            releaseDate = releaseDate
    )
}
