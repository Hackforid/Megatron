package com.smilehacker.megatronsample

import android.os.Bundle
import com.smilehacker.megatron.HostActivity
import com.smilehacker.megatronsample.fragments.AFragment

class MainActivity : HostActivity() {

    override fun getContainerID(): Int {
        return R.id.container
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    private fun init() {
        startFragment(AFragment::class.java)
    }
}
