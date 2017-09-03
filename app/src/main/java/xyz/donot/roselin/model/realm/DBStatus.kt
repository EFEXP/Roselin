package xyz.donot.roselin.model.realm


import io.realm.RealmObject
import io.realm.annotations.Required



open class DBStatus : RealmObject() {
	@Required
  open  var status: ByteArray?=null
}
