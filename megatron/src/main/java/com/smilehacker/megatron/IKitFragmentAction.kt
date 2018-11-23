package com.smilehacker.megatron

import android.arch.lifecycle.LiveData
import android.os.Bundle
import android.view.View

/**
 * Created by kleist on 16/6/7.
 */

/**
 * base interface
 */
interface IFragmentAction {
    companion object {

        val RESULT_CANCELED = 0
        val RESULT_OK = 1
    }

    fun push(fragment: KitFragment) : LiveData<Bundle>

    fun pop(fragment: KitFragment)

    fun popTo(fragment: KitFragment)
}


internal interface IKitFragment : IFragmentAction {



    val hostActivity: HostActivity

    fun setShareElements(vararg shareElements : Pair<View, String>)

    fun getShareElements() : MutableList<Pair<View, String>>?

    /**
     * first enter
     * second exit
     */
    var transitionAnimation : Pair<Int, Int>?

    fun onVisible()

    fun onInvisible()

    fun onBackPress() : Boolean
}
