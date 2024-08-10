package com.fallgan.deliveryapp.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import com.fallgan.deliveryapp.R
import com.fallgan.deliveryapp.adapter.WalletHistoryAdapter
import com.fallgan.deliveryapp.helper.ApiConfig.requestToVolley
import com.fallgan.deliveryapp.helper.Constant
import com.fallgan.deliveryapp.helper.Session
import com.fallgan.deliveryapp.helper.VolleyCallback
import com.fallgan.deliveryapp.model.WalletHistory

class WalletHistoryActivity : AppCompatActivity() {
    lateinit var session: Session
    lateinit var walletHistories: ArrayList<WalletHistory?>
    lateinit var walletHistoryAdapter: WalletHistoryAdapter
    lateinit var tvBalance: TextView
    lateinit var recyclerViewWalletHistory: RecyclerView
    lateinit var toolbar: Toolbar
    lateinit var activity: Activity
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var btnSendWithdrawalRequest: Button
    private lateinit var scrollView: NestedScrollView
    private var isLoadMore = false
    var total = 0
    var dataType = Constant.FUND_TRANSFERS
    var offset = 0
    private var filterIndex = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet_history)
        activity = this@WalletHistoryActivity
        session = Session(activity)
        toolbar = findViewById(R.id.toolbar)
        tvBalance = findViewById(R.id.tvBalance)
        btnSendWithdrawalRequest = findViewById(R.id.btnSendWithdrawalRequest)
        swipeRefresh =
            findViewById(R.id.swipeRefresh)
        scrollView = findViewById(R.id.scrollView)
        recyclerViewWalletHistory = findViewById(R.id.recyclerViewWalletHistory)
        recyclerViewWalletHistory.layoutManager = LinearLayoutManager(activity)
        tvBalance.text = Session(activity).getData(Constant.CURRENCY) + Constant.formatter.format(
            session.getData(Constant.BALANCE)!!.toDouble()
        )
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle(R.string.wallet_history)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        getWalletHistory()
        swipeRefresh.setOnRefreshListener(OnRefreshListener {
            swipeRefresh.isRefreshing = false
            offset = 0
            getWalletHistory()
        })
        btnSendWithdrawalRequest.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this@WalletHistoryActivity)
            val inflater =
                this@WalletHistoryActivity.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_confirm_send_request, null)
            alertDialog.setView(dialogView)
            alertDialog.setCancelable(true)
            val dialog = alertDialog.create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val tvDialogSend = dialogView.findViewById<TextView>(R.id.tvDialogSend)
            val tvDialogCancel = dialogView.findViewById<TextView>(R.id.tvDialogCancel)
            val edtAmount = dialogView.findViewById<EditText>(R.id.edtAmount)
            val edtMsg = dialogView.findViewById<EditText>(R.id.edtMsg)
            tvDialogSend.setOnClickListener {
                if (edtAmount.text.toString().isNotEmpty() || edtAmount.text.toString() == "0") {
                    if (edtAmount.text.toString()
                            .toDouble() <= session.getData(Constant.BALANCE)!!
                            .toDouble()
                    ) {
                        SendWithdrawalRequest(
                            edtAmount.text.toString().trim { it <= ' ' },
                            edtMsg.text.toString().trim { it <= ' ' })
                        dialog.dismiss()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            R.string.alert_balance_limit,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    edtAmount.error = getString(R.string.alert_enter_amount)
                }
            }
            tvDialogCancel.setOnClickListener { dialog.dismiss() }
            dialog.show()
        }
    }

    private fun SendWithdrawalRequest(amount: String?, message: String?) {
        val params: MutableMap<String, String?> = HashMap()
        params[Constant.TYPE_ID] = session.getData(Constant.ID)
        params[Constant.SEND_WITHDRAWAL_REQUEST] = Constant.GetVal
        params[Constant.TYPE] = Constant.DELIVERY_BOY
        params[Constant.AMOUNT] = amount
        params[Constant.MESSAGE] = message
        requestToVolley(object : VolleyCallback {
            @SuppressLint("SetTextI18n")
            override fun onSuccess(result: Boolean, response: String?) {
                if (result) {
                    try {
                        val objectbject1 = JSONObject(response)
                        if (!objectbject1.getBoolean(Constant.ERROR)) {
                            val update_balance = objectbject1.getString(Constant.UPDATED_BALANCE)
                            tvBalance.text =
                                Session(activity).getData(Constant.CURRENCY) + Constant.formatter.format(
                                    update_balance.toDouble()
                                )
                            DrawerActivity.tvWallet.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_wallet_white,
                                0,
                                0,
                                0
                            )
                            DrawerActivity.tvWallet.text =
                                getString(R.string.wallet_balance) + "\t:\t" + Session(
                                    activity
                                ).getData(Constant.CURRENCY) + Constant.formatter.format(
                                    update_balance.toDouble()
                                )
                            session.setData(
                                Constant.BALANCE,
                                objectbject1.getString(Constant.UPDATED_BALANCE)
                            )
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }
        }, activity, Constant.MAIN_URL, params, false)
    }

    fun getWalletHistory() {
        walletHistories = ArrayList()
        val linearLayoutManager = LinearLayoutManager(activity)
        recyclerViewWalletHistory.layoutManager = linearLayoutManager
        walletHistoryAdapter = WalletHistoryAdapter(activity, walletHistories)

        val params: MutableMap<String, String?> = HashMap()
        params[Constant.TYPE_ID] = session.getData(Constant.ID)
        params[Constant.GET_WITHDRAWAL_REQUEST] = Constant.GetVal
        params[Constant.OFFSET] = "" + offset
        params[Constant.TYPE] = Constant.DELIVERY_BOY
        params[Constant.LIMIT] = Constant.PRODUCT_LOAD_LIMIT
        params[Constant.DATA_TYPE] = dataType
        requestToVolley(object : VolleyCallback {
            override fun onSuccess(result: Boolean, response: String?) {
                if (result) {
                    try {
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
                                    val notification = g.fromJson(
                                        jsonObject1.toString(),
                                        WalletHistory::class.java
                                    )
                                    walletHistories.add(notification)
                                } else {
                                    break
                                }
                            }
                            if (offset == 0) {
                                walletHistoryAdapter =
                                    WalletHistoryAdapter(activity, walletHistories)
                                walletHistoryAdapter.setHasStableIds(true)
                                recyclerViewWalletHistory.adapter = walletHistoryAdapter
                                scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                                    if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                                        val linearLayoutManager =
                                            recyclerViewWalletHistory.layoutManager as LinearLayoutManager?
                                        if (walletHistories.size < total) {
                                            if (!isLoadMore) {
                                                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == walletHistories.size - 1) {

                                                    offset += Constant.LOAD_ITEM_LIMIT
                                                    val params: MutableMap<String, String?> =
                                                        HashMap()
                                                    params[Constant.TYPE_ID] =
                                                        session.getData(
                                                            Constant.ID
                                                        )
                                                    params[Constant.GET_WITHDRAWAL_REQUEST] =
                                                        Constant.GetVal
                                                    params[Constant.OFFSET] = "" + offset
                                                    params[Constant.TYPE] =
                                                        Constant.DELIVERY_BOY
                                                    params[Constant.LIMIT] =
                                                        Constant.PRODUCT_LOAD_LIMIT
                                                    params[Constant.DATA_TYPE] = dataType
                                                    requestToVolley(
                                                        object : VolleyCallback {
                                                            override fun onSuccess(
                                                                result: Boolean,
                                                                response: String?
                                                            ) {
                                                                if (result) {
                                                                    try {
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
                                                                                            WalletHistory::class.java
                                                                                        )
                                                                                    walletHistories.add(
                                                                                        notification
                                                                                    )
                                                                                } else {
                                                                                    break
                                                                                }
                                                                            }
                                                                            walletHistoryAdapter.notifyDataSetChanged()
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
        if (item.itemId == R.id.toolbar_filter) {
            val builder = AlertDialog.Builder(
                activity
            )
            builder.setTitle(activity.resources.getString(R.string.filterby))
            builder.setSingleChoiceItems(Constant.filterValues, filterIndex) { dialog, item ->
                filterIndex = item
                when (item) {
                    0 -> dataType = Constant.FUND_TRANSFERS
                    1 -> dataType = Constant.WITHDRAWAL_REQUEST
                }
                getWalletHistory()
                dialog.dismiss()
            }
            val alertDialog = builder.create()
            alertDialog.show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
    }
}