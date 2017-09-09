package xyz.donot.roselinx.view.custom

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import xyz.donot.roselinx.R
import kotlin.properties.Delegates


class CustomButton(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : ConstraintLayout(context, attributeSet) {
    private var buttonSrc by Delegates.notNull<Drawable>()
    private var buttonText by Delegates.notNull<String>()
    private var view by Delegates.notNull<View>()
    var onClick:()->Unit={}
    init {
        view = LayoutInflater.from(context).inflate(R.layout.custom_button, this)
        attributeSet?.let {
            val tArray = context.obtainStyledAttributes(it, R.styleable.my_button)
            buttonText = tArray.getString(R.styleable.my_button_buttonText)
            buttonSrc = tArray.getDrawable(R.styleable.my_button_buttonSrc)
            view.findViewById<ImageView>(R.id.bt_image).setImageDrawable(buttonSrc)
            view.findViewById<TextView>(R.id.bt_text).setText(buttonText)
            this.setOnClickListener{onClick()}
            tArray.recycle()
        }
    }

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context) : this(context, null, 0)
}
