package xyz.donot.roselinx.view.activity

import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import com.mlsdev.rximagepicker.RxImagePicker
import com.mlsdev.rximagepicker.Sources
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity
import kotlinx.android.synthetic.main.activity_tweet_edit.*
import kotlinx.android.synthetic.main.content_tweet_edit.*
import xyz.donot.roselinx.R
import xyz.donot.roselinx.util.extraUtils.defaultSharedPreferences
import xyz.donot.roselinx.util.extraUtils.show
import xyz.donot.roselinx.view.fragment.DraftFragment
import xyz.donot.roselinx.view.fragment.TrendFragment
import xyz.donot.roselinx.viewmodel.EditTweetViewModel
import java.io.File
import java.util.*
import kotlin.properties.Delegates


class EditTweetActivity : AppCompatActivity() {

    var viewmodel by Delegates.notNull<EditTweetViewModel>()
    private var pair:Pair<Uri,Int>?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tweet_edit)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        viewmodel= ViewModelProviders.of(this).get(EditTweetViewModel::class.java).apply {

            statusId= intent.getLongExtra("status_id", 0)
            draft.observe(this@EditTweetActivity,android.arch.lifecycle.Observer{
                it?.let {
                    editText_status.editableText.clear()
                    editText_status.append(it)
                }
            })
            hashtag.observe(this@EditTweetActivity,android.arch.lifecycle.Observer{
                it?.let {
                    editText_status.append(" $it")
                }
            })
            finish.observe(this@EditTweetActivity,android.arch.lifecycle.Observer {
                finish()
            })
        }

        val manager = LinearLayoutManager(this@EditTweetActivity).apply { orientation = LinearLayoutManager.HORIZONTAL }

        pic_recycler_view.apply {
            if (onFlingListener == null) LinearSnapHelper().attachToRecyclerView(pic_recycler_view)
            hasFixedSize()
            layoutManager = manager
            pic_recycler_view.adapter =viewmodel. mAdapter
        }

        //View#Set
        if (intent.hasExtra("user_screen_name")) {
           viewmodel. screenName = "@${intent.getStringExtra("user_screen_name")} "
        }
        editText_status.setText( viewmodel. screenName)
        editText_status.setSelection(editText_status.editableText.count())
        if (intent.hasExtra("status_txt")) {
            reply_for_status.text = intent.getStringExtra("status_txt")
            reply_for_status.show()
        }
        send_status.setOnClickListener {
           viewmodel.onSendClick(editText_status.text.toString())
        }
       viewmodel.mAdapter.setOnItemClickListener { _, _, position ->
            val item =  viewmodel.mAdapter.getItem(position)
            val color = ContextCompat.getColor(this@EditTweetActivity, R.color.colorPrimary)
            AlertDialog.Builder(this@EditTweetActivity).setTitle("写真")
                    .setMessage("何をしますか？")
                    /*	.setNegativeButton("確認", { _, _ ->
                            start<PictureActivity>(Bundle {
                                putStringArrayList("picture_urls", arrayListOf(mAdapter.data[position].toString()))
                            })
                        })*/
                    .setPositiveButton("編集", { _, _ ->
                        pair =Pair(item!!,position)
                                UCrop.of(item, Uri.fromFile(File(cacheDir, "${Date().time}.jpg")))
                                .withOptions(UCrop.Options().apply {
                                    setImageToCropBoundsAnimDuration(100)
                                    setFreeStyleCropEnabled(true)
                                    setToolbarColor(color)
                                    setActiveWidgetColor(color)
                                    setStatusBarColor(color)
                                    setAllowedGestures(UCropActivity.SCALE, UCropActivity.SCALE, UCropActivity.SCALE)
                                })
                                .start(this@EditTweetActivity)
                    }).setNeutralButton("削除", { _, _ ->
                viewmodel.mAdapter.remove(position)
            })
                    .show()

        }
        show_drafts.setOnClickListener {
           DraftFragment().show(supportFragmentManager, "")
        }

        text_tools.setOnClickListener {
            val item = R.array.text_tools
            AlertDialog.Builder(this@EditTweetActivity)
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
                                editText_status.setText(viewmodel.suddenDeath(editText_status.text.toString()))
                            }
                            "縦書き" -> {
                                editText_status.setText(viewmodel.tanzaku(editText_status.text.toString()))
                            }
                        }
                    })
                    .show()
        }
        trend_hashtag.setOnClickListener {
          TrendFragment().show(supportFragmentManager, "")
        }
        use_camera.setOnClickListener {
            if (pic_recycler_view.layoutManager.itemCount < 4
                    && ContextCompat.checkSelfPermission(this@EditTweetActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                RxImagePicker.with(applicationContext).requestImage(Sources.CAMERA)
                        .subscribe {
                            it.let { addPhotos(it) }
                        }
            }
        }
        add_picture.setOnClickListener {
            if (pic_recycler_view.layoutManager.itemCount < 4
                    && ContextCompat.checkSelfPermission(this@EditTweetActivity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                RxImagePicker.with(applicationContext).requestImage(Sources.GALLERY)
                        .subscribe({ addPhotos(it) }, { it.printStackTrace() })
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == AppCompatActivity.RESULT_OK && data != null) {
            when (requestCode) {
                UCrop.REQUEST_CROP -> {
                    val resultUri = UCrop.getOutput(data)
                    viewmodel.  mAdapter.setData(pair!!.second,resultUri!!)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }


    private fun addPhotos(uri: Uri) =  viewmodel.mAdapter.addData(uri)


    override fun onBackPressed() {
        if (!editText_status.editableText.isBlank() && !editText_status.editableText.isEmpty()) {
            AlertDialog.Builder(this@EditTweetActivity)
                    .setTitle("戻る")
                    .setMessage("下書きに保存しますか？")
                    .setPositiveButton("はい", { _, _ ->
                        viewmodel.saveDraft(editText_status.text.toString())
                        super.onBackPressed()
                    })
                    .setNeutralButton("削除", { _, _ -> super.onBackPressed() })
                    .setNegativeButton("キャンセル", { _, _ -> })
                    .show()
        } else {
            super.onBackPressed()
        }
    }
}

