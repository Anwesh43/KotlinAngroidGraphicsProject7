package com.example.rightlinehypoleftview

import android.view.View
import android.view.MotionEvent
import android.content.Context
import android.app.Activity
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
val parts : Int = 6
val scGap : Float = 0.05f / parts
val strokeFactor : Float = 90f
val sizeFactor : Float = 5.9f
val delay : Long = 20
val backColor : Int = "#BDBDBD".toColorInt()
val rot : Float = 135f
val deg : Float = 90f
val totalLines = 2

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

val rots : Array<Float> = arrayOf(0f, 0f, rot)

fun Canvas.drawXY(x : Float, y : Float, cb : () -> Unit) {
    save()
    translate(x, y)
    cb()
    restore()
}

data class Line(var lineNumber : Int, var ix : Float, var iy : Float) {
    public var next : Line? = null
    init {
        if (lineNumber < totalLines) {
            var nix : Float = 0f
            var niy : Float = 0f
            if (ix == 0f) {
                niy = 0f
                nix = 1f
            } else {
                nix = 0f
                niy = 1f
            }
            next = Line(lineNumber + 1, nix, niy)
        }
    }

    fun draw(canvas : Canvas, size : Float, sc : Float, paint : Paint) {
        val dci : (Int) -> Float = {
            sc.divideScale(it, parts)
        }
        canvas.drawXY(0f, 0f) {
            canvas.rotate(rot * dci(lineNumber + 1))
            canvas.drawLine(0f, 0f, size * ix * dci(lineNumber), size * iy * dci(lineNumber), paint)
            canvas.drawXY(size * ix, size * iy) {
                next?.draw(canvas, size, sc, paint)
            }
        }

    }
}

val rootLine : Line = Line(0, 1f, 0f)


fun Canvas.drawRightLineHypoLeft(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val dsc : (Int) -> Float = {
        scale.divideScale(it, parts)
    }
    drawXY(w / 2 - (w / 2) * dsc(5), h / 2) {
        rotate(deg * dsc(4))
        drawXY(size, 0f) {
            rootLine.draw(this, size, scale, paint)
        }
    }
}

fun Canvas.drawRLHLNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i].toColorInt()
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    paint.style = Paint.Style.STROKE
    drawRightLineHypoLeft(scale, w, h, paint)
}

class RightLineHypoLeftView(ctx : Context) : View(ctx) {

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
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
                dir = 0f
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

    data class RLHLNode(var i : Int = 0) {

        private var next : RLHLNode? = null
        private var prev : RLHLNode? = null
        private val state : State = State()

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = RLHLNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawRLHLNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : RLHLNode {
            var curr : RLHLNode? = prev
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

    data class RightLineHypoLeft(var i : Int) {

        private var curr : RLHLNode = RLHLNode(0)
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

    data class Renderer(var view : RightLineHypoLeftView) {

        private val animator : Animator = Animator(view)
        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val rlhl : RightLineHypoLeft = RightLineHypoLeft(0)

        fun render(canvas : Canvas) {
            canvas.drawColor(backColor)
            rlhl.draw(canvas, paint)
            animator.animate {
                rlhl.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            rlhl.startUpdating {
                animator.start()
            }
        }
    }
}
