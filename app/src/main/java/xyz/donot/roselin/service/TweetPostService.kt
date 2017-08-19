package xyz.donot.roselin.service

import android.app.IntentService
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import twitter4j.StatusUpdate
import xyz.donot.roselin.R
import xyz.donot.roselin.util.getDeserialized
import xyz.donot.roselin.util.getTwitterInstance
import java.io.File
import java.util.*

class TweetPostService : IntentService("TweetPostService") {
    val twitter by lazy { getTwitterInstance() }
    override fun onHandleIntent(intent: Intent) {
        val filePath: ArrayList<String>
        val id=Random().nextInt(100)+1
        if(intent.hasExtra("StatusUpdate")){
            val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val updateStatus= intent.getByteArrayExtra("StatusUpdate").getDeserialized<StatusUpdate>()
            if(intent.hasExtra("FilePath")){
               filePath=intent.getStringArrayListExtra("FilePath")
                notificate(id)
                val uploadedMediaId = filePath.map {  twitter.uploadMedia(File(it)).mediaId }
                val array = LongArray(uploadedMediaId.size)
                var i=0
                while (i < uploadedMediaId.size) {
                    array[i] = uploadedMediaId[i]
                    i++
                }
                updateStatus.setMediaIds(*array)
            }
          try {
              twitter.updateStatus(updateStatus)
          }
            catch(e:Exception){
                mNotificationManager.cancel(id)
            }
          mNotificationManager.cancel(id)

        }
    }

    private fun notificate(int:Int) {
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mNotification = NotificationCompat.Builder(this,"Sending")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("送信中")
                .setProgress(100,100,true)
                .setContentText("ツイートを送信中…")
                .build()
        mNotificationManager.notify(int, mNotification )

    }
}