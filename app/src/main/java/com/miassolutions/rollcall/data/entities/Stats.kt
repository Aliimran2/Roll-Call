package com.miassolutions.rollcall.data.entities

data class Stats(val id : Int, val date : String, val present : Int, val absent : Int, val total : Int, val percentage : Double)