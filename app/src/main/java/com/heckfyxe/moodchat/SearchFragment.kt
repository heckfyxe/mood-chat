package com.heckfyxe.moodchat


import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.heckfyxe.moodchat.adapters.UserAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.processors.PublishProcessor
import kotlinx.android.synthetic.main.recycler_view.*
import java.util.concurrent.TimeUnit


class SearchFragment : androidx.fragment.app.Fragment() {

    companion object {
        private const val TAG = "SearchFragment"
        private const val KEY_SEARCH_QUERY = "com.heckfyxe.moodchat.KEY_SEARCH_QUERY"

        @JvmStatic
        fun newInstance() =
                SearchFragment()
    }

    private lateinit var adapter: UserAdapter

    private var searchQuery = ""
    private val searchProcessor = PublishProcessor.create<String>()
    private var disposables = mutableListOf<Disposable>()
    private lateinit var viewModel: SearchFragmentViewModel

    private var isAdvancedLoading = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = UserAdapter()

        disposables.add(searchProcessor
                .debounce(335, TimeUnit.MILLISECONDS)
                .onBackpressureLatest()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{
                    showProgressBar()
                    searchQuery = it
                    viewModel.updateUsers(searchQuery)
                })

        viewModel = ViewModelProviders.of(this).get(SearchFragmentViewModel::class.java)

        viewModel.usersLiveData.observe(this, Observer {
            if (it == null)
                return@Observer

            hideProgressBar()
            hideNotFoundText()

            if (it.error != null) {
                Log.e(TAG, it.error?.apiError?.errorMessage)
                Toast.makeText(activity, R.string.loading_error, Toast.LENGTH_SHORT).show()
                return@Observer
            }

            if (!it.isAdvanced) {
                if (it.result!!.isNotEmpty()) {
                    adapter.replaceUsers(it.result!!)
                } else {
                    adapter.clear()
                    showNotFoundText()
                }
            } else {
                // activity have recreated
                if (adapter.size() == 0) {
                    adapter.insertUsers(*it.data?.toTypedArray() ?: emptyArray())
                }
                adapter.hideLoadingView()
                isAdvancedLoading = false
                adapter.insertUsers(*it.result!!.toTypedArray())
            }
        })

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.recycler_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_view?.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        recycler_view?.adapter = adapter
        recycler_view?.setHasFixedSize(true)
        recycler_view?.isVerticalScrollBarEnabled = true
        recycler_view?.addOnScrollListener(object: androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: androidx.recyclerview.widget.RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (!recyclerView.canScrollVertically(1) && !isAdvancedLoading) {
                    isAdvancedLoading = true
                    adapter.showLoadingView()
                    viewModel.loadNextUsers(searchQuery)
                }
            }
        })

        if (viewModel.usersLiveData.value?.result == null)
            searchProcessor.onNext(searchQuery)
    }



    private fun showProgressBar() {
        recycler_view?.visibility = View.GONE
        recyclerTextView?.visibility = View.INVISIBLE
        progressBar?.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        recycler_view?.visibility = View.VISIBLE
        progressBar?.visibility = View.INVISIBLE
    }

    private fun showNotFoundText() {
        recycler_view?.visibility = View.GONE
        progressBar?.visibility = View.INVISIBLE
        recyclerTextView?.visibility = View.VISIBLE
    }

    private fun hideNotFoundText() {
        recyclerTextView?.visibility = View.INVISIBLE
        recycler_view?.visibility = View.VISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_search, menu)

        val searchView = menu.findItem(R.id.action_search)?.actionView as? SearchView
        searchView?.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchQuery = newText
                Log.i("SearchFragment", "OnTextChanged")
                searchProcessor.onNext(searchQuery)
                return true
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(KEY_SEARCH_QUERY, searchQuery)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        searchQuery = savedInstanceState?.getString(KEY_SEARCH_QUERY) ?: ""
    }

    override fun onDestroy() {
        super.onDestroy()

        searchProcessor.onComplete()
        disposables.forEach { disposable ->
            if (!disposable.isDisposed)
                disposable.dispose()
        }
    }
}
