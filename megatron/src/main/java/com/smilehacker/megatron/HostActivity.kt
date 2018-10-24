package com.smilehacker.megatron

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.smilehacker.megatron.util.DLog

/**
 * Created by kleist on 16/6/6.
 */
abstract class HostActivity : AppCompatActivity(), IFragmentAction {

    abstract fun getContainerID() : Int

    private lateinit var mFragmentation : Fragmentation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFragmentation = ViewModelProviders.of(this).get(Fragmentation::class.java)
        mFragmentation.init(this)
    }

    override fun <T : KitFragment> startFragment(to: Class<T>, bundle: Bundle?, launchMode: Int) {
        mFragmentation.start(supportFragmentManager, to, bundle, launchMode, null)
    }

    override fun <T : KitFragment> startFragmentForResult(to: Class<T>, bundle: Bundle?, requestCode: Int, launchMode: Int) {
        mFragmentation.start(supportFragmentManager, to, bundle, requestCode)
    }

    override fun popFragment() {
        onBackPressed()
    }

    override fun <T : KitFragment> popToFragment(fragment: Class<T>, bundle: Bundle?, includeSelf: Boolean) {
        mFragmentation.popTo(supportFragmentManager, fragment, bundle, includeSelf)
    }

    override fun onBackPressed() {
        val top = mFragmentation.getTopFragment(supportFragmentManager)
        if (top == null || top !is IKitFragment) {
            return
        }
        if (top.onBackPress()) {
            return
        }

        if (mFragmentation.getStackCount() > 1) {
            mFragmentation.finish(supportFragmentManager, top)
        } else {
            finish()
        }
    }

    fun logFragmentStack() {
        DLog.i("===stack ${mFragmentation.hashCode()} ===")
        supportFragmentManager?.let {
            mFragmentation.getFragments(it).forEach {
                DLog.d(it.javaClass.name + ":" + it.tag)
            }
        }
        DLog.i("===end===")
    }
}