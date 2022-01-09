package com.letter.cameraassistant.repository

class AssistantClickRepo {

    fun loopClickTask(interval: Int, times: Int, task: (() -> Unit)?) {
        var loopTime = times
        while (loopTime > 0 && !Thread.interrupted()) {
            loopTime --
            task?.invoke()
            Thread.sleep(interval.toLong())
        }
    }
}