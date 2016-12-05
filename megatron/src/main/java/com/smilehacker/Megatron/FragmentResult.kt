package com.smilehacker.Megatron

import android.os.Bundle

/**
 * Created by kleist on 16/6/8.
 */
data class FragmentResult(val requestCode: Int, var resultCode: Int = IFragmentAction.RESULT_CANCELED, var data: Bundle? = null)
