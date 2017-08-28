package xyz.donot.roselin.view.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import xyz.donot.roselin.view.activity.SearchSettingActivity
import java.util.*

class DatePickFragment: DialogFragment()
{
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val  c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val parentActivity= activity
        //val minDate = Calendar.getInstance().apply {
        //    add(Calendar.DAY_OF_MONTH, -7)
     //   }


        val listener= DatePickerDialog.OnDateSetListener { view, y, monthOfYear, dayOfMonth ->
            if(parentActivity is SearchSettingActivity) {
                parentActivity.dateSet(y, monthOfYear+1, dayOfMonth,arguments.getBoolean("isFrom",false))
            }

        }

        return DatePickerDialog(activity, listener,  year, month, day)
        .apply {
            datePicker.maxDate= c.timeInMillis
         //   datePicker.minDate=minDate.timeInMillis
        }



    }
}
