package com.android.example.themoviedb.vo

import android.arch.persistence.room.Entity
import android.arch.persistence.room.TypeConverters
import com.android.example.themoviedb.db.TmdbTypeConverters

@Entity(primaryKeys = ["query"])
@TypeConverters(TmdbTypeConverters::class)
data class MovieSearchResult(
        val query: String,
        val movieIds: List<Int>,
        val totalCount: Int,
        val totalPages: Int,
        val page: Int?
)
