package com.heckfyxe.moodchat

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (supportFragmentManager.findFragmentById(R.id.main_fragment_container) == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.main_fragment_container, MainActivityFragment.newInstance())
                .commit()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()

        setResult(Activity.RESULT_CANCELED)
    }
}
