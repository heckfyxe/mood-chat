package com.heckfyxe.moodchat.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun inflate(parent: ViewGroup, @LayoutRes layout: Int): View {
    val inflater = LayoutInflater.from(parent.context)
    return inflater.inflate(layout, parent, false)
}