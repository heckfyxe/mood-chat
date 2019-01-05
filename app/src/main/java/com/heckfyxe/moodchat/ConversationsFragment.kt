package com.heckfyxe.moodchat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.heckfyxe.moodchat.adapter.ConversationAdapter
import kotlinx.android.synthetic.main.fragment_conversations.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class ConversationsFragment : Fragment() {

    private val model: ConversationsViewModel by viewModel()
    private lateinit var conversationAdapter: ConversationAdapter

    companion object {
        fun newInstance() = ConversationsFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        conversationAdapter = ConversationAdapter {
            startActivity(Intent(context, MessagesActivity::class.java).apply {
                putExtra(MessagesActivity.EXTRA_PEER_ID, it.peerId)
                putExtra(MessagesActivity.EXTRA_LAST_MESSAGE_ID, it.lastMessageId)
            })
        }
        model.pagedList.observe(this, Observer {
            conversationAdapter.submitList(it)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_conversations, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        conversationRecyclerView?.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = conversationAdapter
            setHasFixedSize(true)
        }

        conversationSwipeRefresh?.setOnRefreshListener {
            val updateStatus = model.refresh()
            updateStatus.observe(this, Observer {
                conversationSwipeRefresh?.isRefreshing = false
            })
        }

        model.refresh()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        model.errors.observe(this, Observer {
            Log.e("ConversationFragment", it.toString())
            Snackbar.make(view!!, it.apiError.errorMessage, Snackbar.LENGTH_LONG)
                .setAction(R.string.retry) {
                    model.refresh()
                }.show()
        })
    }
}
