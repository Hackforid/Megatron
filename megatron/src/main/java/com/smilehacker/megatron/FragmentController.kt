package com.smilehacker.megatron

import android.arch.lifecycle.ViewModel
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.smilehacker.megatron.util.nullOr
import java.lang.ref.WeakReference
import java.util.*

/**
 * Created by zhouquan on 16/6/9.
 */
class FragmentController : ViewModel() {

    object LAUNCH_MODE {
        const val SINGLE_TOP = 1
        const val CLEAR_TOP = 2
        const val STANDARD = 3
    }

    private var mFragmentStack: FragmentStack = FragmentStack()
    private var mContainerID: Int = 0
    private var mFragmentManager: WeakReference<FragmentManager>? = null

    companion object {
    }

    fun init(activity: HostActivity) {
        mContainerID = activity.getContainerID()
        mFragmentManager = WeakReference(activity.supportFragmentManager)
    }

    inline fun <reified T : Fragment> instanceFragment(bundle: Bundle? = null): T {
        val frg = T::class.java.constructors[0].newInstance() as T
        frg.arguments = bundle
        return frg
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : KitFragment> newFragment(clazz: Class<T>, bundle: Bundle? = null): T {
        val frg = clazz.constructors[0].newInstance() as T
        frg.arguments = bundle
        return frg
    }

    fun <T : KitFragment> start(from: KitFragment?, to: Class<T>,
                             bundle: Bundle = Bundle(),
                             launchMode: Int = LAUNCH_MODE.STANDARD,
                             requestCode: Int = -1) {

        val fragmentManager = from?.fragmentManager ?: mFragmentManager?.get() ?: return

        when (launchMode) {
            LAUNCH_MODE.STANDARD -> {
                startStandard(fragmentManager, from, to, bundle, requestCode)
            }
            LAUNCH_MODE.SINGLE_TOP -> {
                val top = getTopFragment(fragmentManager)
                if (top != null && top.javaClass == to) {
//                    top.onNewBundle(bundle)
                } else {
                    startStandard(fragmentManager, from, to, bundle, requestCode)
                }
            }
            LAUNCH_MODE.CLEAR_TOP -> {

                val instanceIndex = mFragmentStack.getSingleTaskInstancePos(to)
                if (instanceIndex == -1) {
                    startStandard(fragmentManager, from, to, bundle, requestCode)
                } else {
                    startClearTop(fragmentManager, to, bundle)
                }
            }
        }
    }

    private fun <T : KitFragment> startClearTop(fragmentManager: FragmentManager, to: Class<T>, bundle: Bundle?) {
        popTo(fragmentManager, to, bundle, false)
    }


    private fun <T: KitFragment> startStandard(fragmentManager: FragmentManager, from: KitFragment?, toFrgClazz: Class<T>, bundle: Bundle, requestCode: Int) {
        val toTag = mFragmentStack.getNewFragmentName(toFrgClazz)
        mFragmentStack.putStandard(toTag)
        val to= newFragment(toFrgClazz, bundle)
//        to.setRequestCode(requestCode)
//        to.setFromFragmentTag(from?.tag)

        val ft = fragmentManager.beginTransaction()

        // 使用栈顶fragment做动画
        val top = getTopFragment(fragmentManager)
        if (top != null) {
            // set transition  animation
            val transitionAnims = top.transitionAnimation
            ft.setCustomAnimations(transitionAnims?.first.nullOr(0), transitionAnims?.second.nullOr(0))
            ft.hide(top)
        }

        ft.add(mContainerID, to, toTag)

        // set share elements
        if (VERSION.SDK_INT >= 21 && from != null) {
            from.getShareElementPairs()?.forEach {
                ft.addSharedElement(it.first, it.second)
            }
        }

        ft.commitNowAllowingStateLoss()
    }


    fun finish(fragmentManager: FragmentManager, fragment: KitFragment) {
        val top = getTopFragment(fragmentManager)
        if (top == fragment) {
            popTo(fragmentManager, fragment, null, true)
        } else {
            val ft = fragmentManager.beginTransaction()
            ft.remove(fragment)

            handleFragmentResult(fragmentManager, fragment)
            ft.commitNowAllowingStateLoss()
        }
    }

    fun popTo(fragmentManager: FragmentManager, to: KitFragment, bundle: Bundle? = null, includeSelf: Boolean = false) {
        val fragments = getFragments(fragmentManager)
        if (fragments.isEmpty()) {
            return
        }

        val top = fragments.last()

        if (to == top && !includeSelf) {
//            to.onNewBundle(bundle)
            return
        }

        val target: KitFragment // pop后被显示出来的fragment
        if (includeSelf) {
            if (fragments.indexOf(to) == 0) {
                popAll(fragmentManager)
                return
            } else {
                target = fragments[fragments.indexOf(to) - 1]
            }
        } else {
            target = to
        }

        val ft = fragmentManager.beginTransaction()

        val transitionAnims = top.transitionAnimation
        ft.setCustomAnimations(transitionAnims?.first.nullOr(0), transitionAnims?.second.nullOr(0))

        for (i in (fragments.lastIndex) downTo (fragments.indexOf(target) + 1)) {
            handleFragmentResult(fragmentManager, fragments[i])
            ft.remove(fragments[i])
        }
        ft.show(target)

        if (Build.VERSION.SDK_INT >= 21) {
            top.getShareElementPairs()?.forEach {
                ft.addSharedElement(it.first, it.second)
            }
        }

        mFragmentStack.popTo(to.tag!!, includeSelf)
        ft.commitNowAllowingStateLoss()
    }

    fun <T : KitFragment> popTo(fragmentManager: FragmentManager, fragmentClass: Class<T>, bundle: Bundle? = null, includeSelf: Boolean = false) {
        val fragments = getFragments(fragmentManager)
        if (fragments.isEmpty()) {
            return
        }

        val instanceIndex = mFragmentStack.getSingleTaskInstancePos(fragmentClass)
        if (instanceIndex == -1) {
            return
        }

        val top = fragments.last()

        if (instanceIndex == fragments.lastIndex && !includeSelf) {
//            top.onNewBundle(bundle)
            return
        }

        val target: Fragment?

        if (!includeSelf) {
            target = fragments[instanceIndex]
        } else {
            if (instanceIndex == 0) {
                popAll(fragmentManager)
                return
            } else {
                target = fragments[instanceIndex - 1]
            }
        }

        val ft = fragmentManager.beginTransaction()

        val transitionAnims = top.transitionAnimation
        ft.setCustomAnimations(transitionAnims?.first.nullOr(0), transitionAnims?.second.nullOr(0))

        for (i in (fragments.indexOf(target) + 1)..(fragments.lastIndex)) {
            if (i > 0) {
                handleFragmentResult(fragmentManager, fragments[i])
            }
            ft.remove(fragments[i])
        }
        ft.show(target)

        if (Build.VERSION.SDK_INT >= 21) {
            top.getShareElementPairs()?.forEach {
                ft.addSharedElement(it.first, it.second)
            }
        }

        ft.commitNowAllowingStateLoss()
        mFragmentStack.popTo(target.tag!!, false)
    }

    fun popAll(fragmentManager: FragmentManager) {
        val fragments = getFragments(fragmentManager)
        if (fragments.isEmpty()) {
            return
        }
        val ft = fragmentManager.beginTransaction()
        val top = fragments.last()

        val transitionAnims = top.transitionAnimation
        ft.setCustomAnimations(transitionAnims?.first.nullOr(0), transitionAnims?.second.nullOr(0))

        for (fragment in fragments) {
            ft.remove(fragment)
        }
        ft.commitNowAllowingStateLoss()
        mFragmentStack.popAll()
    }

    private fun handleFragmentResult(fragmentManager: FragmentManager, fragment: KitFragment) {
//        val requestCode = fragment.getRequestCode()
//        val sourceFragmentTag = fragment.getSourceFragmentTag()
//        if (requestCode > 0 && sourceFragmentTag != null && fragment.fragmentResult != null) {
//            val source = fragmentManager.findFragmentByTag(sourceFragmentTag)
//            if (source != null && source is KitFragment) {
////                source.onFragmentResult(requestCode, fragment.fragmentResult!!.resultCode, fragment.fragmentResult!!.data)
//            }
//        }
    }

    fun getFragments(fragmentManager: FragmentManager): List<KitFragment> {
        val frgTags = mFragmentStack.getFragments()
        val frgs = ArrayList<KitFragment>(frgTags.size)
        frgTags.forEach {
            val frg = fragmentManager.findFragmentByTag(it)
            if (frg is KitFragment) {
                frgs.add(frg)
            }
        }
        return frgs
    }

    fun getStackCount(): Int {
        return mFragmentStack.getStackCount()
    }

    fun getTopFragment(fragmentManager: FragmentManager): KitFragment? {
        val frgTags = mFragmentStack.getFragments()
        return if (frgTags.size > 0) {
            fragmentManager.findFragmentByTag(frgTags.last()) as? KitFragment
        } else {
            null
        }
    }

}