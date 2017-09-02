package xyz.donot.roselin.view.activity

import android.app.DownloadManager
import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.webkit.MimeTypeMap
import android.widget.MediaController
import kotlinx.android.synthetic.main.activity_video.*
import xyz.donot.roselin.R
import xyz.donot.roselin.util.extraUtils.getDownloadManager
import xyz.donot.roselin.util.extraUtils.logd
import java.util.*


class VideoActivity : AppCompatActivity() {
	private val url by lazy { intent.getStringExtra("video_url") }
	override fun onSupportNavigateUp(): Boolean {
		onBackPressed()
		return super.onSupportNavigateUp()
	}

	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.menu_videos, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		val id = item.itemId
		when (id) {
			R.id.save_it -> {

				SaveVideo()

			}
		}
		return super.onOptionsItemSelected(item)
	}

	private fun SaveVideo() {
		val request = DownloadManager.Request(Uri.parse(url))
		request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, Date().time.toString() + ".mp4")
		request.setTitle(resources.getString(R.string.downloading))
		request.setMimeType(getMimeType(url))
		logd { Environment.DIRECTORY_DOWNLOADS + "/" + Date().time.toString() + ".mp4" }
		getDownloadManager().enqueue(request)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_video)
		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.setDisplayShowHomeEnabled(true)
		val mp = MediaController(this@VideoActivity)
		val pDialog = ProgressDialog(this@VideoActivity)
		pDialog.setMessage("読み込み中" + "...")
		pDialog.isIndeterminate = true
		pDialog.setCancelable(false)
		pDialog.show()

		videoView.apply {
			setVideoURI(Uri.parse(url))
			setMediaController(mp)
			setOnPreparedListener {
				pDialog.dismiss()
			}
			setOnCompletionListener {
				videoView.seekTo(0)
				start()
			}
			start()

		}
	}


	private fun getMimeType(url: String): String? {
		var type: String? = null
		val extension = MimeTypeMap.getFileExtensionFromUrl(url)
		if (extension != null) {
			type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
		}
		return type
	}

}