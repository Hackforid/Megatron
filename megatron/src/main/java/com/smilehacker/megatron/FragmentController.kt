package com.smilehacker.megatron

import android.os.Bundle
import android.support.v4.app.FragmentManager

/**
 * Created by kleist on 16/6/6.
 */
class FragmentController(val activity: HostActivity) {

    object FRAGMENT {
        const val IS_ROOT = "key_is_root"

        object LAUNCH_MODE {
            const val SINGLE_TOP = 1
            const val SINGLE_TASK = 2
            const val STANDARD = 3
        }
    }

    object START_TYPE {
        const val ADD = 1
        const val ADD_WITH_RESULT = 2
        const val ADD_AND_POP = 3
    }

    fun start(fragmentManager: FragmentManager, from: KitFragment?, to: KitFragment,
              launchMode: Int = FRAGMENT.LAUNCH_MODE.STANDARD,
              startType: Int = START_TYPE.ADD, requestCode: Int = 0) {
        val ft = fragmentManager.beginTransaction()
        val toName = to.javaClass.name

        when (startType) {
            START_TYPE.ADD_WITH_RESULT -> {
                to.fragmentResult = FragmentResult(requestCode)
            }
            START_TYPE.ADD_AND_POP -> {
                fragmentManager.popBackStack()
            }
        }

        if (handleLaunchMode(fragmentManager, to, launchMode)) {
            return
        }

        if (from == null) {
            val bundle = to.arguments ?: Bundle()
            bundle.putBoolean(FRAGMENT.IS_ROOT, true)
            ft.setCustomAnimations(R.anim.frg_slide_in_from_bottom, R.anim.frg_slide_in_from_bottom)
            ft.add(activity.getContainerID(), to, toName)
        } else {
            ft.setCustomAnimations(R.anim.frg_slide_in_from_bottom, R.anim.frg_slide_in_from_bottom)
            ft.add(activity.getContainerID(), to, toName)
            ft.hide(from)
        }

        ft.addToBackStack(toName)
        ft.commitNow()
    }

    private fun handleLaunchMode(fragmentManager: FragmentManager, to: KitFragment, launchMode: Int) : Boolean {
        when(launchMode) {
            FRAGMENT.LAUNCH_MODE.SINGLE_TOP -> {
                val fragments = fragmentManager.fragments
                val index = fragments.indexOf(to)
                if (index == fragmentManager.backStackEntryCount - 1) {
                    // TODO:new bundle
                    handleNewBundle(to, null)
                    return true
                }
            }
            FRAGMENT.LAUNCH_MODE.SINGLE_TASK -> {
                // TODO: support singleTask
                fragmentManager.popBackStackImmediate(to.javaClass.name, 0)
                handleNewBundle(to, null)
                return true
            }
        }
        return false
    }

    private fun handleNewBundle(to: KitFragment, bundle : Bundle?) {
        to.onNewBundle(bundle)
    }

    fun pop(fragmentManager: FragmentManager) {
        if (fragmentManager.backStackEntryCount > 1) {
            handleBack(fragmentManager)
        }
    }

    private fun handleBack(fragmentManager: FragmentManager) {
        val fragments = fragmentManager.fragments
        var result : FragmentResult? = null
        var findTop = false
        if (fragments != null) {
            for (i in (fragments.size - 1) downTo 0) {
                val frg = fragments[i]
                if (frg != null && frg is KitFragment) {
                    if (!findTop) {
                        result = frg.fragmentResult
                        if (result == null) {
                            break
                        }
                        findTop = true
                    } else {
                        if (result != null) {
                            frg.onFragmentResult(result.requestCode, result.resultCode, result.data)
                        }
                        break
                    }
                }
            }
        }
        fragmentManager.popBackStackImmediate()
    }

    fun getTopFragment(fragmentManager: FragmentManager) : KitFragment? {
        val fragments = fragmentManager.fragments
        if (fragments != null) {
            for (i in (fragments.size - 1) downTo  0) {
                val frg = fragments[i]
                if (frg != null && frg is KitFragment) {
                    return frg
                }
            }
        }
        return null
    }

}
