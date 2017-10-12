package xyz.donot.roselinx.ui.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import xyz.donot.roselinx.R
import kotlin.properties.Delegates


class CustomButton : ConstraintLayout {
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) {
        initView(attributeSet)
    }

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
        initView(attributeSet)
    }

    constructor(context: Context) : super(context)

    private var buttonSrc by Delegates.notNull<Drawable>()
    private var buttonText by Delegates.notNull<String>()

    var onClick: () -> Unit = {}

    init {
        View.inflate(context, R.layout.custom_button, this)
    }

    private fun initView(attr: AttributeSet?) {
        attr?.let {
            val tArray = context.obtainStyledAttributes(it, R.styleable.my_button)
            if (tArray.hasValue(R.styleable.my_button_buttonSrc))
                setSrc(tArray.getResourceId(R.styleable.my_button_buttonSrc,0))
            if (tArray.hasValue(R.styleable.my_button_buttonText))
                setText(tArray.getString(R.styleable.my_button_buttonText))
            this.setOnClickListener { onClick() }
            tArray.recycle()
        }
    }

    fun setText(string: String) {
        buttonText = string
        this.findViewById<TextView>(R.id.bt_text).text = buttonText
    }

    fun setSrc(id: Int) {
        buttonSrc = ContextCompat.getDrawable(context, id)
        this.findViewById<ImageView>(R.id.bt_image).setImageResource(id)
    }

    private fun setSrc(drawable: Drawable) {
        buttonSrc = drawable
        this.findViewById<ImageView>(R.id.bt_image).setImageDrawable(buttonSrc)
    }


}
