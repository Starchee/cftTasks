package com.starchee.fragmentnavigationtask

import android.content.Context
import androidx.fragment.app.Fragment
import java.lang.ClassCastException

abstract class BaseFragment: Fragment() {

    lateinit var navigationOnClickListener: NavigationOnClickListener

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try{
            navigationOnClickListener = context as NavigationOnClickListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement NavigationOnClickListener")
        }
    }
}