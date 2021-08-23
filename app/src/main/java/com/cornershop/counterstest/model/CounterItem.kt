package com.cornershop.counterstest.model.data


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "counterItem")
data class Counter(
    @SerializedName("count")
    val count: Int = 0,
    @PrimaryKey
    @SerializedName("id")
    val id: String = "",
    @SerializedName("title")
    val title: String = "",
    var isSelectedForDelete: Boolean = false
)

data class CounterTitle(
    @SerializedName("title")
    val title: String = ""
)

data class CounterId(
    @SerializedName("id")
    val id: String = ""
)
