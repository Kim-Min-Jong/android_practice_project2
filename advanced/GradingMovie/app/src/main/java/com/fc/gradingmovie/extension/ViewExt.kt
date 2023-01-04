package com.fc.gradingmovie.extension

import android.view.View
import androidx.annotation.Px
import com.fc.gradingmovie.extension.dip

@Px
fun View.dip(dipValue: Float) = context.dip(dipValue)

fun View.toVisible() {
    visibility = View.VISIBLE
}

fun View.toGone() {
    visibility = View.GONE
}