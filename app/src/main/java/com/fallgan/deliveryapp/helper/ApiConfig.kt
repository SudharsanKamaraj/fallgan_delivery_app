package com.fallgan.deliveryapp.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.toolbox.StringRequest
import android.widget.Toast
import io.jsonwebtoken.SignatureAlgorithm
import javax.crypto.spec.SecretKeySpec
import io.jsonwebtoken.Jwts
import android.util.Patterns
import com.android.volley.*
import java.lang.Exception
import java.security.Key
import java.util.*
import com.fallgan.deliveryapp.R

object ApiConfig {
    @JvmStatic
    fun checkValidation(
        item: String,
        isMainValidation: Boolean,
        isMobileValidation: Boolean
    ): Boolean {
        return if (item.isEmpty()) true else if (isMainValidation && !Patterns.EMAIL_ADDRESS.matcher(
                item
            )
                .matches()
        ) true else isMobileValidation && (item.length < 10 || item.length > 12)
    }

    fun volleyErrorMessage(error: VolleyError?): String {
        var message = ""
        try {
            message = when (error) {
                is NetworkError -> {
                    "Cannot connect to Internet...Please check your connection!"
                }
                is ServerError -> {
                    "The server could not be found. Please try again after some time!!"
                }
                is AuthFailureError -> {
                    "Cannot connect to Internet...Please check your connection!"
                }
                is ParseError -> {
                    "Parsing error! Please try again after some time!!"
                }
                is TimeoutError -> {
                    "Connection TimeOut! Please check your internet connection."
                }
                else -> ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return message
    }

    @JvmStatic
    fun disableButton(activity: Activity?, button: Button) {
        button.background = ContextCompat.getDrawable(activity!!, R.drawable.disabled_btn)
        button.setTextColor(ContextCompat.getColor(activity, R.color.black))
        button.isEnabled = false
        button.postDelayed({
            button.background = ContextCompat.getDrawable(activity, R.drawable.bg_button)
            button.setTextColor(ContextCompat.getColor(activity, R.color.white))
            button.isEnabled = true
        }, 3000)
    }

    @JvmStatic
    fun disableSwipe(swipeRefreshLayout: SwipeRefreshLayout) {
        swipeRefreshLayout.isEnabled = false
        swipeRefreshLayout.postDelayed({ swipeRefreshLayout.isEnabled = true }, 3000)
    }

    @JvmStatic
    fun requestToVolley(
        callback: VolleyCallback,
        activity: Activity,
        url: String?,
        params: MutableMap<String, String?>,
        isProgress: Boolean
    ) {
        val progressDisplay = ProgressDisplay(activity)
        if (AppController.isConnected(activity)) {
            if (isProgress) progressDisplay.showProgress()
            val stringRequest: StringRequest = object : StringRequest(
                Method.POST, url, Response.Listener { response: String? ->
//                    System.out.println("================= " + url + " == " + response);
                    callback.onSuccess(true, response)
                    if (isProgress) progressDisplay.hideProgress()
                },
                Response.ErrorListener { error: VolleyError ->
                    if (isProgress) progressDisplay.hideProgress()
                    Toast.makeText(activity, error.toString(), Toast.LENGTH_LONG).show()
                    callback.onSuccess(false, "")
                    val message = volleyErrorMessage(error)
                    if (message != "") if (isProgress) progressDisplay.hideProgress()
                    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
                }) {
                override fun getHeaders(): MutableMap<String, String?> {
                    val params1: MutableMap<String, String?> = HashMap()
                    params1["Authorization"] =
                        "Bearer " + createJWT("eKart", "eKart Authentication")
                    return params1
                }

                override fun getParams(): MutableMap<String, String?> {
                    params[Constant.AccessKey] = Constant.AccessKeyVal
                    return params
                }
            }
            stringRequest.retryPolicy = DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
            AppController.instance!!.getRequestQueue().cache.clear()
            AppController.instance!!.addToRequestQueue<String>(stringRequest)
        }
    }

    fun createJWT(issuer: String?, subject: String?): String? {
        try {
            val signatureAlgorithm = SignatureAlgorithm.HS256
            val nowMillis = System.currentTimeMillis()
            val now = Date(nowMillis)
            val apiKeySecretBytes = Constant.JWT_KEY.toByteArray()
            val signingKey: Key = SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.jcaName)
            val builder = Jwts.builder()
                .setIssuedAt(now)
                .setSubject(subject)
                .setIssuer(issuer)
                .signWith(signatureAlgorithm, signingKey)
            return builder.compact()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}