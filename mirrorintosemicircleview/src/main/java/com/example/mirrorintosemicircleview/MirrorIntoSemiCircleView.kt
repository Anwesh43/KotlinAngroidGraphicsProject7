package com.example.mirrorintosemicircleview

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Canvas
import androidx.core.graphics.toColorInt
import android.app.Activity
import android.content.Context

val colors : Array<String> = arrayOf(
    "#1A237E",
    "#EF5350",
    "#AA00FF",
    "#C51162",
    "#00C853"
)
val parts : Int = 6
val scGap :Float = 0.05f / parts
val strokeFactor : Float = 90f
val sizeFactor : Float = 5.9f
val rot : Float = 90f
val delay : Long = 20
val backColor : Int = "#BDBDBD".toColorInt()
