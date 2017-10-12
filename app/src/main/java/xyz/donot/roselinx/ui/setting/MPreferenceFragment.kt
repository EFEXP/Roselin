package xyz.donot.roselinx.ui.setting

import android.os.Bundle
import android.preference.PreferenceFragment
import xyz.donot.roselinx.R

class MPreferenceFragment: PreferenceFragment(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)
    }

}
