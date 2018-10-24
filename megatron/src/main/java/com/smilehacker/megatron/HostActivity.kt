package com.smilehacker.megatron

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.smilehacker.megatron.util.DLog

/**
 * Created by kleist on 16/6/6.
 */
abstract class HostActivity : AppCompatActivity(), IFragmentAction {

    abstract fun getContainerID() : Int

    private lateinit var mFragmentController : FragmentController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFragmentController = ViewModelProviders.of(this).get(FragmentController::class.java)
        mFragmentController.init(this)
    }

    override fun <T : KitFragment> startFragment(to: Class<T>, bundle: Bundle?, launchMode: Int) {
        mFragmentController.start(null, to, bundle ?: Bundle(), launchMode)
    }

    override fun <T : KitFragment> startFragmentForResult(to: Class<T>, bundle: Bundle?, requestCode: Int, launchMode: Int) {
        mFragmentController.start(null, to, bundle ?: Bundle(), requestCode)
    }

    override fun onBackPressed() {
        val top = mFragmentController.getTopFragment(supportFragmentManager)
        if (top == null || top !is IKitFragment) {
            return
        }
        if (top.onBackPress()) {
            return
        }

        if (mFragmentController.getStackCount() > 1) {
            mFragmentController.finish(supportFragmentManager, top)
        } else {
            finish()
        }
    }

    fun logFragmentStack() {
        DLog.i("===stack ${mFragmentController.hashCode()} ===")
        supportFragmentManager?.let {
            mFragmentController.getFragments(it).forEach {
                DLog.d(it.javaClass.name + ":" + it.tag)
            }
        }
        DLog.i("===end===")
    }
}