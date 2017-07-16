package com.smilehacker.Megatron

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.View
import com.smilehacker.Megatron.util.DLog

/**
 * Created by kleist on 16/8/2.
 */
abstract class KitPreferenceFragment(val kitFragmentActor: IKitFragmentActor) : PreferenceFragmentCompat(), IKitFragment, IKitFragmentActor by kitFragmentActor {

    companion object {
        const val KEY_IS_HIDDEN = "key_is_hidden"
    }
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

    constructor() : this(KitFragmentActor()) {
        if (kitFragmentActor is KitFragmentActor) {
            kitFragmentActor.fragment = this
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
}
