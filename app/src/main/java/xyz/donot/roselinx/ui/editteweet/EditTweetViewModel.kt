package xyz.donot.roselinx.ui.editteweet

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import twitter4j.StatusUpdate
import xyz.donot.roselinx.Roselin
import xyz.donot.roselinx.model.entity.TweetDraft
import xyz.donot.roselinx.service.TweetPostService
import xyz.donot.roselinx.ui.util.extraUtils.defaultSharedPreferences
import xyz.donot.roselinx.ui.util.getAccount
import xyz.donot.roselinx.ui.util.getPath
import xyz.donot.roselinx.ui.view.SingleLiveEvent
import kotlin.properties.Delegates

class EditTweetViewModel(application: Application) : AndroidViewModel(application) {
    val draft: MutableLiveData<TweetDraft> = MutableLiveData()
    val hashtag: MutableLiveData<String> = MutableLiveData()
    val finish: SingleLiveEvent<Unit> = SingleLiveEvent()
    var statusId by Delegates.notNull<Long>()
    val mAdapter = TwitterImageAdapter()
    var screenName: String = ""

    fun getNowPlaying():String{
      val pref=  getApplication<Roselin>(). defaultSharedPreferences
        val track = pref.getString("track", "")
        val artists = pref.getString("artist", "")
        val album = pref.getString("album", "")
        return "♪ $track /$album /$artists #NowPlaying"
    }

    fun onSendClick(string: String) {
        if (string.codePointCount(0, string.length)<= 140){
            val app = getApplication<Roselin>()
            val updateStatus = StatusUpdate(string)
            updateStatus.inReplyToStatusId = statusId
            val filePathList = ArrayList<String>()
            mAdapter.data.forEach { filePathList.add(getPath(app, it)!!) }
            app.startService(TweetPostService.getIntent(app,filePathList,updateStatus))
            finish.call() }
    }

    fun saveDraft(string: String) {
        TweetDraft.save(TweetDraft(getAccount().id,string,statusId,screenName))
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