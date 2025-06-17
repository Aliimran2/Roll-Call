import android.os.Parcel
import android.os.Parcelable
import com.google.android.material.datepicker.CalendarConstraints
import com.miassolutions.rollcall.utils.clearTimeComponents
import java.util.*

class SundayPastDateValidator() :
    CalendarConstraints.DateValidator {
    var isWeekendDisabled: Boolean = true


    override fun isValid(date: Long): Boolean {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = date
            clearTimeComponents()
        }

        val today = Calendar.getInstance().apply {
            clearTimeComponents()
        }

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val isWeekend = dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY
        val isFuture = calendar.after(today)

        return when {
            isWeekendDisabled -> !isWeekend && !isFuture // disable weekends and future dates
            else -> !isFuture // disable future date only
        }

    }


    constructor(parcel: Parcel) : this() {}

    override fun describeContents(): Int = 0
    override fun writeToParcel(dest: Parcel, flags: Int) {}
    override fun equals(other: Any?): Boolean {
        return other is SundayPastDateValidator
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }


    companion object CREATOR : Parcelable.Creator<SundayPastDateValidator> {
        override fun createFromParcel(parcel: Parcel): SundayPastDateValidator {
            return SundayPastDateValidator(parcel)
        }

        override fun newArray(size: Int): Array<SundayPastDateValidator?> {
            return arrayOfNulls(size)
        }
    }

}