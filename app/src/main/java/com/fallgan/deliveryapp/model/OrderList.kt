package com.fallgan.deliveryapp.model

import java.util.ArrayList
import com.fallgan.deliveryapp.model.Items
import java.io.Serializable

class OrderList : Serializable {
    var id: String? = null
    var otp: String? = null
    var mobile: String? = null
    var order_note: String? = null
    var total: String? = null
    var delivery_charge: String? = null
    var wallet_balance: String? = null
    var discount: String? = null
    var promo_discount: String? = null
    var final_total: String? = null
    var payment_method: String? = null
    var address: String? = null
    var latitude: String? = null
    var longitude: String? = null
    var delivery_time: String? = null
    var date_added: String? = null
    var seller_mobile: String? = null
    var user_name: String? = null
    var seller_name: String? = null
    var seller_address: String? = null
    var seller_latitude: String? = null
    var seller_longitude: String? = null
    var items: ArrayList<Items>? = null
}