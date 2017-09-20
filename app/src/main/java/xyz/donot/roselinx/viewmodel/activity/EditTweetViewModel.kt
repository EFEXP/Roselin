package xyz.donot.roselinx.viewmodel.activity

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import io.realm.Realm
import twitter4j.StatusUpdate
import xyz.donot.roselinx.Roselin
import xyz.donot.roselinx.model.realm.DBDraft
import xyz.donot.roselinx.service.TweetPostService
import xyz.donot.roselinx.util.extraUtils.newIntent
import xyz.donot.roselinx.util.getMyId
import xyz.donot.roselinx.util.getPath
import xyz.donot.roselinx.util.getSerialized
import xyz.donot.roselinx.view.adapter.TwitterImageAdapter
import xyz.donot.roselinx.view.custom.SingleLiveEvent
import kotlin.properties.Delegates

class EditTweetViewModel(application: Application) : AndroidViewModel(application) {
    val draft: MutableLiveData<String> = MutableLiveData()
    val hashtag: MutableLiveData<String> = MutableLiveData()
    val finish: SingleLiveEvent<Unit> = SingleLiveEvent()
    var statusId by Delegates.notNull<Long>()
    val mAdapter = TwitterImageAdapter()
    var screenName: String = ""

    fun onSendClick(string: String) {
        if (string.codePointCount(0, string.length)<= 140){
            val app = getApplication<Roselin>()
            val updateStatus = StatusUpdate(string)
            updateStatus.inReplyToStatusId = statusId
            val filePathList = ArrayList<String>()
            mAdapter.data.forEach { filePathList.add(getPath(app, it)!!) }
            app.startService(app.newIntent<TweetPostService>()
                    .putExtra("StatusUpdate", updateStatus.getSerialized())
                    .putStringArrayListExtra("FilePath", filePathList))
            finish.call() }
    }

    fun saveDraft(string: String) {
        Realm.getDefaultInstance().executeTransaction {
            it.createObject(DBDraft::class.java).apply {
                text = string
                replyToScreenName = screenName
                replyToStatusId = statusId
                accountId = getMyId()
            }
        }

    }

    fun suddenDeath(string: String): String {
        val i = string.codePointCount(0, string.length) - 4
        var a = ""
        var b = ""
        for (v in 0..i) {
            a += "人"
            b += "^Y"
        }
        return "＿人人人人人$a＿\n＞ $string ＜\n￣Y^Y^Y^Y^Y$b￣"
    }

    fun tanzaku(string: String): String {
        var text = "┏┻┓\n┃　┃\n"
        string.forEach {
            text += "┃$it┃\n"
        }
        text += "┃　┃\n┗━┛"
        return text
    }


}