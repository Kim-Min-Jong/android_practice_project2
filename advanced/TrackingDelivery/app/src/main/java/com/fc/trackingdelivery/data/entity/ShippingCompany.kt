package com.fc.trackingdelivery.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class ShippingCompany(
    @PrimaryKey
    @SerializedName("Code")
    val code: String,
    @SerializedName("Name")
    val name: String
)
