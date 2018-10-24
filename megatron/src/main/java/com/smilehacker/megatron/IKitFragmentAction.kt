package com.smilehacker.megatron

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

        val STATE_SAVE_ENTER = "state_save_enter"
        val STATE_SAVE_EXIT = "state_save_exit"
        val STATE_SAVE_POP_ENTER = "state_save_pop_enter"

        val STATE_SAVE_POP_EXIT = "state_save_pop_exit"

    }

    fun <T : KitFragment> startFragment(to : Class<T>, bundle: Bundle? = null,
                                     launchMode : Int = FragmentController.LAUNCH_MODE.STANDARD)


    fun <T : KitFragment> startFragmentForResult(to : Class<T>, bundle: Bundle? = null,
                                              requestCode: Int, launchMode : Int = FragmentController.LAUNCH_MODE.STANDARD)
}


internal interface IKitFragment : IFragmentAction {



    val hostActivity: HostActivity

    fun finish()

    fun setResult(resultCode: Int, data: Bundle? = null)

    fun setShareElements(vararg shareElements : Pair<View, String>)


    fun getShareElementPairs() : MutableList<Pair<View, String>>?

    var transitionAnimation : Pair<Int, Int>?

    fun onVisible()

    fun onInvisible()

    fun onNewBundle(bundle: Bundle?)

    fun onBackPress() : Boolean

    fun onFragmentResult(requestCode: Int, resultCode: Int, data: Bundle?)
}
