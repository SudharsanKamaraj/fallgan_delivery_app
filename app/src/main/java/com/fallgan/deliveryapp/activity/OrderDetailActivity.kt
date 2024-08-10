package com.fallgan.deliveryapp.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.google.android.material.snackbar.Snackbar
import org.json.JSONException
import org.json.JSONObject
import com.fallgan.deliveryapp.R
import com.fallgan.deliveryapp.adapter.ItemsAdapter
import com.fallgan.deliveryapp.helper.ApiConfig.disableSwipe
import com.fallgan.deliveryapp.helper.ApiConfig.requestToVolley
import com.fallgan.deliveryapp.helper.AppController.Companion.isConnected
import com.fallgan.deliveryapp.helper.AppController.Companion.toTitleCase
import com.fallgan.deliveryapp.helper.Constant
import com.fallgan.deliveryapp.helper.Session
import com.fallgan.deliveryapp.helper.VolleyCallback
import com.fallgan.deliveryapp.model.OrderList
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@SuppressLint("SetTextI18n")
class OrderDetailActivity : AppCompatActivity() {
    private lateinit var tvNote: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvOrderID: TextView
    private lateinit var tvName: TextView
    private lateinit var tvSellerPhoneNumber: TextView
    private lateinit var tvSellerName: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvAddress: TextView
    private lateinit var tvDeliveryTime: TextView
    private lateinit var tvItemTotal: TextView
    private lateinit var tvTaxAmt: TextView
    private lateinit var tvPCAmount: TextView
    private lateinit var tvWallet: TextView
    private lateinit var tvFinalTotal: TextView
    private lateinit var tvPaymentMethod: TextView
    private lateinit var tvDiscountAmount: TextView
    private lateinit var tvDeliveryCharge: TextView
    private lateinit var btnDeliveryStatus: Button
    private lateinit var btnGetDirection: Button
    private lateinit var SwipeRefresh: SwipeRefreshLayout
    lateinit var toolbar: Toolbar
    lateinit var activity: Activity
    private lateinit var lytOrderDetail: RelativeLayout
    private lateinit var latitude: String
    private lateinit var longitude: String
    private lateinit var s_latitude: String
    private lateinit var s_longitude: String
    private lateinit var orderList: OrderList
    private lateinit var arrayList1: ArrayList<String>
    private lateinit var tvSellerAddress: TextView
    private lateinit var otp: String
    private lateinit var updatedStatus: Array<String?>
    lateinit var recyclerView: RecyclerView
    private var position1 = 0
    private var checkedItem = 0
    private var itemIds = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)
        toolbar = findViewById(R.id.toolbar)
        activity = this@OrderDetailActivity
        orderList = (intent.getSerializableExtra(Constant.ITEM) as OrderList?)!!
        position1 = intent.getIntExtra(Constant.POSITION, 0)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.order_item_number) + orderList.id
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        tvDate = findViewById(R.id.tvDate)
        tvOrderID = findViewById(R.id.tvOrderID)
        tvName = findViewById(R.id.tvName)
        tvNote = findViewById(R.id.tvNote)
        tvSellerAddress = findViewById(R.id.tvSellerAddress)
        tvPhone = findViewById(R.id.tvPhone)
        tvSellerPhoneNumber = findViewById(R.id.tvSellerPhoneNumber)
        tvSellerName = findViewById(R.id.tvSellerName)
        tvAddress = findViewById(R.id.tvAddress)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        findViewById<View>(R.id.nestedScrollView).isNestedScrollingEnabled = false
        recyclerView.isNestedScrollingEnabled = false
        tvDeliveryTime = findViewById(R.id.tvDeliveryTime)
        tvItemTotal = findViewById(R.id.tvItemTotal)
        tvDeliveryCharge = findViewById(R.id.tvDeliveryCharge)
        btnDeliveryStatus = findViewById(R.id.btnDeliveryStatus)
        tvTaxAmt = findViewById(R.id.tvTaxAmt)
        tvPCAmount = findViewById(R.id.tvPCAmount)
        tvFinalTotal = findViewById(R.id.tvFinalTotal)
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod)
        tvWallet = findViewById(R.id.tvWallet)
        tvDiscountAmount = findViewById(R.id.tvDiscountAmount)
        lytOrderDetail = findViewById(R.id.lytOrderDetail)
        SwipeRefresh = findViewById(R.id.SwipeRefresh)
        btnGetDirection = findViewById(R.id.btnGetDirection)
        if (isConnected(activity)) {
            getOrderData(orderList)
        } else {
            setSnackBar(
                activity,
                getString(R.string.no_internet_message),
                getString(R.string.retry),
                Color.RED
            )
        }
        SwipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        SwipeRefresh.setOnRefreshListener(OnRefreshListener {
            if (isConnected(activity)) {
                getOrderData(orderList)
                SwipeRefresh.isRefreshing = false
                disableSwipe(SwipeRefresh)
            } else {
                setSnackBar(
                    activity,
                    getString(R.string.no_internet_message),
                    getString(R.string.retry),
                    Color.RED
                )
            }
            SwipeRefresh.isRefreshing = false
        })
    }

    private fun getOrderData(orderList: OrderList?) {
        for (i in orderList!!.items!!.indices) {
            itemIds = if (i != orderList.items!!.size) {
                itemIds + orderList.items!![i].id + ","
            } else {
                itemIds + orderList.items!![i].id
            }
        }
        tvOrderID.text = getString(R.string.order_id) + orderList.id
        tvDate.text = getString(R.string.order_on) + orderList.date_added
        otp = orderList.otp.toString()
        tvName.text = getString(R.string._name) + orderList.user_name
        tvNote.text = if (orderList.order_note == "") "-" else orderList.order_note
        tvPhone.text = orderList.mobile
        tvSellerPhoneNumber.text = orderList.seller_mobile
        tvSellerName.text = orderList.seller_name
        tvSellerAddress.text = orderList.seller_address
        tvAddress.text = orderList.address
        btnDeliveryStatus.text = toTitleCase(orderList.items!![0].active_status)
        s_latitude = orderList.seller_latitude.toString()
        s_longitude = orderList.seller_longitude.toString()
        tvDeliveryTime.text = getString(R.string.delivery_by) + orderList.delivery_time
        tvDeliveryCharge.text = Session(activity)
            .getData(Constant.CURRENCY) + orderList.delivery_charge
        if (orderList.latitude == "0" && orderList.longitude == "0") {
            btnGetDirection.visibility = View.GONE
        } else {
            latitude = orderList.latitude.toString()
            longitude = orderList.longitude.toString()
        }
        tvItemTotal.text = Session(activity)
            .getData(Constant.CURRENCY) + orderList.total
        tvPCAmount.text = Session(activity)
            .getData(Constant.CURRENCY) + orderList.promo_discount
        tvDiscountAmount.text = Session(activity)
            .getData(Constant.CURRENCY) + orderList.discount
        tvWallet.text = Session(activity)
            .getData(Constant.CURRENCY) + orderList.wallet_balance
        tvFinalTotal.text = Session(activity)
            .getData(Constant.CURRENCY) + orderList.final_total
        tvPaymentMethod.text =
            getString(R.string.via) + orderList.payment_method!!.uppercase(Locale.getDefault())
        lytOrderDetail.visibility = View.VISIBLE
        arrayList1 = ArrayList()
        arrayList1.add(Constant.AWAITING_PAYMENT)
        arrayList1.add(Constant.RECEIVED)
        arrayList1.add(Constant.PROCESSED)
        arrayList1.add(Constant.SHIPPED)
        arrayList1.add(Constant.DELIVERED)
        arrayList1.add(Constant.CANCELLED)
        arrayList1.add(Constant.RETURNED)
        recyclerView.adapter = ItemsAdapter(activity, orderList.items!!)
    }

    private fun Confirm_OTP() {
        val alertDialog = AlertDialog.Builder(this@OrderDetailActivity)
        val inflater =
            this@OrderDetailActivity.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_otp_confirm_request, null)
        alertDialog.setView(dialogView)
        alertDialog.setCancelable(true)
        val dialog = alertDialog.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val tvDialogConfirm = dialogView.findViewById<TextView>(R.id.tvDialogConfirm)
        val tvDialogCancel = dialogView.findViewById<TextView>(R.id.tvDialogCancel)
        val edtOTP = dialogView.findViewById<EditText>(R.id.edtOTP)
        tvDialogConfirm.setOnClickListener { v: View? ->
            if (edtOTP.text.toString()
                    .isNotEmpty() || edtOTP.text.toString() == "0" || edtOTP.text.toString().length >= 6
            ) {
                if (checkedItem <= 3) {
                    if (checkedItem == 3) {
                        if (edtOTP.text.toString() == otp) {
                            ChangeOrderStatus(
                                activity,
                                updatedStatus[checkedItem]!!.lowercase(Locale.getDefault())
                            )
                            dialog.dismiss()
                        } else {
                            Toast.makeText(
                                activity,
                                getString(R.string.otp_not_matched),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(
                        activity,
                        getString(R.string.can_not_update_order),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                edtOTP.error = getString(R.string.alert_otp)
            }
        }
        tvDialogCancel.setOnClickListener { v: View? -> dialog.dismiss() }
        dialog.show()
    }

    fun onButtonClick(view: View) {
        if (isConnected(activity)) {
            val id = view.id
            if (id == R.id.btnCallCustomer) {
                try {
                    val callIntent = Intent(Intent.ACTION_CALL)
                    if (ContextCompat.checkSelfPermission(
                            this@OrderDetailActivity,
                            Manifest.permission.CALL_PHONE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            this@OrderDetailActivity, arrayOf(
                                Manifest.permission.CALL_PHONE
                            ), 1
                        )
                    } else {
                        callIntent.data =
                            Uri.parse("tel:" + tvPhone.text.toString().trim { it <= ' ' })
                        startActivity(callIntent)
                    }
                } catch (ignored: Exception) {
                }
            } else if (id == R.id.btnGetDirection) {
                val builder1 = android.app.AlertDialog.Builder(activity)
                builder1.setMessage(R.string.map_open_message)
                builder1.setCancelable(true)
                builder1.setPositiveButton(
                    getString(R.string.yes)
                ) { dialog: DialogInterface?, id15: Int ->
//                                com.google.android.apps.maps
                    try {
                        val googleMapIntentUri =
                            Uri.parse("google.navigation:q=$latitude,$longitude")
                        val mapIntent = Intent(Intent.ACTION_VIEW, googleMapIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        activity.startActivity(mapIntent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        val builder112 = android.app.AlertDialog.Builder(activity)
                        builder112.setMessage("Please install google map first.")
                        builder112.setCancelable(true)
                        builder112.setPositiveButton(
                            getString(R.string.ok)
                        ) { dialog14: DialogInterface, id151: Int -> dialog14.cancel() }
                        val alert11 = builder112.create()
                        alert11.show()
                    }
                }
                builder1.setNegativeButton(
                    getString(R.string.no)
                ) { dialog: DialogInterface, id13: Int -> dialog.cancel() }
                val alert11 = builder1.create()
                alert11.show()
            } else if (id == R.id.btnCallToSeller) {
                try {
                    val callIntent = Intent(Intent.ACTION_CALL)
                    if (ContextCompat.checkSelfPermission(
                            this@OrderDetailActivity,
                            Manifest.permission.CALL_PHONE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            this@OrderDetailActivity, arrayOf(
                                Manifest.permission.CALL_PHONE
                            ), 1
                        )
                    } else {
                        callIntent.data = Uri.parse(
                            "tel:" + tvSellerPhoneNumber.text.toString().trim { it <= ' ' })
                        startActivity(callIntent)
                    }
                } catch (ignored: Exception) {
                }
            } else if (id == R.id.btnGetSellerDirection) {
                val builder1 = android.app.AlertDialog.Builder(activity)
                builder1.setMessage(R.string.map_open_message)
                builder1.setCancelable(true)
                builder1.setPositiveButton(
                    getString(R.string.yes)
                ) { dialog: DialogInterface?, id14: Int ->
//                                com.google.android.apps.maps
                    try {
                        val googleMapIntentUri =
                            Uri.parse("google.navigation:q=$s_latitude,$s_longitude")
                        val mapIntent = Intent(Intent.ACTION_VIEW, googleMapIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        activity.startActivity(mapIntent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        val builder11 = android.app.AlertDialog.Builder(activity)
                        builder11.setMessage("Please install google map first.")
                        builder11.setCancelable(true)
                        builder11.setPositiveButton(
                            getString(R.string.ok)
                        ) { dialog12: DialogInterface, id12: Int -> dialog12.cancel() }
                        val alert11 = builder11.create()
                        alert11.show()
                    }
                }
                builder1.setNegativeButton(
                    getString(R.string.no)
                ) { dialog: DialogInterface, id1: Int -> dialog.cancel() }
                val alert11 = builder1.create()
                alert11.show()
            } else if (id == R.id.btnDeliveryStatus) {
                // setup the alert builder
                updatedStatus = arrayOfNulls(6)
                val builder = android.app.AlertDialog.Builder(activity)
                builder.setTitle(R.string.update_status) // add a radio button list
                updatedStatus = arrayOf(
                    Constant.RECEIVED,
                    Constant.PROCESSED,
                    Constant.SHIPPED,
                    Constant.DELIVERED,
                    Constant.CANCELLED,
                    Constant.RETURNED
                )
                when (btnDeliveryStatus.text.toString()) {
                    Constant.RECEIVED -> checkedItem = 0
                    Constant.PROCESSED -> checkedItem = 1
                    Constant.SHIPPED -> checkedItem = 2
                    Constant.DELIVERED -> checkedItem = 3
                    Constant.CANCELLED -> checkedItem = 4
                    Constant.RETURNED -> checkedItem = 5
                }
                builder.setSingleChoiceItems(
                    updatedStatus,
                    checkedItem
                ) { dialog: DialogInterface?, which: Int ->
                    checkedItem = which
                    updatedStatus[0] = updatedStatus[which]
                }
                builder.setPositiveButton(R.string.ok) { dialog: DialogInterface?, which: Int ->
                    Constant.CLICK = true
                    val alertDialog = AlertDialog.Builder(
                        activity
                    )
                    // Setting Dialog Message
                    alertDialog.setMessage(R.string.change_order_status_msg)
                    alertDialog.setCancelable(false)
                    val alertDialog1 = alertDialog.create()

                    // Setting OK Button
                    alertDialog.setPositiveButton(R.string.yes) { dialog1: DialogInterface?, which1: Int ->
                        if (otp == "0") {
                            ChangeOrderStatus(
                                activity,
                                updatedStatus[0]!!.lowercase(Locale.getDefault())
                            )
                        } else {
                            if (checkedItem == 3) {
                                Confirm_OTP()
                            } else {
                                ChangeOrderStatus(
                                    activity,
                                    updatedStatus[0]!!.lowercase(Locale.getDefault())
                                )
                            }
                        }
                        btnDeliveryStatus.text = toTitleCase(updatedStatus[0])
                    }
                    alertDialog.setNegativeButton(R.string.no) { dialog13: DialogInterface?, which12: Int -> alertDialog1.dismiss() }
                    // Showing Alert Message
                    alertDialog.show()
                }
                builder.setNegativeButton(R.string.cancel, null) // create and show the alert dialog
                val dialog = builder.create()
                dialog.show()
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

    private fun ChangeOrderStatus(activity: Activity?, status: String) {
        if (itemIds != "") {
            if (isConnected(activity!!)) {
                val params: MutableMap<String, String?> = HashMap()
                params[Constant.BULK_STATUS_UPDATE] = Constant.GetVal
                params[Constant.ITEM_IDS] = itemIds
                params[Constant.STATUS] = status
                requestToVolley(object : VolleyCallback {
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
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    fun setSnackBar(activity: Activity?, message: String?, action: String?, color: Int) {
        val snackBar = Snackbar.make(
            activity!!.findViewById(android.R.id.content),
            message!!,
            Snackbar.LENGTH_INDEFINITE
        )
        snackBar.setAction(action) { view: View? ->
            getOrderData(orderList)
            snackBar.dismiss()
        }
        snackBar.setActionTextColor(color)
        val snackBarView = snackBar.view
        val textView = snackBarView.findViewById<TextView>(R.id.snackbar_text)
        textView.maxLines = 5
        snackBar.show()
    }
}