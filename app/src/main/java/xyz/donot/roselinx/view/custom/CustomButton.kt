package xyz.donot.roselinx.view.custom

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import xyz.donot.roselinx.R
import kotlin.properties.Delegates


class CustomButton(context: Context, attributeSet: AttributeSet?=null, defStyleAttr: Int=0) : ConstraintLayout(context, attributeSet,defStyleAttr) {
    constructor(context: Context,attributeSet: AttributeSet?) : this(context,attributeSet,0)
    constructor(context: Context) : this(context,null,0)
    private var buttonSrc by Delegates.notNull<Drawable>()
    private var buttonText by Delegates.notNull<String>()
    private var view by Delegates.notNull<View>()
    var onClick:()->Unit={}

    init {
        view = LayoutInflater.from(context).inflate(R.layout.custom_button, this)
        attributeSet?.let {
            val tArray = context.obtainStyledAttributes(it, R.styleable.my_button)
            if (tArray.hasValue(R.styleable.my_button_buttonSrc))
                setSrc(tArray.getDrawable(R.styleable.my_button_buttonSrc))
            if (tArray.hasValue(R.styleable.my_button_buttonText))
                setText(tArray.getString(R.styleable.my_button_buttonText))
            this.setOnClickListener{onClick()}
            tArray.recycle()
        }

    }

    fun setText(string: String) {
        buttonText=string
        view.findViewById<TextView>(R.id.bt_text).text =    buttonText
    }
    fun setSrc(id:Int){
        buttonSrc=ContextCompat.getDrawable(context,id)
        view.findViewById<ImageView>(R.id.bt_image).setImageDrawable(buttonSrc)
    }
    private fun setSrc(drawable: Drawable){
        buttonSrc=drawable
        view.findViewById<ImageView>(R.id.bt_image).setImageDrawable(buttonSrc)
    }


}
