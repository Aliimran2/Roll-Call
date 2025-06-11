package com.miassolutions.rollcall.ui.dataclasses

interface CommonListItem

data class Dashboard(val image: Int, val title: String) : CommonListItem
data class TopCard(val title: String, val subTitle: String) : CommonListItem