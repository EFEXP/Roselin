package xyz.donot.roselinx.view.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import com.squareup.picasso.Picasso
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_user.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.User
import xyz.donot.roselinx.R
import xyz.donot.roselinx.model.realm.DBCustomProfile
import xyz.donot.roselinx.model.realm.DBMute
import xyz.donot.roselinx.util.extraUtils.toast
import xyz.donot.roselinx.util.getSerialized
import xyz.donot.roselinx.util.getTwitterInstance
import xyz.donot.roselinx.view.adapter.UserTimeLineAdapter
import kotlin.properties.Delegates


class UserActivity : AppCompatActivity() {
	private var mUser: User by Delegates.notNull()
	private val realm by lazy { Realm.getDefaultInstance() }
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			findViewById<View>(android.R.id.content).systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
		}
		setContentView(R.layout.activity_user)
		if (intent.hasExtra("screen_name")) {
			launch(UI) {
				try {
					mUser = async(CommonPool) { getTwitterInstance().showUser(intent.getStringExtra("screen_name")) }.await()
					setUp(mUser)
				} catch (e: Exception) {
					toast(e.localizedMessage)
				}
			}
		} else {
			launch(UI) {
				try {
					mUser = async(CommonPool) { getTwitterInstance().showUser(intent.getLongExtra("user_id", 0L)) }.await()
					setUp(mUser)
				} catch (e: Exception) {
					toast(e.localizedMessage)
				}
			}

		}
	}

	private fun setUp(user_: User) {
		Picasso.with(applicationContext).load(user_.profileBannerIPadRetinaURL).into(banner)
		banner.setOnClickListener {
			startActivity(Intent(applicationContext, PictureActivity::class.java)
					.putStringArrayListExtra("picture_urls", arrayListOf(user_.profileBannerIPadRetinaURL)))
		}
		toolbar.apply {
			title = user_.screenName
		}
		val adapter = UserTimeLineAdapter(supportFragmentManager)
		adapter.user = user_
		viewpager_user.adapter = adapter
		viewpager_user.offscreenPageLimit = adapter.count
		tabs_user.setupWithViewPager(viewpager_user)
		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.setDisplayShowHomeEnabled(true)
	}

	override fun onDestroy() {
		super.onDestroy()
		realm.close()
	}

	override fun onSupportNavigateUp(): Boolean {
		onBackPressed()
		return super.onSupportNavigateUp()
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.mute -> {
				realm.executeTransaction {
					it.createObject(DBMute::class.java)
							.apply {
								id = mUser.id
								user = mUser.getSerialized()
							}
				}
				toast("ミュートしました")
			}
			R.id.change_name -> {
				val editText = EditText(this@UserActivity)
				AlertDialog.Builder(this@UserActivity)
						.setTitle("TLでの表示名を変更します")
						.setView(editText)
						.setPositiveButton("OK") { _, _ ->
							realm.executeTransaction {
								it.copyToRealmOrUpdate(
										DBCustomProfile().apply {
											id = mUser.id
											customname = editText.text.toString()
										}
								)
							}
							toast("変更しました")
						}
						.setNegativeButton("キャンセル") { _, _ -> }
						.show()
			}
			R.id.revert_name -> {
				realm.executeTransaction {
					it.where(DBCustomProfile::class.java).findAll().deleteAllFromRealm()
				}
				toast("戻しました")
			}
			else -> onBackPressed()
		}
		return super.onOptionsItemSelected(item)
	}


	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		val inflater = menuInflater
		inflater.inflate(R.menu.menu_user, menu)
		return true
	}

}
