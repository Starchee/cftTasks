package com.starchee.workermanager

import android.content.Context
import android.os.Handler
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class Timer private constructor(context: Context) {

    @Volatile
    var startTimeInSeconds: Long = 0
    var isStopped = AtomicBoolean(true)
    var onTickListener: OnTickListener? = null


    private val workManager = WorkManager.getInstance(context)

    interface OnTickListener {
        fun onTick(secondsUntilFinished: Long)
    }

    private val handler = Handler()

    companion object {
        private const val REQUEST_TAG = "time finish request"
        private var instance: Timer? = null
        fun getInstance(context: Context): Timer? {
            if (instance == null) {
                instance = Timer(context)
            }
            return instance
        }
    }

    fun start() {
        if (!isStopped.get()){
            stop()
        }
        isStopped.set(false)
        Thread {
            while (startTimeInSeconds > 0 && !isStopped.get()) {
                try {
                    startTimeInSeconds--
                    onTickListener?.let {
                        handler.post {
                            it.onTick(startTimeInSeconds)
                        }
                    }
                    Thread.sleep(1000)
                } catch (t: Throwable) {
                }
            }
        }.start()
    }

    fun stop() {
        isStopped.set(true)
        workManager.cancelAllWorkByTag(REQUEST_TAG)
    }

    fun notifyWhenEnd() {
        if ( startTimeInSeconds > 0) {
            onTickListener = null
            notifyEndTimer(startTimeInSeconds)
        }
    }

    private fun notifyEndTimer(secondsUntilFinished: Long) {
        val request = OneTimeWorkRequestBuilder<TimerWorker>()
            .addTag(REQUEST_TAG)
            .setInitialDelay(secondsUntilFinished, TimeUnit.SECONDS)
            .build()
        workManager.enqueue(request)
    }

}