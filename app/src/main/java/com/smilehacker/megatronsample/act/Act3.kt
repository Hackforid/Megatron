package com.smilehacker.megatronsample.act

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import org.jetbrains.anko.button
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout

/**
 * Created by quan.zhou on 2018/10/24.
 */
class Act3 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verticalLayout {
            textView("Act 3")
            button("clear to 1") {
                setOnClickListener {
                    val intent = Intent(this@Act3, Act1::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivityForResult(intent, 1002)
                }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.i("Acr3", "onDestory")
    }
}