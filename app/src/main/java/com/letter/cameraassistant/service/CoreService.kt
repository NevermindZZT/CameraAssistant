package com.letter.cameraassistant.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.app.Instrumentation
import android.content.Intent
import android.graphics.Path
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import com.afollestad.materialdialogs.MaterialDialog
import com.letter.cameraassistant.repository.AssistantClickRepo
import com.letter.cameraassistant.ui.dialog.assistantConfig
import com.letter.cameraassistant.widget.FloatingBallView

private const val TAG = "CoreService"

class CoreService : AccessibilityService() {

    private val floatingBallView by lazy {
        FloatingBallView(this)
    }

    private val assistantClickRepo by lazy {
        AssistantClickRepo()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
        floatingBallView.show()
        floatingBallView.aimClickListener = View.OnClickListener {
            assistantClickRepo.stop()
            MaterialDialog(this).show {
                assistantConfig { _, data ->
                    Log.d(TAG, "$data")
                    if (data.photosTime > 0) {
                        if (!data.intelligenceMode) {
                            assistantClickRepo.loopClickTask(data.photosInterval, data.photosTime) {
                                clickAim()
//                                sendKey(KeyEvent.KEYCODE_CAMERA)
                            }
                        } else {
                            AssistantClickRepo.IntelligenceMode.start(data.photosTime) {
                                clickAim()
                            }
                        }
                    }
                }
                window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
            }
        }
    }

    override fun onDestroy() {
        floatingBallView.remove()
        super.onDestroy()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        AssistantClickRepo.IntelligenceMode.call()
    }

    override fun onInterrupt() {

    }

    private fun checkViewState() {
        val rootNode = rootInActiveWindow
        for (i in 0 until rootNode.childCount) {
            val child = rootNode.getChild(i)
            Log.d(TAG, "child: $child")
        }
    }

    private fun clickPosition(x: Float, y: Float) {
        val path = Path()
        path.moveTo(x, y)
        val gestureDescription = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 100L, 100L))
            .build()
        dispatchGesture(
            gestureDescription,
            object : AccessibilityService.GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription?) {
                    super.onCompleted(gestureDescription)
                    Log.d(TAG, "dispatchGesture completed")
                }

                override fun onCancelled(gestureDescription: GestureDescription?) {
                    super.onCancelled(gestureDescription)
                    Log.w(TAG, "dispatchGesture cancelled")
                }
            },
            null
        )
    }

    private fun clickAim() {
        val position = floatingBallView.getAimPosition()
        clickPosition(position[0], position[1])
        Log.d(TAG, "click at: ${position[0]}, ${position[1]}")
    }

    private fun sendKey(keyCode: Int) {
        val instrumentation = Instrumentation()
        instrumentation.sendKeyDownUpSync(keyCode)
    }

}