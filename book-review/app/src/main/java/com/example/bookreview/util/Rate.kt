package com.example.bookreview.util

import com.example.bookreview.R

fun setRate(rate : Int) : Int {
    return when(rate){
        0 -> R.drawable.ic_rate_0
        1 -> R.drawable.ic_rate_1
        2 -> R.drawable.ic_rate_2
        3 -> R.drawable.ic_rate_3
        4 -> R.drawable.ic_rate_4
        else -> R.drawable.ic_rate_5
    }
}