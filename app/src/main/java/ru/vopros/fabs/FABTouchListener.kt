package ru.vopros.fabs

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

class FABTouchListener(
    private val onSwipe: () -> Unit
) : View.OnTouchListener {

    private var startX: Float = 0f
    private var downX: Float = 0f
    private var upX: Float = 0f

    private companion object {
        const val MAX_DISTANCE = 1000
        const val MIN_DISTANCE = 800

        fun inRange(x: Float): Boolean {
            return abs(x) > MIN_DISTANCE && abs(x) < MAX_DISTANCE
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = v.x
                downX = v.x - event.rawX
            }
            MotionEvent.ACTION_UP -> {
                upX = event.rawX
                val delta = downX - upX
                return if (!inRange(delta)) {
                    onSwipe()
                    true
                } else {
                    v.x = startX
                    false
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val delta = event.rawX + downX
                if (delta < (MAX_DISTANCE + downX)) {
                    val alpha = if (!inRange(delta)) 0.25f else 1f
                    v.animate()
                        .x(event.rawX + downX)
                        .alpha(alpha)
                        .setDuration(0)
                        .start()
                }
            }
            else -> return false
        }
        return false
    }

}