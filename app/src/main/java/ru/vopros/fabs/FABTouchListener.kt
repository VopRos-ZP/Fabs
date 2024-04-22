package ru.vopros.fabs

import android.annotation.SuppressLint
import android.util.Log
import android.view.MotionEvent
import android.view.View

class FABTouchListener(
    private val startX: Float,
    private val onSwipe: () -> Unit
) : View.OnTouchListener {

    private var downX: Float = 0f

    private companion object {
        const val MAX_DISTANCE = 1100
        const val MIN_DISTANCE = 700

        fun inRange(x: Float): Boolean {
            return x > MIN_DISTANCE && x < MAX_DISTANCE
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = v.x - event.rawX
                Log.d("FABTouchListener", "start -> $startX")
                Log.d("FABTouchListener", "raw -> ${event.rawX}")
                Log.d("FABTouchListener", "down -> $downX")
            }
            MotionEvent.ACTION_UP -> {
                val delta = event.rawX + downX
                val isNotInRange = !inRange(delta)
                if (isNotInRange) {
                    onSwipe()
                } else {
                    v.postOnAnimation {
                        v.x = startX
                    }
                }
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val alpha = if (!inRange(event.rawX + downX)) 0.25f else 1f
                v.animate()
                    .x(event.rawX + downX)
                    .alpha(alpha)
                    .setDuration(0)
                    .start()
                return false
            }
            else -> return false
        }
        return false
    }

}