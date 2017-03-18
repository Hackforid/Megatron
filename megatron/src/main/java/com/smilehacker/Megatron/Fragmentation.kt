package com.smilehacker.Megatron

import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.smilehacker.Megatron.model.SharedTransition
import com.smilehacker.Megatron.util.DLog
import com.smilehacker.Megatron.util.createParcel
import com.smilehacker.Megatron.util.nullOr
import java.util.*

/**
 * Created by zhouquan on 16/6/9.
 */
class Fragmentation : Parcelable {

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        p0?.writeParcelable(mFragmentStack, 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField @Suppress("unused") val CREATOR = createParcel { Fragmentation(it) }
    }

    constructor(parcel: Parcel) : this() {
        mFragmentStack = parcel.readParcelable(FragmentStack.javaClass.classLoader)
    }

    constructor() {
        mFragmentStack = FragmentStack()
    }


    object START_TYPE {
        const val ADD = 1
        const val ADD_WITH_RESULT = 2
    }

    object LAUNCH_MODE {
        const val SINGLE_TOP = 1
        const val SINGLE_TASK = 2
        const val STANDARD = 3
    }

    private lateinit var mFragmentStack: FragmentStack

    private var mContainerID: Int = 0

    fun init(activity: HostActivity) {
        mContainerID = activity.getContainerID()
    }

    inline fun <reified T : Fragment> instanceFragment(bundle: Bundle? = null) : T{
        val frg = T::class.java.constructors[0].newInstance() as T
        frg.arguments = bundle
        return frg
    }

    private fun <T: Fragment> newFragment(clazz: Class<T>, bundle: Bundle? = null) : T{
        val frg = clazz.constructors[0].newInstance() as T
        frg.arguments = bundle
        return frg
    }

    fun <T : Fragment> start(fragmentManager: FragmentManager, to: Class<T>,
                             bundle: Bundle? = null,
                             launchMode: Int = LAUNCH_MODE.STANDARD,
                             startType: Int = START_TYPE.ADD,
                             requestCode: Int = 0,
                             sharedTransition: SharedTransition? = null) {

        val fragmentTag : String?
        val top: Fragment?
        if (getStackCount() > 0) {
            top = getTopFragment(fragmentManager)
        } else {
            top = null
        }

        when (launchMode) {
            LAUNCH_MODE.STANDARD -> {
                fragmentTag = mFragmentStack.getNewFragmentName(to)
                mFragmentStack.putStandard(fragmentTag)
                startStandard(fragmentManager, top, newFragment(to, bundle), fragmentTag, startType, requestCode, sharedTransition)
            }
            LAUNCH_MODE.SINGLE_TOP -> {
                if (top != null && top.javaClass == to) {
                    top as IKitFragmentAction
                    top.onNewBundle(bundle)
                } else {
                    fragmentTag = mFragmentStack.getNewFragmentName(to)
                    mFragmentStack.putStandard(fragmentTag)
                    startStandard(fragmentManager, top, newFragment(to, bundle), fragmentTag, startType, requestCode)
                }
            }
            LAUNCH_MODE.SINGLE_TASK -> {

                val instanceIndex = mFragmentStack.getSingleTaskInstancePos(to)
                if (instanceIndex == -1) {
                    fragmentTag = mFragmentStack.getNewFragmentName(to)
                    mFragmentStack.putStandard(fragmentTag)
                    startStandard(fragmentManager, top, newFragment(to, bundle), fragmentTag, startType, requestCode)
                } else {
                    startSingleTask(fragmentManager, to, bundle, sharedTransition)
                }
            }
        }
    }

    private fun <T : Fragment>  startSingleTask(fragmentManager: FragmentManager, to: Class<T>, bundle: Bundle?, sharedTransition: SharedTransition? = null) {
        popTo(fragmentManager, to, bundle, false, sharedTransition)
    }

