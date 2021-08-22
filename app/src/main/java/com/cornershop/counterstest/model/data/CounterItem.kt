package com.cornershop.counterstest.model.data


import com.google.gson.annotations.SerializedName

data class Counter(
    @SerializedName("count")
    val count: Int = 0,
    @SerializedName("id")
    val id: String = "",
    @SerializedName("title")
    val title: String = ""
)

data class CounterTitle(
    @SerializedName("title")
    val title: String = ""
)

data class CounterId(
    @SerializedName("id")
    val id: String = ""
)
