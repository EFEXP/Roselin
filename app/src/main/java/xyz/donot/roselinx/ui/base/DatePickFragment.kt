package xyz.donot.roselinx.ui.base

import android.app.DatePickerDialog
import android.app.Dialog
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.DialogFragment
import xyz.donot.roselinx.ui.search.DateCompact
import xyz.donot.roselinx.ui.search.SearchSettingViewModel
import java.util.*

class DatePickFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
		val viewmodel=ViewModelProviders.of(activity).get(SearchSettingViewModel::class.java)
        //val minDate = Calendar.getInstance().apply {
        //    add(Calendar.DAY_OF_MONTH, -7)
        //   }
        val listener = DatePickerDialog.OnDateSetListener { _, year_, monthOfYear, dayOfMonth ->
                if (arguments.getBoolean("isFrom", false))
            { viewmodel.dayFrom.value= DateCompact(year_, monthOfYear + 1, dayOfMonth)
            }
            else{viewmodel.dayTo.value= DateCompact(year_, monthOfYear + 1, dayOfMonth)
                }
        }
        return DatePickerDialog(activity,listener,c.get(Calendar.YEAR),  c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH))
                .apply {
                    datePicker.maxDate = c.timeInMillis
                    //   datePicker.minDate=minDate.timeInMillis
                }
    }
}