    private fun startStandard(fragmentManager: FragmentManager, from: Fragment?, to: Fragment,
                              toTag: String,
                              startType: Int = START_TYPE.ADD,
                              requestCode: Int = 0,
                              sharedTransition: SharedTransition? = null) {
        if (to !is IKitFragmentAction) {
            throw IllegalArgumentException("to fragment should implement IKitFragmentAction")
        }
        when (startType) {
            START_TYPE.ADD_WITH_RESULT -> {
                to.fragmentResult = FragmentResult(requestCode)
            }
        }

        val ft = fragmentManager.beginTransaction()
        ft.setCustomAnimations(to.getAnimation()?.first.nullOr(0), if (from !is IKitFragmentAction) 0 else from?.getAnimation()?.second.nullOr(0))

        if (from != null) {
            ft.add(mContainerID, to, toTag)
            ft.hide(from)
        } else {
            ft.add(mContainerID, to, toTag)
        }
        if (Build.VERSION.SDK_INT >= 21 && sharedTransition != null) {
            to.setSharedTransition(sharedTransition.copy(sharedElement = null, transitionName = sharedTransition.sharedElement?.transitionName))
            if (sharedTransition.sharedElementEnterTransition != null) {
                to.sharedElementEnterTransition = sharedTransition.sharedElementEnterTransition
            }
            if (sharedTransition.sharedElementReturnTransition != null) {
                to.sharedElementReturnTransition= sharedTransition.sharedElementReturnTransition
            }
            DLog.i("sharedElement=${sharedTransition.sharedElement} name=${sharedTransition.transitionName}")
            ft.addSharedElement(sharedTransition.sharedElement, sharedTransition.transitionName)
        }
        ft.commitNow()
    }


    fun finish(fragmentManager: FragmentManager, fragment: Fragment) {
        popTo(fragmentManager, fragment.javaClass, null, true)
    }

    fun <T : Fragment> popTo(fragmentManager: FragmentManager, fragmentClass: Class<T>, bundle: Bundle? = null, includeSelf: Boolean = false, sharedTransition: SharedTransition? = null) {
        val fragments = getFragments(fragmentManager)
        if (fragments.isEmpty()) {
            return
        }

        val instanceIndex = mFragmentStack.getSingleTaskInstancePos(fragmentClass)
        val target : Fragment?
        val top = fragments.last()
        top as IKitFragmentAction

        if (instanceIndex == -1) {
            return
        }

        if (instanceIndex == fragments.lastIndex) {
            if (includeSelf) {
            } else {
                top.onNewBundle(bundle)
                return
            }
        }


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
        target as IKitFragmentAction

        val ft = fragmentManager.beginTransaction()
        ft.setCustomAnimations(target.getAnimation()?.first.nullOr(0), top.getAnimation()?.second.nullOr(0))
        for (i in (fragments.indexOf(target) + 1)..(fragments.lastIndex)) {
            if (i > 0) {
                handleFragmentResult(fragments[i], fragments[i-1])
            }
            ft.remove(fragments[i])
        }
        ft.show(target)

        val mySharedTransition = sharedTransition ?: top.getSharedTransition()
        if (Build.VERSION.SDK_INT >= 21 && mySharedTransition != null) {
            if (mySharedTransition.sharedElementEnterTransition != null) {
                target.sharedElementEnterTransition = mySharedTransition.sharedElementEnterTransition
            }
            ft.addSharedElement(mySharedTransition.sharedElement, mySharedTransition.transitionName)
        }

        ft.commitNow()
        mFragmentStack.popTo(target.tag, false)
    }

    fun popAll(fragmentManager: FragmentManager) {
        val fragments = getFragments(fragmentManager)
        if (fragments.isEmpty()) {
            return
        }
        val ft = fragmentManager.beginTransaction()
        val top = fragments.last()
        top as IKitFragmentAction
        if (top.getAnimation() != null) {
            ft.setCustomAnimations(top.getAnimation()!!.first, top.getAnimation()!!.second)
        }
        for (fragment in fragments) {
            ft.remove(fragment)
        }
        ft.commitNow()
        mFragmentStack.popAll()
    }

    private fun handleFragmentResult(frg: Fragment, preFrg: Fragment) {
        if (frg is IKitFragmentAction && preFrg is IKitFragmentAction) {
            if (frg.fragmentResult != null) {
                preFrg.onFragmentResult(frg.fragmentResult!!.requestCode,
                        frg.fragmentResult!!.resultCode, frg.fragmentResult!!.data)
            }
        }

    }

    fun getFragments(fragmentManager: FragmentManager): MutableList<Fragment> {
        val frgTags = mFragmentStack.getFragments()
        val frgs = ArrayList<Fragment>(frgTags.size)
        frgTags.forEach {
            val frg = fragmentManager.findFragmentByTag(it)
            frg?.let { frgs.add(it) }
        }
        return frgs
    }

    fun getStackCount(): Int {
        return mFragmentStack.getStackCount()
    }

    fun getTopFragment(fragmentManager: FragmentManager): Fragment? {
        val frgTags = mFragmentStack.getFragments()
        if (frgTags.size > 0) {
            return fragmentManager.findFragmentByTag(frgTags.last())
        } else {
            return null
        }
    }
}