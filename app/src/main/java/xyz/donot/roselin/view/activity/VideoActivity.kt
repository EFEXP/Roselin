package xyz.donot.roselin.view.activity

import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.MediaController
import kotlinx.android.synthetic.main.activity_video.*
import xyz.donot.roselin.R

class VideoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        val url = intent.getStringExtra("video_url")
        val mp = MediaController(this@VideoActivity)
        val   pDialog =  ProgressDialog(this@VideoActivity)
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
            setOnCompletionListener { videoView.seekTo(0)
                start()
            }
            start()

        }
    }
}