package xyz.donot.roselin.model.realm


import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required



open class DBAccount : RealmObject() {
	@PrimaryKey open var id: Long = 0
	open var isMain: Boolean = false
	open var user: ByteArray? = null
	@Required
	open var twitter: ByteArray? = null
}
