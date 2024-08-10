package com.fallgan.deliveryapp.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.fallgan.deliveryapp.R
import com.fallgan.deliveryapp.helper.Constant
import com.fallgan.deliveryapp.helper.Session
import java.util.*

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var activity: Activity
        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        setContentView(R.layout.activity_splash)

        activity = this@SplashActivity

        val resources = activity.resources
        val dm = activity.resources.displayMetrics
        val configuration = resources.configuration
        configuration.setLocale(Locale(Constant.LANGUAGE.lowercase(Locale.getDefault())))
        resources.updateConfiguration(configuration, dm)

        val session = Session(applicationContext)

        Handler().postDelayed({
            if (session.isUserLoggedIn) {
                session.setData(Constant.OFFSET, "" + 0)
                startActivity(
                    Intent(
                        this@SplashActivity,
                        MainActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            } else {
                startActivity(
                    Intent(
                        this@SplashActivity,
                        LoginActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
        }, 2000.toLong())
    }
}