package com.smilehacker.megatronsample.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.smilehacker.megatron.KitFragment
import org.jetbrains.anko.backgroundColorResource
import org.jetbrains.anko.button
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.support.v4.UI

/**
 * Created by quan.zhou on 2018/10/24.
 */
class BFragment: KitFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return UI {
            linearLayout {
                backgroundColorResource = android.R.color.holo_green_light

                button("to C") {
                    setOnClickListener {
                        push(CFragment())
                    }
                }
            }
        }.view
    }

}