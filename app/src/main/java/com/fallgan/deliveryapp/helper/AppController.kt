package com.fallgan.deliveryapp.helper

import android.app.Activity
import android.app.Application
import android.content.SharedPreferences
import android.net.ConnectivityManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.fallgan.deliveryapp.helper.AppController
import com.fallgan.deliveryapp.R

class AppController : Application() {
    private var mRequestQueue: RequestQueue? = null
    private var sharedPref: SharedPreferences? = null
    override fun onCreate() {
        super.onCreate()
        instance = this
        sharedPref = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE)
    }

    fun getData(id: String?): String? {
        return sharedPref!!.getString(id, "")
    }

    fun getRequestQueue(): RequestQueue {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(applicationContext)
        }
        return mRequestQueue!!
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        req.tag = TAG
        getRequestQueue().add(req)
    }

    companion object {
        val TAG: String = AppController::class.java.simpleName

        @get:Synchronized
        var instance: AppController? = null
            private set

        @JvmStatic
        fun isConnected(activity: Activity): Boolean {
            var check = false
            val connectionManager =
                activity.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectionManager.activeNetworkInfo
            if (networkInfo != null && networkInfo.isConnected) {
                check = true
            }
            return check
        }

        @JvmStatic
        fun toTitleCase(str: String?): String? {
            if (str == null) {
                return null
            }
            var space = true
            val builder = StringBuilder(str)
            val len = builder.length
            for (i in 0 until len) {
                val c = builder[i]
                if (space) {
                    if (!Character.isWhitespace(c)) {
                        // Convert to title case and switch out of whitespace mode.
                        builder.setCharAt(i, Character.toTitleCase(c))
                        space = false
                    }
                } else if (Character.isWhitespace(c)) {
                    space = true
                } else {
                    builder.setCharAt(i, Character.toLowerCase(c))
                }
            }
            return builder.toString()
        }
    }
}