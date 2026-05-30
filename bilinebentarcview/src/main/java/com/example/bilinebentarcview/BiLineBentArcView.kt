package com.example.bilinebentarcview

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.RectF
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
val scGap : Float = 0.05f / parts
val bentDeg : Float = 45f
val rot : Float = 180f
val strokeFactor : Float = 90f
val sizeFactor : Float = 5.9f
val delay : Long = 20

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawXY(x : Float, y : Float, cb : () -> Unit) {
    save()
    translate(x, y)
    cb()
    restore()
}

fun Canvas.scaleXY(sx : Float, sy : Float, cb : () -> Unit) {
    drawXY(0f, 0f) {
        scale(sx, sy)
        cb()
    }
}

fun Canvas.drawBiLineBentArc(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val dsc : (Int) -> Float = {
        scale.divideScale(it, parts)
    }
    drawXY(w / 2, h / 2 + (h  / 2) * dsc(5)) {
        rotate(rot * dsc(3))
        for (j in 0..1) {
            scaleXY(1f - 2 * j, 1f) {
                drawXY((w / 4) * (1 - dsc(2)), 0f) {
                    rotate(bentDeg * dsc(1))
                    drawLine(0f, 0f, 0f, -size * dsc(0), paint)
                }
            }
        }
        drawArc(RectF(-size / 2, -size / 2, size / 2, size / 2), 150f, bentDeg * dsc(4), false, paint)

    }
}

fun Canvas.drawBLBANode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i].toColorInt()
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    paint.style = Paint.Style.STROKE
    drawBiLineBentArc(scale, w, h, paint)
}

