package xyz.donot.roselinx.view.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import com.mlsdev.rximagepicker.RxImagePicker
import com.mlsdev.rximagepicker.Sources
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_tweet_edit.*
import kotlinx.android.synthetic.main.content_tweet_edit.*
import twitter4j.StatusUpdate
import xyz.donot.roselinx.R
import xyz.donot.roselinx.model.realm.DBDraft
import xyz.donot.roselinx.service.TweetPostService
import xyz.donot.roselinx.util.extraUtils.defaultSharedPreferences
import xyz.donot.roselinx.util.extraUtils.newIntent
import xyz.donot.roselinx.util.extraUtils.show
import xyz.donot.roselinx.util.getMyId
import xyz.donot.roselinx.util.getPath
import xyz.donot.roselinx.util.getSerialized
import xyz.donot.roselinx.util.replace
import xyz.donot.roselinx.view.adapter.TwitterImageAdapter
import xyz.donot.roselinx.view.fragment.DraftFragment
import xyz.donot.roselinx.view.fragment.TrendFragment
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class TweetEditActivity : AppCompatActivity() {
	private var croppingUri: Uri? = null
	private val statusId by lazy { intent.getLongExtra("status_id", 0) }
	private val mAdapter = TwitterImageAdapter()
	private var screenName: String = ""
	private var dialog: DialogFragment? = null
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_tweet_edit)
		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.setDisplayShowHomeEnabled(true)
		tvTextCounter.setEditText(editText_status)
		tvTextCounter.setCharCountChangedListener { _, b ->
			if (b) {
				send_status.isEnabled = false
			}
		}
		val manager = LinearLayoutManager(this@TweetEditActivity).apply { orientation = LinearLayoutManager.HORIZONTAL }

		pic_recycler_view.apply {
			if (onFlingListener == null) LinearSnapHelper().attachToRecyclerView(pic_recycler_view)
			hasFixedSize()
			layoutManager = manager
			pic_recycler_view.adapter = mAdapter
		}

		//View#Set
		if (intent.getStringExtra("user_screen_name") != null) {
			screenName = "@${intent.getStringExtra("user_screen_name")}"
		}
		editText_status.setText(screenName)
		editText_status.setSelection(editText_status.editableText.count())
		if (intent.hasExtra("status_txt")) {
			reply_for_status.text = intent.getStringExtra("status_txt")
			reply_for_status.show()
		}
		send_status.setOnClickListener {
			val updateStatus = StatusUpdate(editText_status.text.toString())
			updateStatus.inReplyToStatusId = statusId
			val filePathList = ArrayList<String>()
			mAdapter.data.forEach { filePathList.add(getPath(this@TweetEditActivity, it)!!) }
			startService(newIntent<TweetPostService>()
					.putExtra("StatusUpdate", updateStatus.getSerialized())
					.putStringArrayListExtra("FilePath", filePathList))
			finish()
		}
		mAdapter.setOnItemClickListener { _, _, position ->
			val item = mAdapter.getItem(position)
			val color = ContextCompat.getColor(this@TweetEditActivity, R.color.colorPrimary)
			AlertDialog.Builder(this@TweetEditActivity).setTitle("写真")
					.setMessage("何をしますか？")
					/*	.setNegativeButton("確認", { _, _ ->
							start<PictureActivity>(Bundle {
								putStringArrayList("picture_urls", arrayListOf(mAdapter.data[position].toString()))
							})
						})*/
					.setPositiveButton("編集", { _, _ ->
						croppingUri = item
						UCrop.of(item!!, Uri.fromFile(File(cacheDir, "${Date().time}.jpg")))
								.withOptions(UCrop.Options().apply {
									setImageToCropBoundsAnimDuration(100)
									setFreeStyleCropEnabled(true)
									setToolbarColor(color)
									setActiveWidgetColor(color)
									setStatusBarColor(color)
									setAllowedGestures(UCropActivity.SCALE, UCropActivity.SCALE, UCropActivity.SCALE)
								})
								.start(this@TweetEditActivity)
					}).setNeutralButton("削除", { _, _ ->
				mAdapter.remove(position)
			})
					.show()

		}
		show_drafts.setOnClickListener {
			dialog = DraftFragment()
			dialog?.show(supportFragmentManager, "")
		}

		text_tools.setOnClickListener {
			val item = R.array.text_tools
			AlertDialog.Builder(this@TweetEditActivity)
					.setItems(item, { _, int ->
						val selectedItem = resources.getStringArray(item)[int]
						when (selectedItem) {
							"#NowPlaying" -> {
								val track = defaultSharedPreferences.getString("track", "")
								val artists = defaultSharedPreferences.getString("artist", "")
								val album = defaultSharedPreferences.getString("album", "")
								editText_status.setText("♪ $track /$album /$artists #NowPlaying")
							}
							"突然の死" -> {
								val i = editText_status.text.count() - 4
								var a = ""
								var b = ""
								for (v in 0..i) {
									a += "人"
									b += "^Y"
								}
								val text = "＿人人人人人人$a＿\n＞ ${editText_status.text} ＜\n￣Y^Y^Y^Y^Y$b￣"
								editText_status.text.clear()
								editText_status.setText(text)
							}
						}
					})
					.show()
		}
		trend_hashtag.setOnClickListener {
			dialog = TrendFragment()
			dialog?.show(supportFragmentManager, "")
		}
		use_camera.setOnClickListener {
			if (pic_recycler_view.layoutManager.itemCount < 4
					&& ContextCompat.checkSelfPermission(this@TweetEditActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
					== PackageManager.PERMISSION_GRANTED) {
				RxImagePicker.with(applicationContext).requestImage(Sources.CAMERA)
						.subscribe {
							it.let { addPhotos(it) }
						}
			}
		}
		add_picture.setOnClickListener {
			if (pic_recycler_view.layoutManager.itemCount < 4
					&& ContextCompat.checkSelfPermission(this@TweetEditActivity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
				RxImagePicker.with(applicationContext).requestImage(Sources.GALLERY)
						.subscribe({ addPhotos(it) }, { it.printStackTrace() })
			}
		}
	}

	fun changeDraft(draft: DBDraft) {
		editText_status.editableText.clear()
		editText_status.append(draft.text)
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		if (resultCode == AppCompatActivity.RESULT_OK && data != null) {
			when (requestCode) {
				UCrop.REQUEST_CROP -> {
					val resultUri = UCrop.getOutput(data)
					mAdapter.replace(croppingUri!!, resultUri!!)
				}
			}
		}


	}

	override fun onSupportNavigateUp(): Boolean {
		onBackPressed()
		return super.onSupportNavigateUp()
	}


	private fun addPhotos(uri: Uri) = mAdapter.addData(uri)
	fun addTrendHashtag(string: String) {
		editText_status.append(" $string")
		dialog?.dismiss()
		dialog = null
	}

	override fun onBackPressed() {
		if (!editText_status.editableText.isBlank() && !editText_status.editableText.isEmpty()) {
			AlertDialog.Builder(this@TweetEditActivity)
					.setTitle("戻る")
					.setMessage("下書きに保存しますか？")
					.setPositiveButton("はい", { _, _ ->
						Realm.getDefaultInstance().executeTransaction {
							it.createObject(DBDraft::class.java).apply {
								text = editText_status.text.toString()
								replyToScreenName = screenName
								replyToStatusId = statusId
								accountId = getMyId()
							}
						}
						super.onBackPressed()
					})
					.setNegativeButton("いいえ", { _, _ -> super.onBackPressed() })
					.show()
		} else {
			super.onBackPressed()
		}
	}
}
