package com.letter.cameraassistant.repository

class AssistantClickRepo {

    private var thread: Thread? = null

    fun loopClickTask(interval: Int, time: Int, task: (() -> Unit)?) {
        thread = Thread {
            var loopTime = time / interval
            while (loopTime > 0 && !Thread.interrupted()) {
                loopTime --
                task?.invoke()
                Thread.sleep(interval.toLong())
            }
        }
        thread?.start()
    }

    fun stop() {
        if (thread != null && thread?.isAlive == true) {
            thread?.interrupt()
            thread = null
        }
        IntelligenceMode.stop()
    }

    object IntelligenceMode {

        private var stopTime: Long = 0L
        private var task: (() -> Unit)? = null

        fun start(time: Int, task: (() -> Unit)?) {
            stopTime = System.currentTimeMillis() + time
            this.task = task
            task?.invoke()
        }

        fun call() {
            if (System.currentTimeMillis() < stopTime) {
                task?.invoke()
            }
        }

        fun stop() {
            stopTime = 0L
        }
    }
}