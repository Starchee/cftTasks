package com.starchee.servicetask

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startService(Intent(this, RandomService::class.java))
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                val x = p1?.getStringExtra(RandomService.RESULT)
                x?.let {
                    hello.text = it
                }
            }
        }
        registerReceiver(receiver, IntentFilter(RandomService.ACTION))
    }
}
