package com.letter.cameraassistant

import android.app.Application

/**
 * Application
 *
 * @author Letter(nevermindzt@gmail.com)
 * @since 1.0.0
 */
class LetterApplication : Application() {

    companion object {
        /**
         * Application 单例
         */
        private var instance: LetterApplication ?= null

        /**
         * 获取Application实例
         * @return LetterApplication Application实例
         */
        @JvmStatic
        fun instance() = instance!!
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}