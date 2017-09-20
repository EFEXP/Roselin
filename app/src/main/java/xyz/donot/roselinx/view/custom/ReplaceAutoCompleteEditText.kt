package xyz.donot.roselinx.view.custom

import android.content.Context
import android.util.AttributeSet
import android.widget.AutoCompleteTextView
import xyz.donot.roselinx.model.UserSuggestAdapter


class ReplaceAutoCompleteEditText(context: Context, attrs: AttributeSet ?=null, defStyleAttr: Int =android.R.attr.autoCompleteTextViewStyle): AutoCompleteTextView(context, attrs, defStyleAttr) {
    constructor(context: Context,attributeSet: AttributeSet?) : this(context,attributeSet,android.R.attr.autoCompleteTextViewStyle)
    constructor(context: Context) : this(context,null,android.R.attr.autoCompleteTextViewStyle)
    override fun replaceText(text: CharSequence) {
        clearComposingText()
        val adapter = adapter as UserSuggestAdapter
        val filter = adapter.filter as UserSuggestAdapter.UserFilter
        val span = getText()
        span.replace(filter.start, filter.end, text)
    }
}

