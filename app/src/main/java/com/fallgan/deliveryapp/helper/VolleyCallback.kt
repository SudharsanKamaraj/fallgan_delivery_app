package com.fallgan.deliveryapp.helper

interface VolleyCallback {
    fun onSuccess(
        result: Boolean,
        response: String?
    )
}