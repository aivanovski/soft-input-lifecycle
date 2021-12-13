package com.github.ai.lifecycle

import android.view.View
import android.view.ViewTreeObserver.OnWindowFocusChangeListener
import androidx.lifecycle.LiveData

class WindowFocusLiveData(private val view: View) : LiveData<Boolean>() {

    private val windowFocusListener =
        OnWindowFocusChangeListener { hasFocus -> postValue(hasFocus) }

    init {
        value = view.hasWindowFocus()
    }

    override fun onActive() {
        super.onActive()
        view.viewTreeObserver.addOnWindowFocusChangeListener(windowFocusListener)
    }

    override fun onInactive() {
        super.onInactive()
        view.viewTreeObserver.removeOnWindowFocusChangeListener(windowFocusListener)
    }

    companion object {
        fun View.asWindowFocusLiveData(): LiveData<Boolean> =
            WindowFocusLiveData(this)
    }
}