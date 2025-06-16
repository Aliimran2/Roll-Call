import android.os.Parcel
import android.os.Parcelable
import com.google.android.material.datepicker.CalendarConstraints
import java.util.*

class WeekdayPastDateValidator() : CalendarConstraints.DateValidator {

    override fun isValid(date: Long): Boolean {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val today = Calendar.getInstance().timeInMillis

        val isWeekend = dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY
        val isFuture = date > today

        return !isWeekend && !isFuture
    }

    override fun describeContents(): Int = 0
    override fun writeToParcel(dest: Parcel, flags: Int) {}


    companion object CREATOR : Parcelable.Creator<WeekdayPastDateValidator> {
        override fun createFromParcel(parcel: Parcel): WeekdayPastDateValidator {
            return WeekdayPastDateValidator()
        }

        override fun newArray(size: Int): Array<WeekdayPastDateValidator?> {
            return arrayOfNulls(size)
        }
    }
}