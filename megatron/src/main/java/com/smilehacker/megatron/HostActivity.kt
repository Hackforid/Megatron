package com.smilehacker.megatron

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
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
        mNavigator = ViewModelProviders.of(this).get(Navigator::class.java)
        mNavigator.init(this)
        mNavigator.getStackCleared().observe(this, Observer<Boolean> { cleared -> Log.i("xx", "cleared $cleared") })
    }

    override fun push(fragment: KitFragment) {
        mNavigator.push(fragment)
    }

    override fun pop(fragment: KitFragment, data: FragmentResult?) {
        mNavigator.pop(fragment, data)
    }

    override fun popTo(fragment: KitFragment, data: FragmentResult?) {
        mNavigator.popTo(fragment, data)
    }

    override fun onBackPressed() {
        val top = mNavigator.getTopFragment() ?: return
        if (top.onBackPress()) {
            return
        }

        if (mNavigator.getStackCount() > 1) {
            mNavigator.pop(top)
        } else {
            finish()
        }
    }

    fun logFragmentStack() {
        DLog.i("===stack ${mNavigator.hashCode()} ===")
        supportFragmentManager?.let {
            mNavigator.getFragments().forEach {
                DLog.d(it.javaClass.name + ":" + it.tag)
            }
        }
        DLog.i("===end===")
    }
}