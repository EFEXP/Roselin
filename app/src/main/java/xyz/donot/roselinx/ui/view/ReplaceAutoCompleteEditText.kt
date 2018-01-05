package xyz.donot.roselinx.ui.view

import android.content.Context
import android.support.text.emoji.widget.EmojiEditTextHelper
import android.support.v7.widget.AppCompatAutoCompleteTextView
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import xyz.donot.roselinx.ui.editteweet.UserSuggestAdapter






class ReplaceAutoCompleteEditText(context: Context, attrs: AttributeSet ?=null, defStyleAttr: Int =android.R.attr.autoCompleteTextViewStyle): AppCompatAutoCompleteTextView(context, attrs, defStyleAttr) {
    constructor(context: Context,attributeSet: AttributeSet?) : this(context,attributeSet,android.R.attr.autoCompleteTextViewStyle)
    constructor(context: Context) : this(context,null,android.R.attr.autoCompleteTextViewStyle)
    val mEmojiEditTextHelper by lazy { EmojiEditTextHelper(this) }
    init {
        super.setKeyListener(mEmojiEditTextHelper.getKeyListener(keyListener))
    }

    override fun setKeyListener(keyListener: android.text.method.KeyListener) {
        super.setKeyListener(mEmojiEditTextHelper.getKeyListener(keyListener))
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection {
        val inputConnection = super.onCreateInputConnection(outAttrs)
        return mEmojiEditTextHelper.onCreateInputConnection(inputConnection, outAttrs)!!
    }



    override fun replaceText(text: CharSequence) {
        clearComposingText()
        val adapter = adapter as UserSuggestAdapter
        val filter = adapter.filter as UserSuggestAdapter.UserFilter
        val span = getText()
        span.replace(filter.start, filter.end, text)
    }
}

