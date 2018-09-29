package com.heckfyxe.moodchat


import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.heckfyxe.moodchat.adapters.UserAdapter
import com.vk.sdk.api.*
import com.vk.sdk.api.model.VKUsersArray
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
    private lateinit var disposable: Disposable

    private val vkRequestListener by lazy { object : VKRequest.VKRequestListener() {

        override fun onComplete(response: VKResponse) {
            val users = VKUsersArray().apply {
                parse(response.json)
            }

            adapter.users.clear()
            adapter.users.addAll(users)
            adapter.notifyDataSetChanged()
        }

        override fun onError(error: VKError) {
            Log.e(TAG, error.apiError.errorMessage)
            Toast.makeText(activity, R.string.loading_error, Toast.LENGTH_SHORT).show()
        }
    }}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        disposable = searchProcessor
                .debounce(335, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{
                    searchQuery = it
                    updateUsers(searchQuery)
                }

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.recycler_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = UserAdapter()

        recycler_view?.layoutManager = LinearLayoutManager(context)
        recycler_view?.adapter = adapter
        recycler_view?.setHasFixedSize(true)

        searchProcessor.onNext(searchQuery)
    }

    private fun updateUsers(q: String?) {
        if (q != null && q.isNotEmpty()) {
            VKApi.users().search(VKParameters(mapOf(
                    VKApiConst.Q to q,
                    VKApiConst.FIELDS to "photo_50")))
        } else {
            VKApi.users().search(VKParameters(mapOf(
                    VKApiConst.FIELDS to "photo_50"
            )))
        }.executeWithListener(vkRequestListener)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_search, menu)

        val searchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchQuery = newText
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
        Log.i("Disposable" , disposable.isDisposed.toString())
        if (!disposable.isDisposed)
            disposable.dispose()
    }
}
