package com.heckfyxe.moodchat

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.vk.sdk.VKSdk
import kotlinx.android.synthetic.main.fragment_main.*

class MainActivityFragment : androidx.fragment.app.Fragment() {

    private val fragments = arrayOf(
        SearchFragment.newInstance(),
        ConversationsFragment.newInstance())

    companion object {
        @JvmStatic
        fun newInstance() =
            MainActivityFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).setSupportActionBar(fragment_main_toolbar)
        activity?.invalidateOptionsMenu()

        main_bottom_nav?.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_search -> {
                    mainViewPager?.setCurrentItem(0, true)
                    fragment_main_toolbar?.setTitle(R.string.search)
                    true
                }
                R.id.action_chats -> {
                    mainViewPager?.setCurrentItem(1, true)
                    fragment_main_toolbar?.setTitle(R.string.chats)
                    true
                }
                R.id.action_friends -> {
                    fragment_main_toolbar?.setTitle(R.string.friends)
                    true
                }
                else -> {
                    false
                }
            }
        }

        mainViewPager?.adapter = object: FragmentPagerAdapter(fragmentManager) {
            override fun getItem(position: Int): Fragment =
                fragments[position]

            override fun getCount(): Int =
                fragments.size
        }

        mainViewPager?.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                main_bottom_nav?.selectedItemId = when (position) {
                    0 -> R.id.action_search
                    1 -> R.id.action_chats
                    else -> return
                }
            }
        })

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
}
