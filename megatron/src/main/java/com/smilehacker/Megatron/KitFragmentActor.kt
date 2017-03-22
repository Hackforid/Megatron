package com.smilehacker.Megatron

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import java.util.*

/**
 * Created by kleist on 16/8/2.
 */
class KitFragmentActor(val fragment: Fragment) : IKitFragmentActor {

    override val hostActivity : HostActivity by lazy { fragment.activity as HostActivity }
    private val mFragmentation : Fragmentation by lazy { hostActivity.mFragmentation }
    private val mSharedElements : MutableMap<String, View> by lazy { HashMap<String, View>() }

    override var fragmentResult: FragmentResult? = null

    init {
        if (fragment !is IKitFragmentAction) {
            throw IllegalArgumentException("fragment must impl IKitFragmentAction")
        }
    }

    override fun <T : Fragment> startFragment(to: Class<T>, bundle: Bundle?, launchMode: Int) {
        mFragmentation.start(fragment.fragmentManager, to, bundle, launchMode)
    }

    override fun <T : Fragment> startFragmentForResult(to: Class<T>, bundle: Bundle?, requestCode: Int, launchMode: Int) {
        mFragmentation.start(fragment.fragmentManager, to, bundle, launchMode, Fragmentation.START_TYPE.ADD_WITH_RESULT, requestCode)
    }


    override fun popFragment() {
        finish()
    }

    override fun <T : Fragment> popToFragment(to: Class<T>, bundle: Bundle?, includeSelf: Boolean) {
        mFragmentation.popTo(fragment.fragmentManager, to, bundle, includeSelf)
    }

    override fun finish() {
        mFragmentation.finish(fragment.fragmentManager, fragment)
    }
    override fun setResult(resultCode: Int, data: Bundle?) {
        fragmentResult?.let { it.data = data; it.resultCode = resultCode }
    }

    override fun makeSceneTransitionAnimation(viewStart: View, transitionName: String) {
        mSharedElements[transitionName] = viewStart
    }

    override fun getSharedElements(): MutableMap<String, View> {
        return mSharedElements
    }
}
