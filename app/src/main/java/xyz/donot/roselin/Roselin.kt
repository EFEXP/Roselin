package xyz.donot.roselin

import android.app.Application
import android.app.UiModeManager
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatDelegate
import com.crashlytics.android.Crashlytics
import com.twitter.sdk.android.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import io.fabric.sdk.android.Fabric
import io.realm.Realm
import io.realm.RealmConfiguration
import xyz.donot.roselin.model.realm.MyRealmMigration
import java.io.FileNotFoundException




class Roselin : Application() {
    private val TWITTER_KEY by lazy { getString(R.string.twitter_consumer_key) }
    private val TWITTER_SECRET by lazy {resources.getString(R.string.twitter_consumer_secret) }

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val authConfig = TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET)
        Fabric.with(this, Twitter(authConfig), Crashlytics())


        val config= RealmConfiguration.Builder().schemaVersion(0L)
                .migration(MyRealmMigration())
                .build()
        try {
            Realm.migrateRealm(config, MyRealmMigration())
        }
        catch(e: FileNotFoundException){}
        Realm.setDefaultConfiguration(config)


        val design=  when(PreferenceManager.getDefaultSharedPreferences(this).getString("night_mode","auto")){
            "black"->{ AppCompatDelegate.MODE_NIGHT_YES}
            "white"->{AppCompatDelegate.MODE_NIGHT_NO}
            "auto"->{AppCompatDelegate.MODE_NIGHT_AUTO}
            else->{AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM}
        }
        AppCompatDelegate.setDefaultNightMode(design)
        (getSystemService(UI_MODE_SERVICE)as UiModeManager).nightMode = UiModeManager.MODE_NIGHT_AUTO
    }

}
