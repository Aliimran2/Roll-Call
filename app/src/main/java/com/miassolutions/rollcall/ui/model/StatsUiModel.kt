package com.miassolutions.rollcall.ui.model

import java.time.LocalDate

data class StatsUiModel(

    val date: LocalDate,
    val presentCount: Int,
    val totalCount: Int,

    ) {
    val percent: Int
        get() = if (totalCount == 0) 0 else (presentCount * 100) / totalCount
}