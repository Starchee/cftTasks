package com.starchee.workermanager

import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), Timer.OnTickListener {

    private var timer: Timer? = null
    private var startTimeInSeconds = 0L

    companion object {
        private const val TIME_IN_SECONDS_KEY = "time in second key"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timer = Timer.getInstance(applicationContext)
        timer?.onTickListener = this

        tv_timer.setOnClickListener {
            TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                startTimeInSeconds = hourOfDay * 60L * 60L + minute * 60L
                setTextTimer(startTimeInSeconds)
            }, 0, 0, true).show()
        }

        btn_timer.setOnClickListener {
            timer?.let {
                if (it.isStopped.get()) {
                    timer?.startTimeInSeconds = startTimeInSeconds
                    timer?.start()
                } else {
                    timer?.stop()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("BOLL", "NOTIFY")
        timer?.notifyWhenEnd()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(TIME_IN_SECONDS_KEY, startTimeInSeconds)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        startTimeInSeconds = savedInstanceState.getLong(TIME_IN_SECONDS_KEY)
        setTextTimer(startTimeInSeconds)
    }

    override fun onTick(secondsUntilFinished: Long) {
        setTextTimer(secondsUntilFinished)
    }

    private fun setTextTimer(secondsUntilFinished: Long) {
        val hours = secondsUntilFinished / (60 * 60)
        val minutes = secondsUntilFinished / 60 - hours * 60
        val seconds = secondsUntilFinished - minutes * 60 - hours * 60 * 60
        startTimeInSeconds = secondsUntilFinished
        tv_timer.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}
