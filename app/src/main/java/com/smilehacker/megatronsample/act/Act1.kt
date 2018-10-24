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
class Act1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("Act1", "onCreate")
        verticalLayout {
            textView("Act 1")
            button("start 2") {
                setOnClickListener {
                    val intent = Intent(this@Act1, Act2::class.java)
                    startActivityForResult(intent, 1002)
                }
            }

            button("delay to Act 3") {
                setOnClickListener {
                    window.decorView.postDelayed({
                        val intent = Intent(this@Act1, Act3::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        startActivityForResult(intent, 1003)
                    }, 5000)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i("Act1", "code=$requestCode resultCode=$resultCode")
    }
}