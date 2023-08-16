package com.example.topstories.util

import java.text.SimpleDateFormat
import java.util.Calendar

fun getDateToday() : String{
    val today = Calendar.getInstance().time
    val formatter = SimpleDateFormat("yyyy-MM-dd")
    return formatter.format(today)
}