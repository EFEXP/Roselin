package xyz.donot.roselinx.model.realm


import io.realm.RealmObject
import io.realm.annotations.Required



open class DBStatus : RealmObject() {
	@Required
  open  var status: ByteArray?=null
}
