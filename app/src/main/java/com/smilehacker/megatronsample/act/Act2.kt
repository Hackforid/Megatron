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
class Act2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verticalLayout {
            textView("Act 2")
            button("start 3") {
                setOnClickListener {
                    val intent = Intent(this@Act2, Act3::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivityForResult(intent, 10022)
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i("Act2", "code=$requestCode resultCode=$resultCode")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("Acr2", "onDestory")
    }
}