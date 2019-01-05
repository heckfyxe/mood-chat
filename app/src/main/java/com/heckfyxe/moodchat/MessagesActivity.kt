package com.heckfyxe.moodchat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MessagesActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PEER_ID = "com.heckfyxe.moodchat.EXTRA_PEER_ID"
        const val EXTRA_LAST_MESSAGE_ID = "com.heckfyxe.moodchat.EXTRA_LAST_MESSAGE_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)

        val peerId = intent.getIntExtra(EXTRA_PEER_ID, -1)
        if (peerId == -1)
            throw Exception("Intent hasn't EXTRA_PEER_ID value")

        val lastMessageId = intent.getIntExtra(EXTRA_LAST_MESSAGE_ID, -1)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.messages_fragment_container,
                    MessagesFragment.newInstance(peerId, lastMessageId))
                .commitNow()
        }

        actionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
    }

}
