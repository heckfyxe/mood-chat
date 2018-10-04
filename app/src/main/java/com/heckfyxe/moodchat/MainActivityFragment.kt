package com.heckfyxe.moodchat

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import com.vk.sdk.VKSdk
import kotlinx.android.synthetic.main.fragment_main.*

class MainActivityFragment : androidx.fragment.app.Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() =
                MainActivityFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).setSupportActionBar(fragment_main_toolbar)
        activity?.invalidateOptionsMenu()

        main_bottom_nav?.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_search -> {
                    replaceContent(SearchFragment.newInstance())
                    fragment_main_toolbar?.setTitle(R.string.search)
                    true
                }
                R.id.action_chats -> {
                    replaceContent()
                    fragment_main_toolbar?.setTitle(R.string.chats)
                    true
                }
                R.id.action_friends -> {
                    replaceContent()
                    fragment_main_toolbar?.setTitle(R.string.friends)
                    true
                }
                else -> {
                    false
                }
            }
        }

        main_bottom_nav?.selectedItemId = main_bottom_nav?.selectedItemId ?: R.id.action_search
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.sign_out, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
            when (item.itemId) {
                R.id.ic_sign_out -> {
                    VKSdk.logout()
                    activity!!.setResult(AppCompatActivity.RESULT_OK)
                    activity!!.finish()
                    true
                }
                else -> false
            }

    private fun replaceContent(fragment: androidx.fragment.app.Fragment = androidx.fragment.app.Fragment()) {
        val fm = activity!!.supportFragmentManager
        val fmFragment = fm.findFragmentById(R.id.content_fragment_container)
        if (fmFragment == null)
            fm.beginTransaction()
                .add(R.id.content_fragment_container, fragment)
                .commit()
        else {
            if (fragment is SearchFragment && fmFragment is SearchFragment) {  //TODO()
                return
            }

            fm.beginTransaction()
                    .replace(R.id.content_fragment_container, fragment)
                    .commit()
        }
    }
}
