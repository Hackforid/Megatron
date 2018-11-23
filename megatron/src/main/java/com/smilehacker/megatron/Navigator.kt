package com.smilehacker.megatron

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.os.Bundle
import android.support.v4.app.FragmentManager
import java.lang.ref.WeakReference

/**
 * Created by quan.zhou on 2018/11/23.
 */
class Navigator: ViewModel() {

    private var mFragmentStack: FragmentStack = FragmentStack()
    private var mContainerID: Int = 0
    private var mFragmentManager: WeakReference<FragmentManager>? = null

    private val mStackCleared: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>().also { it.value = mFragmentStack.getFragments().isEmpty() } }

    fun init(activity: HostActivity) {
        mContainerID = activity.getContainerID()
        mFragmentManager = WeakReference(activity.supportFragmentManager)
    }

    fun getStackCleared() : LiveData<Boolean> = mStackCleared


    @Suppress("UNCHECKED_CAST")
    private fun <T : KitFragment> newFragment(clazz: Class<T>, bundle: Bundle? = null): T {
        val frg = clazz.constructors[0].newInstance() as T
        frg.arguments = bundle
        return frg
    }

    private fun getFragmentManager() : FragmentManager {
        return mFragmentManager?.get() ?: throw IllegalStateException("FragmentManager should be init")
    }

    fun push(fragment: KitFragment) {

        val fragmentManager = getFragmentManager()
        val tag = fragment.tag
        var previewsFragment: KitFragment? = null



        // 判断栈里是否已经有了
        if (tag != null) {
            val existFragment = fragmentManager.findFragmentByTag(tag) as? KitFragment
            if (existFragment != null) {
                if ( existFragment != fragment) {
                    throw IllegalStateException("fragment with tag $tag already exist")
                }
                if (mFragmentStack.getFragments().size > 0 && mFragmentStack.getFragments().contains(tag)) {
                    // 已经排在第一个
                    if (mFragmentStack.getFragments().last() == tag) {
                        // todo notify onNewIntent?
                        return
                    }

                    // 拿到最上面
                    previewsFragment = fragmentManager.findFragmentByTag(mFragmentStack.getTopFragment()!!) as? KitFragment
                    mFragmentStack.getFragments().remove(tag)
                    mFragmentStack.getFragments().add(tag)
                }
            }
        }

        val ft = fragmentManager.beginTransaction()
        if (previewsFragment != null) {
            ft.hide(previewsFragment)
        }
        if (tag == null) {
            val newTag = mFragmentStack.getNewFragmentName(fragment)
            mFragmentStack.putStandard(newTag)
            ft.add(mContainerID, fragment, newTag)
        } else {
            ft.show(fragment)
        }

        ft.commitNowAllowingStateLoss()
    }

    fun pop(fragment: KitFragment, data: FragmentResult? = null) {
        val fragmentManager = getFragmentManager()
        val tag = fragment.tag ?: throw IllegalStateException("Fragment $fragment not added")
        var previewsFragment: KitFragment? = null

        val ft = fragmentManager.beginTransaction()
        val index = mFragmentStack.getFragments().indexOf(tag)
        if (index < 0) {
            throw IllegalStateException("Fragment $fragment not added")
        }
        if (mFragmentStack.getTopFragment() == tag) { // 如果在顶上
            // 如果栈里只有这个fragment
            ft.remove(fragment)
            if (mFragmentStack.getFragments().size == 1) {
                // TODO notify activity finish?
                mStackCleared.value = true
            } else {
                previewsFragment = fragmentManager.findFragmentByTag(mFragmentStack.getFragments()[index - 1]) as? KitFragment
                // todo result
                previewsFragment?.let {
                    it.setFromFragmentTag(tag)
                    it.fragmentResult = data
                    ft.show(it)
                }
            }
        } else { // 如果不在顶上
            ft.remove(fragment)
        }
        mFragmentStack.getFragments().remove(tag)
        ft.commitNowAllowingStateLoss()
    }

    fun popTo(fragment: KitFragment, data: FragmentResult? = null) {
        val fragmentManager = getFragmentManager()
        val tag = fragment.tag ?: throw IllegalStateException("Fragment $fragment not added")

        val index = mFragmentStack.getFragments().indexOf(tag)
        if (index < 0) {
            throw IllegalStateException("Fragment $fragment not added")
        }

        // 如果在顶部了
        if (index == mFragmentStack.getFragments().size - 1) {
            // todo notify onNewIntent?
            return
        }

        val popList = mFragmentStack.getFragments().subList(index + 1, mFragmentStack.getFragments().size)

        val ft = fragmentManager.beginTransaction()
        popList.forEach {
            fragmentManager.findFragmentByTag(it)?.let { ft.remove(it) }
        }
        ft.show(fragment)
        mFragmentStack.getFragments().removeAll(popList)
        fragment.fragmentResult = data
        ft.commitNowAllowingStateLoss()
    }

    override fun onCleared() {
        super.onCleared()
        mFragmentManager?.clear()
    }

    fun getFragments(): List<KitFragment> {
        val fm = getFragmentManager()
        return mFragmentStack.getFragments().mapNotNull { fm.findFragmentByTag(it) as? KitFragment }
    }

    fun getStackCount(): Int {
        return mFragmentStack.getStackCount()
    }

    fun getTopFragment(): KitFragment? {
        val frgTags = mFragmentStack.getFragments()
        return if (frgTags.size > 0) {
            getFragmentManager().findFragmentByTag(frgTags.last()) as? KitFragment
        } else {
            null
        }
    }

}