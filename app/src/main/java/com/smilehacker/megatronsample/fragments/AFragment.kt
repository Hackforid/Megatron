package com.smilehacker.megatronsample.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.smilehacker.megatron.KitFragment
import com.smilehacker.megatronsample.R
import com.smilehacker.megatronsample.act.Act1
import org.jetbrains.anko.backgroundColorResource
import org.jetbrains.anko.button
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.startActivity

/**
 * Created by quan.zhou on 2018/10/24.
 */
class AFragment: KitFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return UI {
            linearLayout {
                backgroundColorResource = android.R.color.holo_red_light

                orientation = LinearLayout.VERTICAL

                button("to B") {
                   setOnClickListener {
                       push(BFragment())
                   }
                }

                button("to B for result") {
                    setOnClickListener {
                        push(BFragment())

                    }
                }

                button("start 2") {
                    setOnClickListener {
                        startActivity<Act1>()
                    }
                }
            }
        }.view
    }

    init {
        transitionAnimation = R.anim.alpha_in to R.anim.alpha_out
    }
}