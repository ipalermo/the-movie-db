
package com.android.example.themoviedb.db

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.util.SparseIntArray
import com.android.example.themoviedb.testing.OpenForTesting
import com.android.example.themoviedb.vo.Movie
import com.android.example.themoviedb.vo.MovieSearchResult
import java.util.*

/**
 * Interface for database access on Movie related operations.
 */
@Dao
@OpenForTesting
abstract class MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(vararg movie: Movie)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertMovies(movies: List<Movie>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun createMovieIfNotExists(movie: Movie): Long

    @Query("SELECT * FROM movie WHERE id = :id")
    abstract fun load(id: Int): LiveData<Movie>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(result: MovieSearchResult)

    @Query("SELECT * FROM MovieSearchResult WHERE `query` = :query")
    abstract fun search(query: String): LiveData<MovieSearchResult>

    fun loadOrdered(movieIds: List<Int>): LiveData<List<Movie>> {
        val order = SparseIntArray()
        movieIds.withIndex().forEach {
            order.put(it.value, it.index)
        }
        return Transformations.map(loadById(movieIds)) { movies ->
            Collections.sort(movies) { m1, m2 ->
                val pos1 = order.get(m1.id)
                val pos2 = order.get(m2.id)
                pos1 - pos2
            }
            movies
        }
    }

    @Query("SELECT * FROM Movie WHERE id in (:movieIds)")
    abstract fun loadById(movieIds: List<Int>): LiveData<List<Movie>>

    @Query("SELECT * FROM MovieSearchResult WHERE `query` = :query")
    abstract fun findSearchResult(query: String): MovieSearchResult?
}
