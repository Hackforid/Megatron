package com.smilehacker.Megatron

import android.os.Bundle
import android.support.v4.app.Fragment

/**
 * Created by kleist on 16/6/7.
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

    fun <T : Fragment> startFragment(to : Class<T>, bundle: Bundle? = null,
                                     launchMode : Int = FragmentController.FRAGMENT.LAUNCH_MODE.STANDARD)


    fun <T : Fragment> startFragmentForResult(to : Class<T>, bundle: Bundle? = null,
                                              requestCode: Int, launchMode : Int = FragmentController.FRAGMENT.LAUNCH_MODE.STANDARD)

    fun popFragment()

    fun <T : Fragment> popToFragment(fragment: Class<T>, bundle: Bundle? = null, includeSelf: Boolean = false)
}

interface IKitFragmentActor : IFragmentAction {

    var fragmentResult: FragmentResult?

    val hostActivity : HostActivity

    fun finish()

    fun setResult(resultCode: Int, data: Bundle? = null)

}

interface IKitFragmentAction : IKitFragmentActor {

    fun onVisible()

    fun onInvisible()

    fun onNewBundle(bundle: Bundle?)

    fun getAnimation() : Pair<Int, Int>?

    fun onBackPress() : Boolean

    fun onFragmentResult(requestCode: Int, resultCode: Int, data: Bundle?)

}
