package com.smilehacker.megatron

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.smilehacker.megatron.util.DLog

/**
 * Created by kleist on 16/6/6.
 */
abstract class HostActivity : AppCompatActivity(), IFragmentAction {

    abstract fun getContainerID() : Int

    private lateinit var mNavigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNavigator = Navigator.of(this)
        mNavigator.getStackCleared().observe(this, Observer<Boolean> { cleared ->
            Log.i("xx", "cleared $cleared")
            if (cleared == true) {
                finish()
            }
        })
    }

    override fun push(fragment: KitFragment) : LiveData<Bundle> {
        return mNavigator.push(fragment)
    }

    override fun pop(fragment: KitFragment) {
        mNavigator.pop(fragment)
    }

    override fun popTo(fragment: KitFragment) {
        mNavigator.popTo(fragment)
    }

    override fun onBackPressed() {
        val top = mNavigator.getTopFragment()
        if (top == null) {
            finish()
            return
        }
        if (top.onBackPress()) {
            return
        }
        mNavigator.pop(top)
    }

    fun logFragmentStack() {
        DLog.i("===stack ${mNavigator.hashCode()} ===")
        supportFragmentManager?.let {
            mNavigator.getFragmentTags().forEach {
                DLog.d(it)
            }

        }
        DLog.i("===end===")
    }
}