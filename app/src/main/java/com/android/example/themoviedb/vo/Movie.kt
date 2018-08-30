package com.android.example.themoviedb.vo

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.TypeConverters
import com.android.example.themoviedb.api.TmdbService.Companion.IMAGES_URL
import com.android.example.themoviedb.db.TmdbTypeConverters
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(
        indices = [
            Index("id")],
        primaryKeys = ["id"]
)
@TypeConverters(TmdbTypeConverters::class)
data class Movie(
        val id: Int,
        @field:SerializedName("title")
        val title: String,
        @field:SerializedName("overview")
        val overview: String,
        @field:SerializedName("poster_path")
        val posterPath: String?,
        @field:SerializedName("release_date")
        val releaseDate: Date
) {

    companion object {
        const val UNKNOWN_ID = -1
    }

    fun releaseYear(): String {
        val instance = Calendar.getInstance()
        instance.time = releaseDate
        return instance.get(Calendar.YEAR).toString()
    }

    fun posterUri(): String {
        return IMAGES_URL.plus(posterPath)
    }
}
