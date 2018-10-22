package com.smilehacker.megatron

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View

/**
 * Created by kleist on 16/8/2.
 */
class KitFragmentActor() : IKitFragmentActor {

    override val hostActivity : HostActivity by lazy { fragment.activity as HostActivity }
    private val mFragmentation : Fragmentation by lazy { ViewModelProviders.of(hostActivity).get(Fragmentation::class.java) }
    lateinit var fragment : Fragment

    override var fragmentResult: FragmentResult? = null

    private var mShareElementPairs : MutableList<Pair<View, String>>? = null

    constructor(fragment : Fragment) : this() {
        this.fragment = fragment
    }

    override fun <T : Fragment> startFragment(to: Class<T>, bundle: Bundle?, launchMode: Int) {
        mFragmentation.start(fragment.fragmentManager!!, to, bundle, launchMode)
    }

    override fun <T : Fragment> startFragmentForResult(to: Class<T>, bundle: Bundle?, requestCode: Int, launchMode: Int) {
        mFragmentation.start(fragment.fragmentManager!!, to, bundle, launchMode, Fragmentation.START_TYPE.ADD_WITH_RESULT, requestCode)
    }


    override fun popFragment() {
        finish()
    }

    override fun <T : Fragment> popToFragment(to: Class<T>, bundle: Bundle?, includeSelf: Boolean) {
        mFragmentation.popTo(fragment.fragmentManager!!, to, bundle, includeSelf)
    }

    override fun finish() {
        mFragmentation.finish(fragment.fragmentManager!!, fragment)
    }
    override fun setResult(resultCode: Int, data: Bundle?) {
        fragmentResult?.let { it.data = data; it.resultCode = resultCode }
    }

    override fun setShareElements(vararg shareElements: Pair<View, String>) {
        mShareElementPairs = shareElements.toMutableList()
    }


    override fun getShareElementPairs(): MutableList<Pair<View, String>>? {
        return mShareElementPairs
    }

    override var transitionAnimation: Pair<Int, Int>? = null
}