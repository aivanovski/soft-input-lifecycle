package com.github.ai.fprovider.demo.extensions

import android.content.Context
import android.view.inputmethod.InputMethodManager

fun Context.getInputMethodManager(): InputMethodManager {
    return getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
}