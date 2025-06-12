package com.miassolutions.rollcall.ui.model

data class StatsUiModel(

    val date: String,
    val presentCount: Int,
    val totalCount: Int,

    ) {
    val percent: Int
        get() = if (totalCount == 0) 0 else (presentCount * 100) / totalCount
}