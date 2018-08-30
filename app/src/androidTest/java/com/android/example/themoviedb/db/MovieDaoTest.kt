
package com.android.example.themoviedb.db

import android.support.test.runner.AndroidJUnit4
import com.android.example.themoviedb.util.LiveDataTestUtil.getValue
import com.android.example.themoviedb.util.TestUtil
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class MovieDaoTest : DbTest() {
    @Test
    fun insertAndRead() {
        val movie = TestUtil.createMovie(1, "foo", "overview", "posterPath", Date())
        db.movieDao().insert(movie)
        val loaded = getValue(db.movieDao().load(1))
        assertThat(loaded, notNullValue())
        assertThat(loaded.title, `is`("bar"))
        assertThat(loaded.overview, `is`("desc"))
        assertThat(loaded.posterPath, `is`("posterPath"))
    }

    @Test
    fun createIfNotExists_exists() {
        val movie = TestUtil.createMovie("foo", "overview", "posterPath")
        db.movieDao().insert(movie)
        assertThat(db.movieDao().createMovieIfNotExists(movie), `is`(-1L))
    }

    @Test
    fun createIfNotExists_doesNotExist() {
        val movie = TestUtil.createMovie("foo", "overview", "posterPath")
        assertThat(db.movieDao().createMovieIfNotExists(movie), `is`(1L))
    }
}
