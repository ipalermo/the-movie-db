
package com.android.example.themoviedb.ui.search

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingComponent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.IBinder
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.navigation.fragment.findNavController
import com.android.example.themoviedb.AppExecutors
import com.android.example.themoviedb.R
import com.android.example.themoviedb.binding.FragmentDataBindingComponent
import com.android.example.themoviedb.databinding.SearchFragmentBinding
import com.android.example.themoviedb.di.Injectable
import com.android.example.themoviedb.testing.OpenForTesting
import com.android.example.themoviedb.ui.common.MovieListAdapter
import com.android.example.themoviedb.ui.common.RetryCallback
import com.android.example.themoviedb.util.autoCleared
import javax.inject.Inject

@OpenForTesting
open class SearchFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    var binding by autoCleared<SearchFragmentBinding>()

    var adapter by autoCleared<MovieListAdapter>()

    lateinit var searchViewModel: SearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.search_fragment,
            container,
            false,
            dataBindingComponent
        )

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        searchViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(SearchViewModel::class.java)
        initRecyclerView()
        val rvAdapter = MovieListAdapter(
            dataBindingComponent = dataBindingComponent,
            appExecutors = appExecutors
        )
        binding.movieList.adapter = rvAdapter
        adapter = rvAdapter

        initSearchInputListener()

        binding.callback = object : RetryCallback {
            override fun retry() {
                searchViewModel.refresh()
            }
        }

        searchViewModel.setQuery("")
    }

    private fun initSearchInputListener() {
        binding.input.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(newText: String?): Boolean {
                doSearch(newText)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                doSearch(newText)
                return false
            }
        })
    }

    private fun doSearch(query: String?) {
        binding.query = query
        searchViewModel.setQuery(query!!)
    }

    private fun initRecyclerView() {

        binding.movieList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastPosition = layoutManager.findLastVisibleItemPosition()
                if (lastPosition == adapter.itemCount - 1) {
                    searchViewModel.loadNextPage()
                }
            }
        })
        searchViewModel.results.observe(this, Observer { result ->
            binding.searchResource = result
            binding.resultCount = result?.data?.size ?: 0
            adapter.submitList(result?.data)
        })

        searchViewModel.loadMoreStatus.observe(this, Observer { loadingMore ->
            if (loadingMore == null) {
                binding.loadingMore = false
            } else {
                binding.loadingMore = loadingMore.isRunning
                val error = loadingMore.errorMessageIfNotHandled
                if (error != null) {
                    Snackbar.make(binding.loadMoreBar, error, Snackbar.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun dismissKeyboard(windowToken: IBinder) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(windowToken, 0)
    }

    /**
     * Created to be able to override in tests
     */
    open fun navController() = findNavController()
}
