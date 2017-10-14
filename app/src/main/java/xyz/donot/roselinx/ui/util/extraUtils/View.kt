package xyz.donot.roselinx.ui.util.extraUtils
import android.view.View
import android.support.v4.app.Fragment as SupportFragment


fun View.show() {
  visibility = View.VISIBLE
}
fun View.hide() {
  visibility = View.GONE
}
fun View.hideSoftKeyboard() {
  context.getInputMethodManager().hideSoftInputFromWindow(this.windowToken, 0)
}

fun View.onClick(f: (View) -> Unit) = this.setOnClickListener(f)

