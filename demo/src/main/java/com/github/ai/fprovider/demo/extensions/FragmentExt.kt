package com.github.ai.fprovider.demo.extensions

import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun Fragment.setupActionBar(action: ActionBar.() -> Unit) {
    val activity = (this.activity as? AppCompatActivity) ?: return

    activity.supportActionBar?.run {
        action.invoke(this)
    }
}