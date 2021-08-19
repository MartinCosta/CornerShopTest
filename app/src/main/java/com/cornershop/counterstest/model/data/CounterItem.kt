package com.cornershop.counterstest.model.data


import com.google.gson.annotations.SerializedName

data class Counter(
    @SerializedName("count")
    val count: Int,
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String
)