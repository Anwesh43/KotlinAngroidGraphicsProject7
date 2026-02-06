package com.example.quarterarcjoinhalfview

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Canvas
import androidx.core.graphics.toColorInt

val colors : Array<String> = arrayOf(
    "#1A237E",
    "#EF5350",
    "#AA00FF",
    "#C51162",
    "#00C853"
)
val parts : Int = 5
val scGap : Float = 0.04f / parts
val sizeFactor : Float = 5.9f
val strokeFactor : Float = 90f
val rot : Float = 90f
val delay : Long = 20
val backColor : Int = "#BDBDBD".toColorInt()

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawXY(x : Float, y : Float, cb : () -> Unit) {
    save()
    translate(x, y)
    cb()
    restore()
}

fun Canvas.drawQuarterArcJoinHalf(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val dsc : (Int) -> Float = {
        scale.divideScale(it, parts)
    }
    drawXY(w / 2, h / 2 + (h / 2) * dsc(5)) {
        for (j in 0..1) {
            drawXY(0f, 0f) {
                rotate(rot * (1 + j) * dsc(3 + j)) //3, j = 0, 4, j= 1
                drawXY((w / 2) * (1 - dsc(0)), 0f) {
                    drawLine(0f, 0f, size, 0f, paint)
                }
                drawXY(0f, -h * 0.5f * (1 - dsc(1))) {
                    drawLine(0f, 0f, 0f, -size, paint)
                }
                drawArc(RectF(-size, -size, size, size), -90f, rot * dsc(2), false, paint)
            }
        }
    }
}

fun Canvas.drawQAJHNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i].toColorInt()
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    paint.style = Paint.Style.STROKE
    drawQuarterArcJoinHalf(scale, w, h, paint)
}
