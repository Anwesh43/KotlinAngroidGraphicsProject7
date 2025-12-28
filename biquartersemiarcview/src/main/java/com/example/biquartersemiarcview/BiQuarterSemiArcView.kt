package com.example.biquartersemiarcview

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
val scGap : Float = 0.05f / parts
val rot : Float = 90f
val delay : Long = 20
val strokeFactor : Float = 90f
val sizeFactor : Float = 5.9f
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

fun Canvas.drawBiQuarterSemiArc(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val dsc : (Int) -> Float = {
        scale.divideScale(it, parts)
    }
    drawXY(w / 2, h / 2 + (h / 2) * dsc(5)) {
        drawXY(0f, 0f) {
            rotate(rot * dsc(4))
            drawLine(0f, 0f, 0f, -size * 0.5f * dsc(2), paint)
            drawArc(RectF(-size / 2, -size / 2, size / 2, size / 2), -90f, 90f * dsc(3), false, paint)
        }
        drawArc(RectF(-size / 2, -size / 2, size / 2, size / 2), 90f, 90f * dsc(0), false, paint)
        drawXY(-size / 2, 0f) {
            drawLine(0f, 0f, size * 0.5f * dsc(1), 0f, paint)
        }
    }
}

fun Canvas.drawBQSANode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i].toColorInt()
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    paint.style = Paint.Style.STROKE
    drawBiQuarterSemiArc(scale, w, h, paint)
}

class BiQuarterSemiArcView(ctx : Context) : View(ctx) {

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas: Canvas) {
        renderer.render(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir == 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir === 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            cb()
            try {
                Thread.sleep(delay)
                view.invalidate()
            } catch(ex : Exception) {

            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class BQSANode(var i : Int = 0, val state : State = State()) {

        private var next : BQSANode? = null
        private var prev : BQSANode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = BQSANode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawBQSANode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : BQSANode {
            var curr : BQSANode? = prev
            if (dir === 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class BiQuarterSemiArc(var i : Int) {

        private var curr : BQSANode = BQSANode(0)
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : BiQuarterSemiArcView) {

        private val animator : Animator = Animator(view)
        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val bqsa : BiQuarterSemiArc = BiQuarterSemiArc(0)

        fun render(canvas : Canvas) {
            canvas.drawColor(backColor)
            bqsa.draw(canvas, paint)
            animator.animate {
                bqsa.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            bqsa.startUpdating {
                animator.start()
            }
        }
    }

    companion object {
        fun create(activity : Activity) : BiQuarterSemiArcView {
            val view : BiQuarterSemiArcView = BiQuarterSemiArcView(activity)
            activity.setContentView(view)
            return view
        }
    }
}
