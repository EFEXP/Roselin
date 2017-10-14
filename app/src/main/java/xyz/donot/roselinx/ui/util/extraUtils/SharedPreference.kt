package xyz.donot.roselinx.ui.util.extraUtils
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

val Context.defaultSharedPreferences: SharedPreferences
  get() = PreferenceManager.getDefaultSharedPreferences(this)

fun SharedPreferences.clear() = apply(getEditor().clear())

fun SharedPreferences.putBoolean(key: String, value: Boolean) =
        apply(getEditor().putBoolean(key, value))

fun SharedPreferences.putFloat(key: String, value: Float) = apply(getEditor().putFloat(key, value))

fun SharedPreferences.putInt(key: String, value: Int) = apply(getEditor().putInt(key, value))

fun SharedPreferences.putLong(key: String, value: Long) = apply(getEditor().putLong(key, value))

fun SharedPreferences.putString(key: String, value: String?) =
        apply(getEditor().putString(key, value))

fun SharedPreferences.putStringSet(key: String, values: Set<String>?) =
        apply(getEditor().putStringSet(key, values))

fun SharedPreferences.remove(key: String) = apply(getEditor().remove(key))



fun SharedPreferences.applyBulk(): SharedPreferences {
  this.bulkEditor?.apply()
  return this
}

fun SharedPreferences.discardBulk(): SharedPreferences {
  this.bulkEditor = null
  return this
}

/*
 * -----------------------------------------------------------------------------
 *  Private fields
 * -----------------------------------------------------------------------------
 */
private var SharedPreferences.bulkEditor: SharedPreferences.Editor?
  get() = this.bulkEditor

    set(editor) {
    this.bulkEditor = editor
  }

/*
 * -----------------------------------------------------------------------------
 *  Private methods
 * -----------------------------------------------------------------------------
 */
private fun SharedPreferences.getEditor(): SharedPreferences.Editor = this.edit()

private fun apply(editor: SharedPreferences.Editor) = editor.apply()