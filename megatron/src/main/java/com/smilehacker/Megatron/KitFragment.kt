package com.smilehacker.Megatron

import android.annotation.TargetApi
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.smilehacker.Megatron.model.SharedTransition
import com.smilehacker.Megatron.util.DLog

/**
 * Created by kleist on 16/6/6.
 */
abstract class KitFragment : Fragment(), IKitFragmentAction {

    companion object {
        const val KEY_IS_HIDDEN = "key_is_hidden"
    }

    val mFragmentActor by lazy { KitFragmentActor(this) }

    override var fragmentResult: FragmentResult? = mFragmentActor.fragmentResult

    override val hostActivity: HostActivity
        get() = mFragmentActor.hostActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            val isHidden = savedInstanceState.getBoolean(KEY_IS_HIDDEN, false)
            val ft = fragmentManager.beginTransaction()
            if (isHidden) {
                ft.hide(this)
            } else {
                ft.show(this)
            }
            ft.commit()
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            onInvisible()
        } else {
            onVisible()
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onVisible()
    }

    override fun <T : Fragment> startFragment(to: Class<T>, bundle: Bundle?, launchMode: Int,
                                              sharedTransition: SharedTransition?) {
        mFragmentActor.startFragment(to, bundle, launchMode, sharedTransition)
    }


    override fun <T : Fragment> startFragmentForResult(to: Class<T>, bundle: Bundle?, requestCode: Int, launchMode: Int, sharedTransition: SharedTransition?) {
        mFragmentActor.startFragmentForResult(to, bundle, requestCode, launchMode, sharedTransition)
    }


    override fun popFragment() {
        mFragmentActor.popFragment()
    }

    override fun <T : Fragment> popToFragment(fragment: Class<T>, bundle: Bundle?, includeSelf: Boolean) {
        mFragmentActor.popToFragment(fragment, bundle, includeSelf)
    }

    override fun finish() {
        mFragmentActor.finish()
    }

    override fun setResult(resultCode: Int, data: Bundle?) {
        mFragmentActor.setResult(resultCode, data)

    }

    override fun onNewBundle(bundle: Bundle?) {
    }

    override fun onBackPress(): Boolean {
        return false
    }

    override fun onFragmentResult(requestCode: Int, resultCode: Int, data: Bundle?) {
    }

    override fun getAnimation(): Pair<Int, Int>? {
        return null
    }

    override fun onVisible() {
        logFragmentStack()
    }

    override fun onInvisible() {
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putBoolean(KEY_IS_HIDDEN, isHidden)
    }


    private fun logFragmentStack() {
        DLog.i("===stack ${hostActivity.mFragmentation.hashCode()} ===")
        hostActivity.mFragmentation.getFragments(fragmentManager).forEach {
            DLog.d(it.javaClass.name + ":" + it.tag)
        }
        DLog.i("===end===")
    }

    @TargetApi(21)
    override fun getSharedTransition(): SharedTransition? {
        return mFragmentActor.getSharedTransition()
    }

    override fun setSharedTransition(sharedTransition: SharedTransition?) {
        mFragmentActor.setSharedTransition(sharedTransition)
    }

    override fun makeSceneTransitionAnimation(viewStart: View, transitionName: String) {
    }
}
