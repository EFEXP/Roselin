package xyz.donot.roselin.model.realm


import io.realm.RealmObject
import io.realm.annotations.RealmClass

/*
0=reply
1=retweet
2=favorite
 */
@RealmClass
open  class DBNotification : RealmObject() {
  open  var status: ByteArray? =null
  open  var type: Int =0
}
