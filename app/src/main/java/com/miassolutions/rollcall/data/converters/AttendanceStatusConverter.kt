package com.miassolutions.rollcall.data.converters

import androidx.room.TypeConverter
import com.miassolutions.rollcall.utils.AttendanceStatus

class AttendanceStatusConverter {

    @TypeConverter
    fun fromStatus(status: AttendanceStatus): String {
        return status.name
    }

    @TypeConverter
    fun toStatus(value: String): AttendanceStatus {
        return AttendanceStatus.valueOf(value)
    }
}