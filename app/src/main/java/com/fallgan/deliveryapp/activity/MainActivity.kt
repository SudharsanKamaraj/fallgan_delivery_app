package com.fallgan.deliveryapp.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import com.fallgan.deliveryapp.R
import com.fallgan.deliveryapp.adapter.OrderListAdapter
import com.fallgan.deliveryapp.helper.*
import com.fallgan.deliveryapp.helper.ApiConfig.disableSwipe
import com.fallgan.deliveryapp.helper.ApiConfig.requestToVolley
import com.fallgan.deliveryapp.helper.AppController.Companion.isConnected
import com.fallgan.deliveryapp.model.OrderList
import java.util.*

@SuppressLint("NotifyDataSetChanged")
class MainActivity : DrawerActivity() {
    lateinit var session1: Session
    private var doubleBackToExitPressedOnce = false
    private lateinit var tvOrdersCount: TextView
    private lateinit var tvBalanceCount: TextView
    private lateinit var tvBonusCount: TextView
    lateinit var recycleOrderList: RecyclerView
    lateinit var toolbar: Toolbar
    lateinit var activity1: Activity
    lateinit var orderListAdapter: OrderListAdapter
    private lateinit var lyt_main_activity_swipe_refresh: SwipeRefreshLayout
    lateinit var scrollView: NestedScrollView
    lateinit var menu: Menu
    var total = 0
    private var isLoadMore = false
    var offset = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_main, frameLayout)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.app_name)
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        activity1 = this@MainActivity
        session1 = Session(activity1)

        val resources = activity1.resources
        val dm = activity1.resources.displayMetrics
        val configuration = resources.configuration
        configuration.setLocale(Locale(Constant.LANGUAGE.lowercase(Locale.getDefault())))
        resources.updateConfiguration(configuration, dm)

        tvBalanceCount = findViewById(R.id.tvBalanceCount)
        tvOrdersCount = findViewById(R.id.tvOrdersCount)
        tvBonusCount = findViewById(R.id.tvBonusCount)
        scrollView = findViewById(R.id.scrollView)
        recycleOrderList = findViewById(R.id.recycleOrderList)
        lyt_main_activity_swipe_refresh = findViewById(R.id.lyt_main_activity_swipe_refresh)
        if (session1.isUserLoggedIn) {
            GetData()
            lyt_main_activity_swipe_refresh.setColorSchemeResources(R.color.colorPrimary)
            lyt_main_activity_swipe_refresh.setOnRefreshListener(OnRefreshListener {
                if (isConnected(activity1)) {
                    offset = 0
                    GetData()
                    disableSwipe(lyt_main_activity_swipe_refresh)
                } else {
                    setSnackBar(
                        activity1,
                        getString(R.string.no_internet_message),
                        getString(R.string.retry)
                    )
                }
                lyt_main_activity_swipe_refresh.isRefreshing = false
            })
        }
        drawerToggle = object : ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        ) {}


        if (session1.getData(Constant.ID) != null) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Get new FCM registration token
                    val token = task.result
                    session1.setData(Constant.FCM_ID, token)
                    registerFCM(token)
                }
            }
        }
    }

    private fun registerFCM(token: String?) {
        val params: MutableMap<String, String?> = HashMap()
        params[Constant.UPDATE_DELIVERY_BOY_FCM_ID] = Constant.GetVal
        params[Constant.DELIVERY_BOY_ID] = session1.getData(Constant.ID)
        params[Constant.FCM_ID] = token
        requestToVolley(object : VolleyCallback {
            override fun onSuccess(result: Boolean, response: String?) {
                if (result) {
                    try {
                        val jsonObject = JSONObject(response)
                        if (!jsonObject.getBoolean(Constant.ERROR)) {
                            session1.setData(Constant.FCM_ID, token)
                        }
                    } catch (ignored: JSONException) {
                    }
                }
            }
        }, activity1, Constant.MAIN_URL, params, false)
    }

    private fun GetData() {
        orderListArrayList = ArrayList()
        val linearLayoutManager = LinearLayoutManager(activity1)
        recycleOrderList.layoutManager = linearLayoutManager
        val params: MutableMap<String, String?> = HashMap()
        params[Constant.DELIVERY_BOY_ID] = session1.getData(Constant.ID)
        params[Constant.GET_ORDERS] = Constant.GetVal
        params[Constant.OFFSET] = "" + offset
        params[Constant.LIMIT] = Constant.PRODUCT_LOAD_LIMIT

//        System.out.println("====params " + params.toString());
        requestToVolley(object : VolleyCallback {
            override fun onSuccess(result: Boolean, response: String?) {
                if (result) {
                    try {
                        //    System.out.println("====product  " + response);
                        val jsonObject = JSONObject(response)
                        if (!jsonObject.getBoolean(Constant.ERROR)) {
                            total = jsonObject.getString(Constant.TOTAL).toInt()
                            session1.setData(Constant.TOTAL, jsonObject.getString(Constant.TOTAL))
                            val `object` = JSONObject(response)
                            val jsonArray = `object`.getJSONArray(Constant.DATA)
                            for (i in 0 until jsonArray.length()) {
                                val jsonObject1 = jsonArray.getJSONObject(i)
                                if (jsonObject1 != null) {
                                    val orderList =
                                        Gson().fromJson(
                                            jsonObject1.toString(),
                                            OrderList::class.java
                                        )
                                    orderListArrayList.add(orderList)
                                } else {
                                    break
                                }
                            }
                            if (offset == 0) {
                                orderListAdapter =
                                    OrderListAdapter(activity1, orderListArrayList)
                                orderListAdapter.setHasStableIds(true)
                                recycleOrderList.adapter = orderListAdapter
                                scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v: NestedScrollView, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->

                                    // if (diff == 0) {
                                    if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                                        val linearLayoutManager1 =
                                            recycleOrderList.layoutManager as LinearLayoutManager?
                                        if (orderListArrayList.size < total) {
                                            if (!isLoadMore) {
                                                if (linearLayoutManager1 != null && linearLayoutManager1.findLastCompletelyVisibleItemPosition() == orderListArrayList.size - 1) {

                                                    Handler().postDelayed({
                                                        offset += Constant.LOAD_ITEM_LIMIT
                                                        val params1: MutableMap<String, String?> =
                                                            HashMap()
                                                        params1[Constant.DELIVERY_BOY_ID] =
                                                            session1.getData(
                                                                Constant.ID
                                                            )
                                                        params1[Constant.GET_ORDERS] =
                                                            Constant.GetVal
                                                        params1[Constant.LIMIT] =
                                                            "" + Constant.LOAD_ITEM_LIMIT
                                                        params1[Constant.OFFSET] = "" + offset
                                                        requestToVolley(
                                                            object : VolleyCallback {
                                                                override fun onSuccess(
                                                                    result1: Boolean,
                                                                    response1: String?
                                                                ) {
                                                                    if (result1) {
                                                                        try {
                                                                            // System.out.println("====product  " + response);
                                                                            val objectbject1 =
                                                                                JSONObject(response1)
                                                                            if (!objectbject1.getBoolean(
                                                                                    Constant.ERROR
                                                                                )
                                                                            ) {
                                                                                session1.setData(
                                                                                    Constant.TOTAL,
                                                                                    objectbject1.getString(
                                                                                        Constant.TOTAL
                                                                                    )
                                                                                )


                                                                                val object1 =
                                                                                    JSONObject(
                                                                                        response1
                                                                                    )
                                                                                val jsonArray1 =
                                                                                    object1.getJSONArray(
                                                                                        Constant.DATA
                                                                                    )
                                                                                for (i in 0 until jsonArray1.length()) {
                                                                                    val jsonObject1 =
                                                                                        jsonArray1.getJSONObject(
                                                                                            i
                                                                                        )
                                                                                    if (jsonObject1 != null) {
                                                                                        val orderList =
                                                                                            Gson().fromJson(
                                                                                                jsonObject1.toString(),
                                                                                                OrderList::class.java
                                                                                            )
                                                                                        orderListArrayList.add(
                                                                                            orderList
                                                                                        )
                                                                                    } else {
                                                                                        break
                                                                                    }
                                                                                }
                                                                                orderListAdapter.notifyDataSetChanged()
                                                                                isLoadMore = false
                                                                            }
                                                                        } catch (e: JSONException) {
                                                                            e.printStackTrace()
                                                                        }
                                                                    }
                                                                }
                                                            },
                                                            activity1,
                                                            Constant.MAIN_URL,
                                                            params1,
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
                            getDeliveryBoyData(activity1)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }
        }, activity1, Constant.MAIN_URL, params, false)
    }

    fun getDeliveryBoyData(activity1: Activity) {
        if (isConnected(activity1)) {
            val params: MutableMap<String, String?> = HashMap()
            params[Constant.DELIVERY_BOY_ID] = session1.getData(Constant.ID)
            params[Constant.GET_DELIVERY_BOY_BY_ID] = Constant.GetVal
            requestToVolley(object : VolleyCallback {
                override fun onSuccess(result: Boolean, response: String?) {
                    //  System.out.println("============" + response);
                    if (result) {
                        try {
                            val jsonObject = JSONObject(response)
                            if (!jsonObject.getBoolean(Constant.ERROR)) {
                                StartMainActivity(
                                    activity1,
                                    jsonObject.getJSONArray(Constant.DATA).getJSONObject(0)
                                )
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }
            }, activity1, Constant.MAIN_URL, params, false)
        } else {
            setSnackBar(
                activity1,
                getString(R.string.no_internet_message),
                getString(R.string.retry)
            )
        }
    }

    @SuppressLint("SetTextI18n")
    fun StartMainActivity(activity1: Activity, jsonObject: JSONObject) {
        if (isConnected(activity1)) {
            try {
                Session(activity1).createUserLoginSession(
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
                session1.setData(Constant.ID, jsonObject.getString(Constant.ID))
                invalidateOptionsMenu()
                tvOrdersCount.visibility = View.VISIBLE
                tvBalanceCount.visibility = View.VISIBLE
                tvBonusCount.visibility = View.VISIBLE
                tvOrdersCount.text = session1.getData(Constant.TOTAL)
                tvBalanceCount.text =
                    Session(activity1).getData(Constant.CURRENCY) + session1.getData(
                        Constant.BALANCE
                    )
                tvBonusCount.text = session1.getData(Constant.BONUS) + " %"
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        } else {
            setSnackBar(
                activity1,
                getString(R.string.no_internet_message),
                getString(R.string.retry)
            )
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (drawer.isDrawerOpen(navigationView)) drawer.closeDrawers() else doubleBack()
    }

    private fun doubleBack() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        doubleBackToExitPressedOnce = true
        Toast.makeText(
            this,
            getString(R.string.please_click_back_again_to_exit),
            Toast.LENGTH_SHORT
        ).show()
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    private fun setSnackBar(activity1: Activity, message: String?, action: String?) {
        val snackbar = Snackbar.make(
            activity1.findViewById(android.R.id.content),
            message!!,
            Snackbar.LENGTH_INDEFINITE
        )
        snackbar.setAction(action) { view: View? -> snackbar.dismiss() }
        snackbar.setActionTextColor(Color.RED)
        val snackbarView = snackbar.view
        val textView =
            snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.maxLines = 5
        snackbar.show()
    }

    private fun enableDisableDeliveryBoy(status: String) {
        val params: MutableMap<String, String?> = HashMap()
        params[Constant.DELIVERY_BOY_ID] = session1.getData(Constant.ID)
        params[Constant.CHANGE_AVAILABILITY] = Constant.GetVal
        params[Constant.IS_AVAILABLE] = status
        requestToVolley(object : VolleyCallback {
            override fun onSuccess(result: Boolean, response: String?) {
                try {
                    val jsonObject = JSONObject(response)
                    var message = jsonObject.getString(Constant.MESSAGE)
                    if (!jsonObject.getBoolean(Constant.ERROR)) {
                        message =
                            getString(R.string.delivery_boy_status) + ((if (status == "1") getString(
                                R.string.enabled
                            ) else getString(
                                R.string.disabled
                            )) + getString(R.string.successfully))
                        if (status == "1") {
                            menu.findItem(R.id.toolbar_action).setIcon(R.drawable.ic_action_off)
                        } else {
                            menu.findItem(R.id.toolbar_action).setIcon(R.drawable.ic_action_on)
                        }
                        session1.setData(Constant.STATUS, status)
                        invalidateOptionsMenu()
                    }
                    Toast.makeText(activity1, message, Toast.LENGTH_SHORT).show()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity1, Constant.MAIN_URL, params, false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.toolbar_action) {
            enableDisableDeliveryBoy(if (session1.getData(Constant.STATUS) == "1") "0" else "1")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        this.menu = menu
        menu.findItem(R.id.toolbar_filter).isVisible = false
        menu.findItem(R.id.toolbar_action).isVisible = true
        if (session1.getData(Constant.STATUS) == "1") {
            menu.findItem(R.id.toolbar_action).setIcon(R.drawable.ic_action_off)
        } else {
            menu.findItem(R.id.toolbar_action).setIcon(R.drawable.ic_action_on)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    public override fun onResume() {
        super.onResume()
        if (Constant.CLICK) {
            orderListAdapter.notifyDataSetChanged()
            Constant.CLICK = false
        }
    }

    companion object {
        lateinit var orderListArrayList: ArrayList<OrderList?>
    }
}