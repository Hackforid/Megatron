package android.os

import android.view.View

/**
 * Created by kleist on 2017/3/22.
 */
class KitBundle : BaseBundle() {
    companion object {
        fun fromBundle(bundle: Bundle) : KitBundle {
            val kitBundle = KitBundle()
            kitBundle.bundle = bundle
            return kitBundle
        }
    }

    var bundle : Bundle? = null
    val sharedElements : MutableMap<String, View> by lazy { HashMap<String, View>() }

    fun addSceneTransitionAnimation(viewStart: View, transitionName: String) {
        sharedElements[transitionName] = viewStart
    }

}

fun BaseBundle.data() : Bundle? {
    if (this is KitBundle) {
        return this.bundle
    } else if (this is Bundle) {
        return this
    } else {
        return null
    }
}
