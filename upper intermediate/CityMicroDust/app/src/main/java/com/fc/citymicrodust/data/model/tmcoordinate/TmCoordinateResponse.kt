package com.fc.citymicrodust.data.model.tmcoordinate


import com.google.gson.annotations.SerializedName

data class TmCoordinateResponse(
    @SerializedName("documents")
    val documents: List<Document?>?,
    @SerializedName("meta")
    val meta: Meta?
)