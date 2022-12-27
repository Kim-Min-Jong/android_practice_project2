package com.fc.trackingdelivery.data.entity

import com.google.gson.annotations.SerializedName

class ShippingCompanies {
    @SerializedName("Company", alternate = ["Recommend"])
    val shippingCompanies: List<ShippingCompany>? = null
}