package com.heckfyxe.moodchat.util

import java.util.*

private val calendar: GregorianCalendar by lazy { GregorianCalendar() }

fun getHHMM(unixtime: Long): String {
    val calendar = calendar.apply {
        timeInMillis = unixtime * 1000L
    }
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    return "%02d:%02d".format(hour, minute)
}