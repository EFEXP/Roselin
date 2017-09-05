package xyz.donot.roselinx.util.extraUtils

import android.content.res.Resources
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.support.v4.app.Fragment as SupportFragment



fun View.show() {
  visibility = View.VISIBLE
}
fun View.hide() {
  visibility = View.GONE
}




val View.dm: DisplayMetrics
  get() = resources.displayMetrics


fun View.hideSoftKeyboard() {
  context.getInputMethodManager().hideSoftInputFromWindow(this.windowToken, 0)
}

fun View.onClick(f: (View) -> Unit) = this.setOnClickListener(f)

abstract class KoiTextWatcher : TextWatcher {
  override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = Unit

  override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) = Unit

  override fun afterTextChanged(s: Editable) = Unit
}

abstract class KoiSeekBarChangeListener : SeekBar.OnSeekBarChangeListener {
  override fun onStopTrackingTouch(seekBar: SeekBar) = Unit

  override fun onStartTrackingTouch(seekBar: SeekBar) = Unit

  override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) = Unit
}
