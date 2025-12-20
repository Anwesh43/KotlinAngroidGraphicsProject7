package com.example.concrotsiderightview

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.RectF
import androidx.core.graphics.toColorInt

val colors : Array<String> = arrayOf(
    "#1A237E",
    "#EF5350",
    "#AA00FF",
    "#C51162",
    "#00C853"
)
val parts : Int = 5
val rot : Float = 180f
val deg : Float = 180f
val backColor : Int = "#BDBDBD".toColorInt()
val scGap : Float = 0.04f / parts
val strokeFactor : Float = 90f
val sizeFactor : Float = 5.9f
val delay : Long = 20

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int): Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int): Float = Math.min(n.inverse(), maxScale(i, n)) * n
