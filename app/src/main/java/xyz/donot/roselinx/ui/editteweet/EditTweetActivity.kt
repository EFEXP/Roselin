package xyz.donot.roselinx.ui.editteweet

import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.widget.ArrayAdapter
import com.mlsdev.rximagepicker.RxImagePicker
import com.mlsdev.rximagepicker.Sources
import com.squareup.picasso.Picasso
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity
import kotlinx.android.synthetic.main.activity_tweet_edit.*
import kotlinx.android.synthetic.main.content_tweet_edit.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import xyz.donot.roselinx.R
import xyz.donot.roselinx.model.entity.RoselinDatabase
import xyz.donot.roselinx.ui.dialog.TrendFragment
import xyz.donot.roselinx.ui.util.extraUtils.bundle
import xyz.donot.roselinx.ui.util.extraUtils.newIntent
import xyz.donot.roselinx.ui.util.extraUtils.show
import xyz.donot.roselinx.ui.util.getAccount
import java.io.File
import java.util.*
import kotlin.properties.Delegates


class EditTweetActivity : AppCompatActivity() {
    var viewmodel by Delegates.notNull<EditTweetViewModel>()
    private var pair: Pair<Uri, Int>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tweet_edit)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        viewmodel = ViewModelProviders.of(this).get(EditTweetViewModel::class.java).apply {
            statusId = intent.getLongExtra("status_id", 0)
            draft.observe(this@EditTweetActivity, android.arch.lifecycle.Observer {
                it?.let {
                    editText_status.editableText.clear()
                    it.replyToScreenName.isNotEmpty()
                    if (!it.replyToScreenName.isBlank())
                        editText_status.append(
                                getString(R.string.at_screenname_blank,it.replyToScreenName)
                        )
                    if (it.replyToStatusId != 0L)
                        viewmodel.statusId = it.replyToStatusId
                    editText_status.append(it.text)
                }
            })
            hashtag.observe(this@EditTweetActivity, android.arch.lifecycle.Observer {
                it?.let {
                    editText_status.append(" $it")
                }
            })
            finish.observe(this@EditTweetActivity, android.arch.lifecycle.Observer {
                finish()
            })
        }
        //recycler
        val manager = LinearLayoutManager(this@EditTweetActivity).apply { orientation = LinearLayoutManager.HORIZONTAL }
        pic_recycler_view.apply {
            if (onFlingListener == null) LinearSnapHelper().attachToRecyclerView(pic_recycler_view)
            hasFixedSize()
            layoutManager = manager
            pic_recycler_view.adapter = viewmodel.mAdapter
        }

        //View#Set
        setUpSuggest()
        if (intent.hasExtra("user_screen_name")) {
            viewmodel.screenName = getString(R.string.at_screenname_blank,intent.getStringExtra("user_screen_name"))
        }
        editText_status.setText(viewmodel.screenName)
        editText_status.setSelection(editText_status.editableText.count())
        if (intent.hasExtra("status_txt")) {
            reply_for_status.text = intent.getStringExtra("status_txt")
            reply_for_status.show()
        }

        val user = getAccount().user
        Picasso.with(this).load(user.biggerProfileImageURLHttps).fit().into(iv_icon)
        editText_status_layout.hint = "@${user.screenName}からツイート"

        send_status.setOnClickListener {
            viewmodel.onSendClick(editText_status.text.toString())
        }
        viewmodel.mAdapter.setOnItemClickListener { _, _, position ->
            val item = viewmodel.mAdapter.getItem(position)
            val color = ContextCompat.getColor(this@EditTweetActivity, R.color.colorPrimary)
            AlertDialog.Builder(this@EditTweetActivity).setTitle(getString(R.string.title_activity_picture))
                    .setMessage("何をしますか？")
                    /*	.setNegativeButton("確認", { _, _ ->
                            start<PictureActivity>(bundle {
                                putStringArrayList("picture_urls", arrayListOf(mAdapter.data[position].toString()))
                            })
                        })*/
                    .setPositiveButton(getString(R.string.dialog_edit), { _, _ ->
                        pair = Pair(item!!, position)
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
                    }).setNeutralButton(getString(R.string.delete), { _, _ ->
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
                                editText_status.setText(viewmodel.getNowPlaying())
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


    private fun setUpSuggest() {
        launch(UI) {
            val screenname = async { RoselinDatabase.getInstance().userDataDao().getAll().map { "@" + it.screenname } }.await()
            val adapter = UserSuggestAdapter(this@EditTweetActivity, android.R.layout.simple_dropdown_item_1line, screenname)
            adapter.listener = object : CursorPositionListener {
                override fun currentCursorPosition() = editText_status.selectionStart
            }
            editText_status.setAdapter<ArrayAdapter<String>>(adapter)
        }


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == AppCompatActivity.RESULT_OK && data != null) {
            when (requestCode) {
                UCrop.REQUEST_CROP -> {
                    val resultUri = UCrop.getOutput(data)
                    viewmodel.mAdapter.setData(pair!!.second, resultUri!!)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    companion object {
        fun newIntent(context: Context,txt:String,statusId: Long,screenName:String):Intent{
            return context.newIntent<EditTweetActivity>(bundle {
                putString("status_txt",txt)
                putLong("status_id",statusId)
                putString("user_screen_name",screenName)
            })
        }
    }

    private fun addPhotos(uri: Uri) = viewmodel.mAdapter.addData(uri)


    override fun onBackPressed() {
        if (!editText_status.editableText.isBlank() && !editText_status.editableText.isEmpty()) {
            AlertDialog.Builder(this@EditTweetActivity)
                    .setTitle(getString(R.string.save))
                    .setMessage(getString(R.string.dialog_question_draft))
                    .setPositiveButton(getString(R.string.dialog_OK), { _, _ ->
                        viewmodel.saveDraft(editText_status.text.toString())
                        super.onBackPressed()
                    })
                    .setNeutralButton(getString(R.string.delete), { _, _ -> super.onBackPressed() })
                    .setNegativeButton(getString(R.string.dialog_cancel), { _, _ -> })
                    .show()
        } else {
            super.onBackPressed()
        }
    }
}

