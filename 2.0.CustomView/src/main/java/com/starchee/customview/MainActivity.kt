package com.starchee.customview

import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        accelerator.setOnTouchListener { view, motionEvent ->
            if ( motionEvent.action == MotionEvent.ACTION_DOWN) {
                speedometer.start()
            } else {
                speedometer.stop()
            }
            view.performClick()
        }
    }

}
