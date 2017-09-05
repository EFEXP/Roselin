package xyz.donot.roselinx

import android.app.Application
import android.app.UiModeManager
import android.preference.PreferenceManager
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


class Roselin : Application() {
	private val TWITTER_KEY by lazy {
		//     resources.getString(R.string.twitter_official_consumer_key)
		getString(R.string.twitter_consumer_key)
	}
	private val TWITTER_SECRET by lazy {
		//   resources.getString(R.string.twitter_official_consumer_secret)
		resources.getString(R.string.twitter_consumer_secret)
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
		val conf = FontRequestEmojiCompatConfig(this@Roselin, fontRequest)
				.setReplaceAll(true)
		EmojiCompat.init(conf)
		//Ad
		MobileAds.initialize(this, getString(R.string.app_ad_id))
		//Delegate
		AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
		WebView(applicationContext)
		val design = if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("night", true)) {
			AppCompatDelegate.MODE_NIGHT_YES
		} else {
			AppCompatDelegate.MODE_NIGHT_NO
		}
		AppCompatDelegate.setDefaultNightMode(design)
		(getSystemService(UI_MODE_SERVICE) as UiModeManager).nightMode = UiModeManager.MODE_NIGHT_AUTO


	}


}
