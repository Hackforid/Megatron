package com.smilehacker.Megatron.util

import android.view.View
import java.util.*

/**
 * Created by kleist on 2017/3/22.
 */

class FragmentOptions {
    private val mSharedElements : MutableMap<String, View> by lazy { HashMap<String, View>() }

    fun makeSceneTransitionAnimation(viewStart: View, transitionName: String) {
        mSharedElements[transitionName] = viewStart
    }

    fun getSharedElements(): MutableMap<String, View> {
        return mSharedElements
    }

}
