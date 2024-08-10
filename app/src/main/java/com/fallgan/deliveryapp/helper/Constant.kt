package com.fallgan.deliveryapp.helper

import java.text.DecimalFormat

object Constant {
    //MODIFICATION PART

    //Admin panel url with it would be necessary to put "/"(slash) at end of the url (https://admin.panel.url/)
    private const val BASE_URL = "https://fallgan.graymatterworks.com/"

    //set your jwt secret key here...key must same in PHP and Android
    const val JWT_KEY = "replace_with_your_strong_jwt_secret_key"

    var LANGUAGE: String = "en"

    //MODIFICATION PART END
    private const val SUB_URL = "delivery-boy/"
    const val MAIN_URL = BASE_URL + SUB_URL + "api/api-v1.php"
    const val DELIVERY_BOY_POLICY = BASE_URL + "delivery-boy-play-store-privacy-policy.php"
    const val DELIVERY_BOY_TERMS = BASE_URL + "delivery-boy-play-store-terms-conditions.php"
    const val AccessKey = "accesskey"
    const val AccessKeyVal = "90336"
    const val GetVal = "1"
    const val LOGIN = "login"
    const val GET_DELIVERY_BOY_BY_ID = "get_delivery_boy_by_id"
    const val UPDATE_DELIVERY_BOY_PROFILE = "update_delivery_boy_profile"
    const val BULK_STATUS_UPDATE = "bulk_status_update"
    const val GET_WITHDRAWAL_REQUEST = "get_withdrawal_requests"
    const val SEND_WITHDRAWAL_REQUEST = "send_withdrawal_request"
    const val TYPE = "type"
    const val DELIVERY_BOY = "delivery_boy"
    const val DELIVERY_BOY_FORGOT_PASSWORD = "delivery_boy_forgot_password"
    const val GET_NOTIFICATION = "get_notifications"
    const val CHECK_DELIVERY_BOY_BY_MOBILE = "check_delivery_boy_by_mobile"
    const val ID = "id"
    var filterValues = arrayOf<CharSequence>("Show Wallet Transactions", "Show Wallet Requests")
    const val WITHDRAWAL_REQUEST = "withdrawal_requests"
    const val FUND_TRANSFERS = "fund_transfers"
    const val TYPE_ID = "type_id"
    const val DELIVERY_BOY_ID = "delivery_boy_id"
    const val GET_ORDERS = "get_orders"
    const val CHANGE_AVAILABILITY = "change_availability"
    const val IS_AVAILABLE = "is_available"
    const val UPDATE_DELIVERY_BOY_FCM_ID = "update_delivery_boy_fcm_id"
    const val ITEM_IDS = "item_ids"
    const val NAME = "name"
    const val MOBILE = "mobile"
    const val PASSWORD = "password"
    const val ADDRESS = "address"
    const val BONUS = "bonus"
    const val BALANCE = "balance"
    const val CURRENCY = "currency"
    const val UPDATED_BALANCE = "updated_balance"
    const val STATUS = "status"
    const val CREATED_AT = "date_created"
    const val DATA = "data"
    const val DATA_TYPE = "data_type"
    const val TOTAL = "total"
    const val MESSAGE = "message"
    const val AMOUNT = "amount"
    const val FROM = "from"
    const val ORDER_ITEM_ID = "order_item_id"
    const val POSITION = "position"
    const val ITEM = "item"
    const val IS_USER_LOGIN = "IsUserLoggedIn"
    const val FCM_ID = "fcm_id"
    const val AWAITING_PAYMENT = "awaiting_payment"
    const val OFFSET = "offset"
    const val LIMIT = "limit"
    const val RECEIVED = "Received"
    const val PROCESSED = "Processed"
    const val SHIPPED = "Shipped"
    const val DELIVERED = "Delivered"
    const val CANCELLED = "Cancelled"
    const val RETURNED = "Returned"
    const val SHOW = "show"
    const val HIDE = "hide"
    const val ERROR = "error"
    const val LOAD_ITEM_LIMIT = 10
    var formatter = DecimalFormat("0.00")
    var PRODUCT_LOAD_LIMIT = "10"
    var CLICK = false
}