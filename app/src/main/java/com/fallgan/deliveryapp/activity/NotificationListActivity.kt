package com.fallgan.deliveryapp.activity

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import com.fallgan.deliveryapp.R
import com.fallgan.deliveryapp.adapter.NotificationAdapter
import com.fallgan.deliveryapp.helper.ApiConfig.disableSwipe
import com.fallgan.deliveryapp.helper.ApiConfig.requestToVolley
import com.fallgan.deliveryapp.helper.AppController.Companion.isConnected
import com.fallgan.deliveryapp.helper.Constant
import com.fallgan.deliveryapp.helper.Session
import com.fallgan.deliveryapp.helper.VolleyCallback
import com.fallgan.deliveryapp.model.Notification

class NotificationListActivity : AppCompatActivity() {
    lateinit var activity: Activity
    lateinit var recyclerView: RecyclerView
    lateinit var notifications: ArrayList<Notification?>
    lateinit var toolbar: Toolbar
    private lateinit var swipeLayout: SwipeRefreshLayout
    lateinit var scrollView: NestedScrollView
    lateinit var notificationAdapter: NotificationAdapter
    var total = 0
    private lateinit var session: Session
    private var isLoadMore = false
    var offset = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_list)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = getString(R.string.notifications)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        swipeLayout = findViewById(R.id.swipeLayout)
        scrollView = findViewById(R.id.scrollView)
        activity = this@NotificationListActivity
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        session = Session(activity)
        if (isConnected(activity)) {
            getNotificationData(0)
        } else {
            setSnackBar(
                activity,
                getString(R.string.no_internet_message),
                getString(R.string.ok),
                Color.RED
            )
        }
        swipeLayout.setOnRefreshListener(OnRefreshListener {
            if (isConnected(activity)) {
                offset = 0
                session.setData("" + offset, "" + 0)
                getNotificationData(0)
                swipeLayout.isRefreshing = false
                disableSwipe(swipeLayout)
            } else {
                setSnackBar(
                    activity,
                    getString(R.string.no_internet_message),
                    getString(R.string.ok),
                    Color.RED
                )
            }
        })
    }

    private fun getNotificationData(startoffset: Int) {
        notifications = ArrayList()
        val linearLayoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = linearLayoutManager
        val params: MutableMap<String, String?> = HashMap()
        params[Constant.DELIVERY_BOY_ID] = session.getData(Constant.ID)
        params[Constant.GET_NOTIFICATION] = Constant.GetVal
        params[Constant.OFFSET] = "" + offset
        params[Constant.LIMIT] = Constant.PRODUCT_LOAD_LIMIT


//        System.out.println("====params " + params.toString());
        requestToVolley(object : VolleyCallback {
            override fun onSuccess(result: Boolean, response: String?) {
                if (result) {
                    try {
                        //System.out.println("====product  " + response);
                        val objectbject = JSONObject(response)
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            total = objectbject.getString(Constant.TOTAL).toInt()
                            session.setData(Constant.TOTAL, total.toString())
                            val `object` = JSONObject(response)
                            val jsonArray = `object`.getJSONArray(Constant.DATA)
                            val g = Gson()
                            for (i in 0 until jsonArray.length()) {
                                val jsonObject1 = jsonArray.getJSONObject(i)
                                if (jsonObject1 != null) {
                                    val notification =
                                        g.fromJson(jsonObject1.toString(), Notification::class.java)
                                    notifications.add(notification)
                                } else {
                                    break
                                }
                            }
                            if (startoffset == 0) {
                                notificationAdapter =
                                    NotificationAdapter(activity, notifications)
                                notificationAdapter.setHasStableIds(true)
                                recyclerView.adapter = notificationAdapter
                                scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                                    // if (diff == 0) {
                                    if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                                        val linearLayoutManager =
                                            recyclerView.layoutManager as LinearLayoutManager?
                                        if (notifications.size < total) {
                                            if (!isLoadMore) {
                                                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == notifications.size - 1) {


                                                    Handler().postDelayed({
                                                        offset += Constant.LOAD_ITEM_LIMIT
                                                        val params: MutableMap<String, String?> =
                                                            HashMap()
                                                        params[Constant.DELIVERY_BOY_ID] =
                                                            session.getData(
                                                                Constant.ID
                                                            )
                                                        params[Constant.GET_NOTIFICATION] =
                                                            Constant.GetVal
                                                        params[Constant.OFFSET] = "" + offset
                                                        params[Constant.LIMIT] =
                                                            Constant.PRODUCT_LOAD_LIMIT
                                                        requestToVolley(
                                                            object : VolleyCallback {
                                                                override fun onSuccess(
                                                                    result: Boolean,
                                                                    response: String?
                                                                ) {
                                                                    if (result) {
                                                                        try {
                                                                            // System.out.println("====product  " + response);
                                                                            val objectbject1 =
                                                                                JSONObject(response)
                                                                            if (!objectbject1.getBoolean(
                                                                                    Constant.ERROR
                                                                                )
                                                                            ) {
                                                                                session.setData(
                                                                                    Constant.TOTAL,
                                                                                    objectbject1.getString(
                                                                                        Constant.TOTAL
                                                                                    )
                                                                                )


                                                                                val `object` =
                                                                                    JSONObject(
                                                                                        response
                                                                                    )
                                                                                val jsonArray =
                                                                                    `object`.getJSONArray(
                                                                                        Constant.DATA
                                                                                    )
                                                                                val g = Gson()
                                                                                for (i in 0 until jsonArray.length()) {
                                                                                    val jsonObject1 =
                                                                                        jsonArray.getJSONObject(
                                                                                            i
                                                                                        )
                                                                                    if (jsonObject1 != null) {
                                                                                        val notification =
                                                                                            g.fromJson(
                                                                                                jsonObject1.toString(),
                                                                                                Notification::class.java
                                                                                            )
                                                                                        notifications.add(
                                                                                            notification
                                                                                        )
                                                                                    } else {
                                                                                        break
                                                                                    }
                                                                                }
                                                                                notificationAdapter.notifyDataSetChanged()
                                                                                isLoadMore = false
                                                                            }
                                                                        } catch (e: JSONException) {
                                                                            e.printStackTrace()
                                                                        }
                                                                    }
                                                                }
                                                            },
                                                            activity,
                                                            Constant.MAIN_URL,
                                                            params,
                                                            false
                                                        )
                                                    }, 0)
                                                    isLoadMore = true
                                                }
                                            }
                                        }
                                    }
                                })
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }
        }, activity, Constant.MAIN_URL, params, true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun setSnackBar(activity: Activity, message: String?, action: String?, color: Int) {
        val snackbar = Snackbar.make(
            activity.findViewById(android.R.id.content),
            message!!,
            Snackbar.LENGTH_INDEFINITE
        )
        snackbar.setAction(action) {
            getNotificationData(0)
            snackbar.dismiss()
        }
        snackbar.setActionTextColor(color)
        val snackbarView = snackbar.view
        val textView =
            snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.maxLines = 5
        snackbar.show()
    }
}