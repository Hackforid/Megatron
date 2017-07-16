package com.smilehacker.Megatron

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity

/**
 * Created by kleist on 16/6/6.
 */
abstract class HostActivity : AppCompatActivity(), IFragmentAction {

    companion object {
        const val KEY_FRAGMENTATION = "key_fragmentation"
    }

    abstract fun getContainerID() : Int

    lateinit var mFragmentation : Fragmentation

    override fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            mFragmentation = savedInstanceState.getParcelable(KEY_FRAGMENTATION)
            mFragmentation.init(this)
        } else {
            mFragmentation = Fragmentation()
            mFragmentation.init(this)
        }
        super.onCreate(savedInstanceState)
    }

    override fun <T : Fragment> startFragment(to: Class<T>, bundle: Bundle?, launchMode: Int) {
        mFragmentation.start(supportFragmentManager, to, bundle, launchMode, Fragmentation.START_TYPE.ADD)
    }

    override fun <T : Fragment> startFragmentForResult(to: Class<T>, bundle: Bundle?, requestCode: Int, launchMode: Int) {
        mFragmentation.start(supportFragmentManager, to, bundle, Fragmentation.START_TYPE.ADD_WITH_RESULT, requestCode)
    }

    override fun popFragment() {
        onBackPressed()
    }

    override fun <T : Fragment> popToFragment(fragment: Class<T>, bundle: Bundle?, includeSelf: Boolean) {
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
            mFragmentation.finish(supportFragmentManager, top!!)
        } else {
            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelable(KEY_FRAGMENTATION, mFragmentation)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
    }
}