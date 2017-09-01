package xyz.donot.roselin.view.custom

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.TextView
import com.klinker.android.link_builder.TouchableMovementMethod


class CustomLinkTextView : TextView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)



    override fun hasFocusable(): Boolean = false

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)

        val movementMethod = movementMethod

        if (movementMethod is TouchableMovementMethod) {
            val span = movementMethod.pressedSpan

            if (span != null) {
                return true
            }
        }

        return false
    }
}
