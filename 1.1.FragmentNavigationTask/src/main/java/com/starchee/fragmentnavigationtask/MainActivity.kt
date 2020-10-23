package com.starchee.fragmentnavigationtask

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager

class MainActivity : AppCompatActivity(), NavigationOnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startFirstFragment()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else super.onBackPressed()
    }

    override fun startFirstFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, FirstFragment())
            .commit()
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    override fun startSecondFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.main_container, SecondFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun startThirdFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.main_container, ThirdFragment())
            .addToBackStack(null)
            .commit()
    }
}
