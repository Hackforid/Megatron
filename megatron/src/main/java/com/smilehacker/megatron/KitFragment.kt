package com.smilehacker.megatron

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View

/**
 * Created by kleist on 16/6/6.
 */
abstract class KitFragment : Fragment(), IKitFragment {

    companion object {
        private const val KEY_IS_HIDDEN = "key_is_hidden"
    }

    override val hostActivity: HostActivity by lazy { activity as HostActivity }
    private val mFragmentation: Fragmentation by lazy { ViewModelProviders.of(hostActivity).get(Fragmentation::class.java) }

    override var fragmentResult: FragmentResult? = null

    private var mShareElementPairs: MutableList<Pair<View, String>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            val isHidden = savedInstanceState.getBoolean(KEY_IS_HIDDEN, false)
            val ft = fragmentManager?.beginTransaction()
            if (isHidden) {
                ft?.hide(this)
            } else {
                ft?.show(this)
            }
            ft?.commit()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onVisible()
    }

    override fun onNewBundle(bundle: Bundle?) {
    }

    override fun onBackPress(): Boolean {
        return false
    }

    override fun onFragmentResult(requestCode: Int, resultCode: Int, data: Bundle?) {
    }

    override fun onVisible() {
        logFragmentStack()
    }

    override fun onInvisible() {
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_IS_HIDDEN, isHidden)
    }


    private fun logFragmentStack() {
        hostActivity.logFragmentStack()
    }

    override fun <T : KitFragment> startFragment(to: Class<T>, bundle: Bundle?, launchMode: Int) {
        mFragmentation.start(fragmentManager!!, to, bundle, launchMode)
    }

    override fun <T : KitFragment> startFragmentForResult(to: Class<T>, bundle: Bundle?, requestCode: Int, launchMode: Int) {
        mFragmentation.start(fragmentManager!!, to, bundle, launchMode, requestCode)
    }


    override fun popFragment() {
        finish()
    }

    override fun <T : KitFragment> popToFragment(to: Class<T>, bundle: Bundle?, includeSelf: Boolean) {
        mFragmentation.popTo(fragmentManager!!, to, bundle, includeSelf)
    }

    override fun finish() {
        mFragmentation.finish(fragmentManager!!, this)
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
