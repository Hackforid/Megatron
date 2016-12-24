package com.smilehacker.Megatron.model

import android.annotation.TargetApi
import android.os.Build
import android.transition.*
import android.view.View

/**
 * Created by zhouquan on 16/12/25.
 */
@TargetApi(21)
data class SharedTransition(var sharedElement: View?,
                            var transitionName: String?,
                            var sharedElementEnterTransition: Transition? = DefaultTransition(),
                            var sharedElementReturnTransition: Transition? = DefaultTransition())

class DefaultTransition : TransitionSet() {
    init {
        if (Build.VERSION.SDK_INT >= 21) {
            addTransition(ChangeBounds())
            addTransition(ChangeTransform())
            addTransition(ChangeImageTransform())
        }
    }
}
