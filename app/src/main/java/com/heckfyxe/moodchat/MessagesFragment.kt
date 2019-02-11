package com.heckfyxe.moodchat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.heckfyxe.moodchat.adapter.MessageAdapter
import com.heckfyxe.moodchat.database.ConversationDao
import com.heckfyxe.moodchat.database.GroupDao
import com.heckfyxe.moodchat.database.UserDao
import com.heckfyxe.moodchat.model.Conversation
import com.vk.sdk.api.model.VKApiConversation
import kotlinx.android.synthetic.main.fragment_messages.*
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject

class MessagesFragment : Fragment() {

    private val conversationDao: ConversationDao by inject()
    private val userDao: UserDao by inject()
    private val groupDao: GroupDao by inject()

    companion object {
        private const val KEY_PEER_ID = "com.heckfyxe.moodchat.KEY_PEER_ID"
        private const val KEY_LAST_MESSAGE_ID = "com.heckfyxe.moodchat.KEY_LAST_MESSAGE_ID"

        fun newInstance(peerId: Int, lastMessageId: Int = -1): MessagesFragment {
            val fragment = MessagesFragment()
            fragment.arguments = Bundle().apply {
                putInt(KEY_PEER_ID, peerId)
                putInt(KEY_LAST_MESSAGE_ID, lastMessageId)
            }
            return fragment
        }
    }

    private val viewModel: MessagesViewModel by inject()
    private val scope = CoroutineScope(Dispatchers.Main)

    private lateinit var conversationDeferred: Deferred<Conversation?>

    private var lastMessageId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments?.containsKey(KEY_PEER_ID) == false) {
            throw Exception("Fragment arguments don't contain value with KEY_PEER_ID")
        }

        val peerId = arguments!!.getInt(KEY_PEER_ID)
        lastMessageId = arguments!!.getInt(KEY_LAST_MESSAGE_ID, -1)
        viewModel.init(peerId, lastMessageId)

        conversationDeferred = scope.async(Dispatchers.IO) {
            conversationDao.getConversationByPeerId(peerId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_messages, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scope.launch {
            val conversation = conversationDeferred.await()

//            conversation.unreadCount.also {
//                if (it > 0) {
//                    viewModel.updateHistory(lastMessageId, it)
//                }
//            }

            val title = when (conversation?.type) {
                VKApiConversation.Type.CHAT ->
                    conversation.chatSettings!!.title

                VKApiConversation.Type.USER, VKApiConversation.Type.EMAIL ->
                    withContext(Dispatchers.IO) {
                        userDao.getUserById(conversation.peerId).let {
                            it?.firstName + "" + it?.lastName
                        }
                    }

                VKApiConversation.Type.GROUP ->
                    withContext(Dispatchers.IO) {
                        groupDao.getGroupById(conversation.localId)?.name
                    }
                else -> ""
            }

            this@MessagesFragment.activity?.actionBar?.title = title

            val messageAdapter = MessageAdapter(conversation?.type == VKApiConversation.Type.CHAT)
            messagesRecyclerView?.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, true)
                adapter = messageAdapter
            }

            viewModel.pagedList.observe(this@MessagesFragment, Observer {
                messageAdapter.submitList(it)
            })
        }
    }
}
