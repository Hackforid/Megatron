package com.smilehacker.megatron

import android.support.v4.app.Fragment
import java.util.*

/**
 * Created by zhouquan on 16/6/9.
 */
class FragmentStack {

    companion object {
        private const val TAG_SPLITER = "$$"
    }

    private var mFragmentStack : MutableList<String> = ArrayList()

    fun getFragments() = mFragmentStack

    fun getStackCount() = mFragmentStack.size

    fun putStandard(fragmentTag : String) {
        mFragmentStack.add(fragmentTag)
    }

    fun pop() {
        mFragmentStack.removeAt(mFragmentStack.lastIndex)
    }

    fun popTo(fragmentTag: String, includeSelf: Boolean = false) {
        if (fragmentTag !in mFragmentStack) {
            return
        }
        if (includeSelf) {
            if (mFragmentStack.indexOf(fragmentTag) > 0) {
                mFragmentStack = mFragmentStack.subList(0, mFragmentStack.indexOf(fragmentTag))
            } else {
                mFragmentStack.clear()
            }
        } else {
            mFragmentStack = mFragmentStack.subList(0, mFragmentStack.indexOf(fragmentTag)+1)
        }
    }

    fun popAll() {
        mFragmentStack.clear()
    }

    fun remove(fragment: String) {
        mFragmentStack.remove(fragment)
    }

    fun getTopFragment() : String? {
        if (mFragmentStack.isEmpty()) {
            return null
        } else {
            return mFragmentStack.last()
        }
    }

    fun <T : Fragment> getNewFragmentName(clazz: Class<T>) : String {
        val className =  clazz.name
        var index = 0
        for (tag in mFragmentStack.asReversed()) {
            val keys = tag.split("$$")
            if (keys.size != 2) {
                continue
            }
            if (keys[0] == className) {
                index = keys[1].toInt()
                break
            }
        }

        return "${className}$$${index+1}"
    }

    fun getNewFragmentName(fragment : Fragment) : String {
        return getNewFragmentName(fragment.javaClass)
    }

    fun <T : Fragment> getSingleTaskInstancePos(fragmentClass: Class<T>) : Int {
        val className = fragmentClass.name
        val reFrgs = mFragmentStack.reversed()
        for (i in reFrgs.indices) {
            val tag = reFrgs[i]
            if (tag.startsWith(className)) {
                return mFragmentStack.size - i - 1
            }
        }
        return -1
    }

    fun getSingleTaskInstancePos(fragment: Fragment) : Int {
        return getSingleTaskInstancePos(fragment.javaClass)
    }

}
