package xyz.donot.roselinx

import android.app.Application
import android.app.UiModeManager
import android.support.text.emoji.EmojiCompat
import android.support.text.emoji.FontRequestEmojiCompatConfig
import android.support.v4.provider.FontRequest
import android.support.v7.app.AppCompatDelegate
import android.webkit.WebView
import com.google.android.gms.ads.MobileAds
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.TwitterConfig
import io.realm.Realm
import io.realm.RealmConfiguration
import xyz.donot.roselinx.util.Key.xxxxx
import xyz.donot.roselinx.util.Key.yyyyyy
import xyz.donot.roselinx.util.extraUtils.RoselinxConfig
import xyz.donot.roselinx.util.extraUtils.defaultSharedPreferences


class Roselin : Application() {
    private val TWITTER_KEY by lazy {
        //resources.getString(R.string.twitter_official_consumer_key)
        yyyyyy
    }
    private val TWITTER_SECRET by lazy {
        //resources.getString(R.string.twitter_official_consumer_secret)
        xxxxx
    }

    override fun onCreate() {
        super.onCreate()
        //Twitter
        val twitterConfig = TwitterConfig.Builder(this).twitterAuthConfig(TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET)).build()
        Twitter.initialize(twitterConfig)
        //realm
        Realm.init(this)
        val config = RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build()
        Realm.setDefaultConfiguration(config)
       RoselinxConfig.logEnabled = true
        /*
        val config = RealmConfiguration.Builder().schemaVersion(0L)
                .migration(MyRealmMigration())
                .build()
        try {
            Realm.migrateRealm(config, MyRealmMigration())
        } catch (e: Exception) {
        }
        Realm.setDefaultConfiguration(config)
        */
        //Font

        val fontRequest = FontRequest(
                "com.google.android.gms.fonts",
                "com.google.android.gms",
                "Noto Color Emoji Compat",
                R.array.com_google_android_gms_fonts_certs)
        val conf = FontRequestEmojiCompatConfig(this, fontRequest)
                .setReplaceAll(true)
        EmojiCompat.init(conf)
        //Ad
        MobileAds.initialize(this, getString(R.string.app_ad_id))
        //Delegate
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        WebView(applicationContext)
        val design = if (defaultSharedPreferences.getBoolean("night", true)) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(design)
        (getSystemService(UI_MODE_SERVICE) as UiModeManager).nightMode = UiModeManager.MODE_NIGHT_AUTO

    }


}
