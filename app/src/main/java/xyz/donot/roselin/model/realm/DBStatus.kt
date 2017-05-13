package xyz.donot.quetzal.model.realm

import io.realm.RealmObject
import io.realm.annotations.RealmClass


@RealmClass
open class DBStatus : RealmObject() {
  open  var status: ByteArray? =null
}
