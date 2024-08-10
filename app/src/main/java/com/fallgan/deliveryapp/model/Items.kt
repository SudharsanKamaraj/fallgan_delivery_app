package com.fallgan.deliveryapp.model

import java.util.ArrayList
import com.fallgan.deliveryapp.model.Items
import java.io.Serializable

class Items : Serializable {
    var id: String? = null
    var quantity: String? = null
    var price: String? = null
    var discounted_price: String? = null
    var tax_amount: String? = null
    var tax_percentage: String? = null
    var sub_total: String? = null
    var active_status: String? = null
    var name: String? = null
    var image: String? = null
    var measurement: String? = null
    var unit: String? = null
}