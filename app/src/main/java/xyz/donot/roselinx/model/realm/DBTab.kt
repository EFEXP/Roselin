package xyz.donot.roselinx.model.realm

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import io.realm.RealmObject
import xyz.donot.roselinx.R
import java.lang.Exception


fun Context.typeToIcon(type: Int): Drawable {
	val d = when (type) {
		HOME -> R.drawable.ic_home
		MENTION -> R.drawable.ic_reply
		SEARCH -> R.drawable.ic_search
		LIST -> R.drawable.ic_view_list
		NOTIFICATION -> R.drawable.ic_notifications
		TREND -> R.drawable.ic_trending
		DM -> R.drawable.ic_mail
        SETTING -> R.drawable.ic_settings_grey_400_36dp
		else -> throw IllegalStateException()
	}
	val d2 = DrawableCompat.wrap(ContextCompat.getDrawable(this, d))
	DrawableCompat.setTint(d2, ContextCompat.getColor(this, android.R.color.white))
	DrawableCompat.setTintMode(d2, PorterDuff.Mode.SRC_IN)
	return d2
}

const val HOME = 0
const val LIST = 1
const val MENTION = 2
const val NOTIFICATION = 3
const val TREND = 4
const val SEARCH = 5
const val DM = 6
const val SETTING = 7
fun toName(int: Int): String = when (int) {
	HOME -> "Home"
	LIST -> "List"
	MENTION -> "Reply"
	NOTIFICATION -> "Notification"
	SEARCH -> "Search"
	TREND -> "Trend"
    DM -> "DirectMessage"
    SETTING -> "Setting"
	else -> throw Exception()
}

fun toSimpleName(int: Int): String = when (int) {
	HOME -> "Home"
	LIST -> "List"
	MENTION -> "Reply"
	NOTIFICATION -> "Notice"
	SEARCH -> "Search"
	TREND -> "Trend"
	DM -> "DM"
    SETTING -> "Setting"
	else -> throw Exception()
}


open class TabDataObject : RealmObject() {
	open var type: Int = 0
	open var screenName: String? = null
	open var accountId: Long = 0L
	open var order: Int = 0
	open var listId: Long = 0L
	open var listName: String? = null
	open var searchQuery: ByteArray? = null
	open var searchWord: String? = null
}
