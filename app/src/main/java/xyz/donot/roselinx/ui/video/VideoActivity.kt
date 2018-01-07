package xyz.donot.roselinx.ui.video

import android.app.DownloadManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import cn.jzvd.JZVideoPlayer
import cn.jzvd.JZVideoPlayerStandard
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_video.*
import xyz.donot.roselinx.R
import xyz.donot.roselinx.ui.util.extraUtils.getDownloadManager
import xyz.donot.roselinx.ui.util.extraUtils.logd
import xyz.klinker.android.drag_dismiss.activity.DragDismissActivity
import java.util.*


class VideoActivity : DragDismissActivity() {
    private val url by lazy { intent.getStringExtra("video_url") }
    private val thumbUrl by lazy { intent.getStringExtra("thumbUrl") }
    override fun onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return
        }
        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        JZVideoPlayer.releaseAllVideos()
    }

    override fun onCreateContent(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View =inflater.inflate(R.layout.activity_video,parent,false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        videoView.apply {
            setUp(url, JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, "Video")
            Picasso.with(this@VideoActivity)
                    .load(thumbUrl)
                    .into(thumbImageView)


        }
    }

    private fun saveVideo() {
        val request = DownloadManager.Request(Uri.parse(url))
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, Date().time.toString() + ".mp4")
        request.setTitle(resources.getString(R.string.downloading))
        request.setMimeType(getMimeType(url))
        logd { Environment.DIRECTORY_DOWNLOADS + "/" + Date().time.toString() + ".mp4" }
        getDownloadManager().enqueue(request)
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
