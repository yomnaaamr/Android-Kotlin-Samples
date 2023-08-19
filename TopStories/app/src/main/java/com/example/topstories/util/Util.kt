package com.example.topstories.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date

@SuppressLint("SimpleDateFormat")
fun getDateToday() : String{
    // Create a SimpleDateFormat object with the desired format
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+00:00")
    val currentDate = Date()
    return sdf.format(currentDate)
}