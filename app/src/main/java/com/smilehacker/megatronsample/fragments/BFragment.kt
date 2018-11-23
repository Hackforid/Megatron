package com.smilehacker.megatronsample.fragments

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.smilehacker.megatron.KitFragment
import org.jetbrains.anko.backgroundColorResource
import org.jetbrains.anko.button
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.support.v4.UI
import com.smilehacker.megatronsample.R

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
                        val data = push(CFragment())
                        data.observe(this@BFragment, Observer {
                            Log.i("BFragment", "getResult from C ${it?.getString("result")}")
                        })
                    }
                }
            }
        }.view
    }

    init {
        transitionAnimation = R.anim.frg_slide_in_from_bottom to R.anim.frg_slide_out_from_bottom
    }
}