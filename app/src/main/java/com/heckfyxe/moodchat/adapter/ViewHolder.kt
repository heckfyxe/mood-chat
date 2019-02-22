package com.heckfyxe.moodchat.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class ViewHolder<M : Any>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(model: M)
}