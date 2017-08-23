package xyz.donot.roselin

import android.app.Application
import android.app.UiModeManager
import android.os.StrictMode
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatDelegate
import com.crashlytics.android.Crashlytics
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.TwitterConfig
import io.fabric.sdk.android.Fabric
import io.realm.Realm
import io.realm.RealmConfiguration
import xyz.donot.roselin.model.realm.MyRealmMigration
import java.io.FileNotFoundException



class Roselin : Application() {
    private val TWITTER_KEY by lazy {
      //  resources.getString(R.string.twitter_official_consumer_key)
      getString(R.string.twitter_consumer_key)
    }
    private val TWITTER_SECRET by lazy {
      //  resources.getString(R.string.twitter_official_consumer_secret)
      resources.getString(R.string.twitter_consumer_secret)
    }

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        Realm.init(this)
        val twitterConfig = TwitterConfig.Builder(this)
                .twitterAuthConfig(TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET))
                .debug(true).build()
        Twitter.initialize(twitterConfig)
        Fabric.with(this, Crashlytics())


        val config= RealmConfiguration.Builder().schemaVersion(0L)
                .migration(MyRealmMigration())
                .build()
        try {
            Realm.migrateRealm(config, MyRealmMigration())
        }
        catch(e: FileNotFoundException){}
        Realm.setDefaultConfiguration(config)

        val design= if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("night",true)){ AppCompatDelegate.MODE_NIGHT_YES}
        else{AppCompatDelegate.MODE_NIGHT_NO}

        AppCompatDelegate.setDefaultNightMode(design)
        (getSystemService(UI_MODE_SERVICE)as UiModeManager).nightMode = UiModeManager.MODE_NIGHT_AUTO

        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build())

    }


}
