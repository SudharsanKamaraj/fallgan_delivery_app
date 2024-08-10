package com.fallgan.deliveryapp.helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AlertDialog
import com.fallgan.deliveryapp.R
import com.fallgan.deliveryapp.activity.LoginActivity

class Session(private var _context: Context) {
    private var pref: SharedPreferences
    private var editor: SharedPreferences.Editor
    private var privateMode = 0
    fun createUserLoginSession(
        fcmId: String?,
        id: String?,
        name: String?,
        mobile: String?,
        password: String?,
        address: String?,
        bonus: String?,
        balance: String?,
        status: String?,
        created_at: String?
    ) {
        editor.putBoolean(Constant.IS_USER_LOGIN, true)
        editor.putString(Constant.FCM_ID, fcmId)
        editor.putString(Constant.ID, id)
        editor.putString(Constant.NAME, name)
        editor.putString(Constant.MOBILE, mobile)
        editor.putString(Constant.PASSWORD, password)
        editor.putString(Constant.ADDRESS, address)
        editor.putString(Constant.BONUS, bonus)
        editor.putString(Constant.BALANCE, balance)
        editor.putString(Constant.STATUS, status)
        editor.putString(Constant.CREATED_AT, created_at)
        editor.commit()
    }

    fun getData(id: String?): String? {
        return pref.getString(id, "")
    }

    fun setData(id: String?, `val`: String?) {
        editor.putString(id, `val`)
        editor.commit()
    }

    val isUserLoggedIn: Boolean
        get() = pref.getBoolean(Constant.IS_USER_LOGIN, false)

    fun logoutUser(activity: Activity) {
        editor.clear()
        editor.commit()
        val i = Intent(activity, LoginActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivity(i)
        activity.finish()
    }

    fun logoutUserConfirmation(activity: Activity) {
        val alertDialog = AlertDialog.Builder(_context)
        // Setting Dialog Message
        alertDialog.setTitle(R.string.logout)
        alertDialog.setMessage(R.string.logout_msg)
        alertDialog.setCancelable(false)
        alertDialog.setIcon(R.drawable.ic_logout_dialog)
        val alertDialog1 = alertDialog.create()

        // Setting OK Button
        alertDialog.setPositiveButton(R.string.yes) { _, _ ->
            editor.clear()
            editor.commit()
            val i = Intent(activity, LoginActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            activity.startActivity(i)
            activity.finish()
        }
        alertDialog.setNegativeButton(R.string.no) { _, _ -> alertDialog1.dismiss() }
        // Showing Alert Message
        alertDialog.show()
    }

    companion object {
        const val PREFER_NAME = "EKart_dboy"
    }

    init {
        pref = _context.getSharedPreferences(PREFER_NAME, privateMode)
        editor = pref.edit()
    }
}