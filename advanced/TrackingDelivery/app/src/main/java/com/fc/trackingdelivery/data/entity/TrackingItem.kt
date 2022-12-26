package com.fc.trackingdelivery.data.entity

import androidx.room.Embedded
import androidx.room.Entity

@Entity(primaryKeys = ["invoice", "code"])
data class TrackingItem(
    val invoice: String,
    @Embedded val company: ShippingCompany
)