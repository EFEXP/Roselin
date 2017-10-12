package xyz.donot.roselinx.ui.base

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData

open class ARecyclerViewModel(app:Application):AndroidViewModel(app){
    val recyclerScroll=MutableLiveData<Int>()
}
