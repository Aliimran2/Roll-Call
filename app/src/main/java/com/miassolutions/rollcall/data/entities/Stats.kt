package com.miassolutions.rollcall.data.entities

data class Stats(

    val date: String,
    val presentCount: Int,
    val totalCount: Int,

    ) {
    val percent: Int
        get() = if (totalCount == 0) 0 else (presentCount * 100) / totalCount
}