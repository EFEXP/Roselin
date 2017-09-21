package xyz.donot.roselinx.view.fragment

import android.os.Bundle
import android.support.v7.app.AppCompatDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.math_fragment.view.*
import xyz.donot.roselinx.R

class MathDialog:AppCompatDialogFragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       val v= inflater.inflate(R.layout.math_fragment,container,false)
        if (arguments.containsKey("math_formula"))
            v.math_view.text = arguments.getString("math_formula")
        return v
    }

}