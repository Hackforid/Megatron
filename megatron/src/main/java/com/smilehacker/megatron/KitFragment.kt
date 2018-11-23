package com.smilehacker.megatron

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v4.app.Fragment
import android.view.View
import com.smilehacker.megatron.util.DLog
import java.lang.ref.WeakReference

/**
 * Created by kleist on 16/6/6.
 */
abstract class KitFragment : Fragment(), IKitFragment {

    companion object {
        private const val KEY_IS_HIDDEN = "key_is_hidden"
    }

    override val hostActivity: HostActivity by lazy { activity as HostActivity }
    private val mNavigator: Navigator by lazy { Navigator.of(hostActivity) }

    private var mShareElementPairs: MutableList<Pair<View, String>>? = null

    private lateinit var mKitFragmentViewModel : KitFragmentViewModel
    private var mTempResultLiveData: WeakReference<MutableLiveData<Bundle>>? = null
    protected var mTempResult: Bundle? = null


    @CallSuper
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

    @CallSuper
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mKitFragmentViewModel = ViewModelProviders.of(this).get(KitFragmentViewModel::class.java)
        if (mTempResultLiveData != null) {
            mKitFragmentViewModel.resultLiveDataRef = mTempResultLiveData
        }
        if (mTempResult != null) {
            mKitFragmentViewModel.result = mTempResult
        }
    }

    @CallSuper
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        DLog.i("onHidden change $hidden ${this.tag}")
        if (hidden) {
            onInvisible()
        } else {
            onVisible()
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        DLog.i("userVisibleHint change $isVisibleToUser ${this.tag}")
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onHiddenChanged(false)
    }

    fun getNavigator() = mNavigator

    override fun pop(fragment: KitFragment) {
        mNavigator.pop(fragment)
    }

    fun pop() {
        mNavigator.pop(this)
    }

    override fun popTo(fragment: KitFragment) {
        mNavigator.popTo(fragment)
    }

    override fun push(fragment: KitFragment) : LiveData<Bundle> {
        return mNavigator.push(fragment)
    }

    override fun onBackPress(): Boolean {
        return false
    }

    override fun onVisible() {
        logFragmentStack()
    }

    override fun onInvisible() {
    }

    override fun onResume() {
        super.onResume()
        DLog.i("onResume $tag")
    }

    override fun onPause() {
        super.onPause()
        DLog.i("onPause $tag")
    }

    override fun onStart() {
        super.onStart()
        DLog.i("onStart $tag")
    }

    override fun onStop() {
        super.onStop()
        DLog.i("onStop $tag")
    }

    @CallSuper
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        DLog.i("is Hidden $isHidden")
        outState.putBoolean(KEY_IS_HIDDEN, isHidden)
    }


    private fun logFragmentStack() {
        hostActivity.logFragmentStack()
    }

    override fun setShareElements(vararg shareElements: Pair<View, String>) {
        mShareElementPairs = shareElements.toMutableList()
    }


    override fun getShareElementPairs(): MutableList<Pair<View, String>>? {
        return mShareElementPairs
    }

    override var transitionAnimation: Pair<Int, Int>? = null

    protected fun setResult(data: Bundle) {
        if (this::mKitFragmentViewModel.isInitialized) {
            mKitFragmentViewModel.result = data
        } else {
            mTempResult = data
        }
    }

    protected fun getResult() = mKitFragmentViewModel.result

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
    }

    internal fun setResultLiveData(data: MutableLiveData<Bundle>) {
        if (this::mKitFragmentViewModel.isInitialized) {
            mKitFragmentViewModel.resultLiveDataRef = WeakReference(data)
        } else {
            mTempResultLiveData = WeakReference(data)
        }
    }

    internal fun getResultLiveData() = mKitFragmentViewModel.resultLiveDataRef?.get()

    internal fun handleClose() {
        mKitFragmentViewModel.resultLiveDataRef?.get()?.value = mKitFragmentViewModel.result
    }

    /**
     * 一些internal的信息存在viewmodel里防止statechange时丢失
     */
    internal class KitFragmentViewModel: ViewModel() {
        var result: Bundle? = null
        var resultLiveDataRef : WeakReference<MutableLiveData<Bundle>>? = null
    }

}
