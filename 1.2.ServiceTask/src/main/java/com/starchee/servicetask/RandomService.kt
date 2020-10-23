package com.starchee.servicetask

import android.app.IntentService
import android.content.Intent
import android.util.Log
import kotlin.random.Random

class RandomService : IntentService(null) {

    companion object {
        private val TAG = RandomService::class.java.simpleName
        const val ACTION = "com.starchee.servicetask.service.receiver"
        const val RESULT = "result"
    }

    override fun onHandleIntent(p0: Intent?) {
        try {
            for (i in 1..50) {
                Thread.sleep(1000)
                sendData("Random number is  ${Random.nextInt(1,21)}")
            }
        } catch (e: InterruptedException) {
            Log.e(TAG, e.toString())
        }

    }

    private fun sendData(data: String) {
        val intent = Intent().apply {
            action = ACTION
            putExtra(RESULT, data)
        }
        sendBroadcast(intent)
    }
}