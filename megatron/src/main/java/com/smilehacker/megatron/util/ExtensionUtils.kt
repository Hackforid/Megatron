package com.smilehacker.megatron.util

/**
 * Created by kleist on 16/8/3.
 */

inline fun <T> Array<out T>?.isNullOrEmpty() : Boolean = this == null || this.isEmpty()

inline fun <T> T?.nullOr(value : T) : T = if (this != null) this else value

