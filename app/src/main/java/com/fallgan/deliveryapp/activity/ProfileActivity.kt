package com.fallgan.deliveryapp.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.google.android.material.snackbar.Snackbar
import org.json.JSONException
import org.json.JSONObject
import com.fallgan.deliveryapp.R
import com.fallgan.deliveryapp.helper.ApiConfig.checkValidation
import com.fallgan.deliveryapp.helper.ApiConfig.disableButton
import com.fallgan.deliveryapp.helper.ApiConfig.disableSwipe
import com.fallgan.deliveryapp.helper.ApiConfig.requestToVolley
import com.fallgan.deliveryapp.helper.AppController.Companion.isConnected
import com.fallgan.deliveryapp.helper.Constant
import com.fallgan.deliveryapp.helper.Session
import com.fallgan.deliveryapp.helper.VolleyCallback

class ProfileActivity : AppCompatActivity() {
    private lateinit var edtname: EditText
    private lateinit var edtaddress: EditText
    private lateinit var tvMobile: TextView
    private lateinit var imglogout: ImageView
    private lateinit var btnsubmit: Button
    lateinit var session: Session
    lateinit var toolbar: Toolbar
    lateinit var activity: Activity
    private lateinit var swipeRefresh: SwipeRefreshLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        toolbar = findViewById(R.id.toolbar)
        edtname = findViewById(R.id.edtname)
        tvMobile = findViewById(R.id.tvMobile)
        edtaddress = findViewById(R.id.edtaddress)
        imglogout = findViewById(R.id.imglogout)
        btnsubmit = findViewById(R.id.btnsubmit)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        activity = this@ProfileActivity
        session = Session(activity)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = resources.getString(R.string.profile)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        edtname.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_profile, 0, 0, 0)
        tvMobile.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        edtaddress.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_edt_home, 0, 0, 0)
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        swipeRefresh.setOnRefreshListener(OnRefreshListener {
            if (isConnected(activity)) {
                edtname.setText(session.getData(Constant.NAME))
                tvMobile.text = session.getData(Constant.MOBILE)
                edtaddress.setText(session.getData(Constant.ADDRESS))
                disableSwipe(swipeRefresh)
            } else {
                setSnackBar(
                    activity,
                    getString(R.string.no_internet_message),
                    getString(R.string.retry),
                    Color.RED
                )
            }
            swipeRefresh.isRefreshing = false
        })
        edtname.setText(session.getData(Constant.NAME))
        tvMobile.text = session.getData(Constant.MOBILE)
        edtaddress.setText(session.getData(Constant.ADDRESS))
    }

    private fun updateUserData() {
        val params: MutableMap<String, String?> = HashMap()
        params[Constant.DELIVERY_BOY_ID] = session.getData(Constant.ID)
        params[Constant.NAME] = edtname.text.toString().trim { it <= ' ' }
        params[Constant.ADDRESS] = edtaddress.text.toString().trim { it <= ' ' }
        params[Constant.UPDATE_DELIVERY_BOY_PROFILE] = Constant.GetVal
        requestToVolley(object : VolleyCallback {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @SuppressLint("SetTextI18n")
            override fun onSuccess(result: Boolean, response: String?) {
                if (result) {
                    try {
                        val jsonObject = JSONObject(response)
                        if (!jsonObject.getBoolean(Constant.ERROR)) {
                            setSnackBar(
                                activity,
                                jsonObject.getString(Constant.MESSAGE),
                                getString(R.string.ok),
                                Color.GREEN
                            )
                            getDeliveryBoyData(activity)
                        } else {
                            setSnackBar(
                                activity,
                                jsonObject.getString(Constant.MESSAGE),
                                getString(R.string.ok),
                                Color.RED
                            )
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }
        }, activity, Constant.MAIN_URL, params, false)
    }

    fun OnClick(view: View) {
        if (isConnected(activity)) {
            val id = view.id
            if (id == R.id.imglogout) {
                session.logoutUserConfirmation(activity)
            } else if (id == R.id.tvChangePassword) {
                startActivity(
                    Intent(activity, LoginActivity::class.java).putExtra(
                        Constant.FROM,
                        "lytUpdatePassword"
                    )
                )
            } else if (id == R.id.btnsubmit) {
                val address: String = edtaddress.text.toString()
                val name: String = edtname.text.toString()
                if (checkValidation(name, false, false)) {
                    edtname.error = getString(R.string.name_required)
                } else if (checkValidation(address, false, false)) {
                    edtaddress.error = getString(R.string.address_required)
                } else {
                    updateUserData()
                    disableButton(activity, btnsubmit)
                }
            }
        } else {
            setSnackBar(
                activity,
                getString(R.string.no_internet_message),
                getString(R.string.retry),
                Color.RED
            )
        }
    }

    fun getDeliveryBoyData(activity: Activity?) {
        if (isConnected(activity!!)) {
            val params: MutableMap<String, String?> = HashMap()
            params[Constant.DELIVERY_BOY_ID] = session.getData(Constant.ID)
            params[Constant.GET_DELIVERY_BOY_BY_ID] = Constant.GetVal
            requestToVolley(object : VolleyCallback {
                override fun onSuccess(result: Boolean, response: String?) {
                    //  System.out.println("============" + response);
                    if (result) {
                        try {
                            val jsonObject = JSONObject(response)
                            if (!jsonObject.getBoolean(Constant.ERROR)) {
                                StartMainActivity(
                                    activity,
                                    jsonObject.getJSONArray(Constant.DATA).getJSONObject(0)
                                )
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }
            }, activity, Constant.MAIN_URL, params, true)
        } else {
            setSnackBar(
                activity,
                getString(R.string.no_internet_message),
                getString(R.string.retry),
                Color.RED
            )
        }
    }

    @SuppressLint("SetTextI18n")
    fun StartMainActivity(activity: Activity?, jsonObject: JSONObject) {
        if (isConnected(activity!!)) {
            try {
                Session(activity).createUserLoginSession(
                    jsonObject.getString(Constant.FCM_ID),
                    jsonObject.getString(Constant.ID),
                    jsonObject.getString(Constant.NAME),
                    jsonObject.getString(Constant.MOBILE),
                    jsonObject.getString(Constant.PASSWORD),
                    jsonObject.getString(Constant.ADDRESS),
                    jsonObject.getString(Constant.BONUS),
                    jsonObject.getString(Constant.BALANCE),
                    jsonObject.getString(Constant.STATUS),
                    jsonObject.getString(Constant.CREATED_AT)
                )
                edtname.setText(session.getData(Constant.NAME))
                DrawerActivity.tvName.text = session.getData(Constant.NAME)
                tvMobile.text = session.getData(Constant.MOBILE)
                edtaddress.setText(session.getData(Constant.ADDRESS))
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        } else {
            setSnackBar(
                activity,
                getString(R.string.no_internet_message),
                getString(R.string.retry),
                Color.RED
            )
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
    }

    fun setSnackBar(activity: Activity?, message: String?, action: String?, color: Int) {
        val snackbar =
            Snackbar.make(findViewById(android.R.id.content), message!!, Snackbar.LENGTH_INDEFINITE)
        snackbar.setAction(action) { snackbar.dismiss() }
        snackbar.setActionTextColor(color)
        val snackbarView = snackbar.view
        val textView =
            snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.maxLines = 5
        snackbar.show()
    }
}