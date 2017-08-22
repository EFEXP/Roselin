package xyz.donot.roselin.view

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import xyz.donot.roselin.R

class MPreferenceFragment:PreferenceFragmentCompat(){
    override fun onCreatePreferences(p0: Bundle?,rootKey: String?) = setPreferencesFromResource(R.xml.preferences, rootKey)
}
