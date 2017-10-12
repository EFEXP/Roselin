package xyz.donot.roselinx.model.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import xyz.donot.roselinx.R
import xyz.donot.roselinx.ui.util.diff.Distinguishable
import java.lang.Exception


const val HOME = 0
const val LIST = 1
const val MENTION = 2
const val NOTIFICATION = 3
const val TREND = 4
const val SEARCH = 5
const val DM = 6
const val SETTING = 7
@Entity(tableName = "saved_tab")
data class SavedTab(
        val type: Int,
        val screenName: String? = null,
        val accountId: Long = 0L,
        var tabOrder: Int = 0,
        val listId: Long = 0L,
        val listName: String? = null,
        var searchQuery: twitter4j.Query? = null,
        val searchWord: String? = null
) : Distinguishable {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
    override fun isTheSame(other:Distinguishable) = id == (other as? SavedTab)?.id
    companion object {
        fun save(tab: SavedTab) = launch(UI) {
            async {
                val maxOrder=RoselinDatabase.getInstance().savedTabDao().maxOrder()
                tab.tabOrder=maxOrder+1
                RoselinDatabase.getInstance().savedTabDao().insertSavedTab(tab) }.await()
        }
    }
}



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
