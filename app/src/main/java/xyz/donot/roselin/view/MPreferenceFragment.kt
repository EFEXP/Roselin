package xyz.donot.roselin.view

import android.os.Bundle
import android.preference.PreferenceFragment
import xyz.donot.roselin.R

class MPreferenceFragment: PreferenceFragment(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)
    }

}
