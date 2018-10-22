package com.smilehacker.megatron.model

import android.annotation.TargetApi
import android.os.Build
import android.transition.*
import android.view.View
import java.util.*

/**
 * Created by zhouquan on 16/12/25.
 */
@TargetApi(21)
data class SharedTransition(var shared: MutableList<Pair<View, String>> = LinkedList(),
                            var sharedElementEnterTransition: Transition? = DefaultTransition(),
                            var sharedElementReturnTransition: Transition? = DefaultTransition())

@TargetApi(21)
class DefaultTransition : TransitionSet() {
    init {
        if (Build.VERSION.SDK_INT >= 21) {
            addTransition(ChangeBounds())
            addTransition(ChangeTransform())
            addTransition(ChangeImageTransform())
        }
    }
}
