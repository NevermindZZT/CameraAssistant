package com.letter.cameraassistant.widget

import android.content.Context
import android.graphics.PixelFormat
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import androidx.core.content.getSystemService
import com.letter.cameraassistant.databinding.LayoutFloatingBallBinding

private const val TAG = "FloatingBallView"

class FloatingBallView(context: Context) : LinearLayout(context), View.OnTouchListener {

    private var binding: LayoutFloatingBallBinding =
        LayoutFloatingBallBinding.inflate(LayoutInflater.from(context), this, true)

    private var longPressed = false

    var aimClickListener: View.OnClickListener? = null

    private var vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?

    private val gestureDetector = GestureDetector(
        context,
        object : GestureDetector.SimpleOnGestureListener() {

            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                aimClickListener?.onClick(binding.floatingButton)
                return true
            }

            override fun onDoubleTap(e: MotionEvent?): Boolean {
                return true
            }

            override fun onLongPress(e: MotionEvent?) {
                longPressed = true
                if (vibrator?.hasVibrator() == true) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(100L, VibrationEffect.EFFECT_HEAVY_CLICK))
                }
            }
        }
    )

    init {
        binding.floatingButton.setOnTouchListener(this)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {

            }
            MotionEvent.ACTION_UP -> {
                longPressed = false
            }
            MotionEvent.ACTION_MOVE -> {
                val params = layoutParams as WindowManager.LayoutParams
                if (longPressed) {
                    params.x = (event.rawX - width / 2).toInt()
                    params.y = (event.rawY - height / 2).toInt()
                    context.getSystemService<WindowManager>()?.updateViewLayout(this, params)
                    invalidate()
                }
            }
        }
        return gestureDetector.onTouchEvent(event)
    }

    fun getAimPosition(): FloatArray {
        val location = IntArray(2)
        val aimWidget = binding.floatingButton
        aimWidget.getLocationOnScreen(location)
        return floatArrayOf(location[0].toFloat() - 1f, location[1].toFloat() - 1f)
    }

    fun show() {
        val layoutParams = WindowManager.LayoutParams()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        layoutParams.apply {
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            format = PixelFormat.RGBA_8888
            flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
            gravity = Gravity.START or Gravity.TOP
            x = 200
            y = 500
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
        }

        windowManager.addView(this, layoutParams)
//        measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        invalidate()
    }

    fun remove() {
        (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager?)?.removeView(this)
    }

}