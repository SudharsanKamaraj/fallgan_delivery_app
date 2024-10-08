package com.fallgan.deliveryapp.model

import java.util.ArrayList
import com.fallgan.deliveryapp.model.Items
import java.io.Serializable

class WalletHistory : Serializable {
    var id: String? = null
    var name: String? = null
    var mobile: String? = null
    var address: String? = null
    var delivery_boy_id: String? = null
    var type: String? = null
    var opening_balance: String? = null
    var amount: String? = null
    var closing_balance: String? = null
    var status: String? = null
    var message: String? = null
    var date_created: String? = null
    var last_updated: String? = null
}