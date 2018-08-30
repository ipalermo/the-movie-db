
package com.android.example.themoviedb.ui.common

import android.databinding.DataBindingComponent
import android.databinding.DataBindingUtil
import android.support.v7.util.DiffUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import com.android.example.themoviedb.AppExecutors
import com.android.example.themoviedb.R
import com.android.example.themoviedb.databinding.MovieItemBinding
import com.android.example.themoviedb.vo.Movie

/**
 * A RecyclerView adapter for [Movie] class.
 */
class MovieListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors) : DataBoundListAdapter<Movie, MovieItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.overview == newItem.overview
                    && oldItem.title == newItem.title
        }
    }
) {

    override fun createBinding(parent: ViewGroup): MovieItemBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.movie_item,
            parent,
            false,
                dataBindingComponent
        )
    }

    override fun bind(binding: MovieItemBinding, item: Movie) {
        binding.movie = item
    }
}
