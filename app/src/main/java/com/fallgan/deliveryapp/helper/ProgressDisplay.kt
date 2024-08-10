package com.fallgan.deliveryapp.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout

class ProgressDisplay(context: Context) {
    fun showProgress() {
        if (mProgressBar.visibility != View.VISIBLE) {
            mProgressBar.visibility = View.VISIBLE
        }
    }

    fun hideProgress() {
        mProgressBar.visibility = View.GONE
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var mProgressBar: ProgressBar
    }

    init {
        val layout = (context as Activity).findViewById<View>(android.R.id.content)
            .rootView as ViewGroup
        mProgressBar = ProgressBar(context, null, android.R.attr.progressBarStyle)
        mProgressBar.isIndeterminate = true
        val params = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )
        val rl = RelativeLayout(context)
        rl.gravity = Gravity.CENTER
        rl.addView(mProgressBar)
        layout.addView(rl, params)
        hideProgress()
    }
}