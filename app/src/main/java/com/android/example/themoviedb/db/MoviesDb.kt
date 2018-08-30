
package com.android.example.themoviedb.db


import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.android.example.themoviedb.vo.Movie
import com.android.example.themoviedb.vo.MovieSearchResult

/**
 * Main database overview.
 */
@Database(
    entities = [
        Movie::class,
        MovieSearchResult::class],
    version = 1,
    exportSchema = false
)
abstract class MoviesDb : RoomDatabase() {

    abstract fun movieDao(): MovieDao
}
