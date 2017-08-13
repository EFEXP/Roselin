package xyz.donot.roselin.view.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Picasso
import xyz.donot.roselin.R


class PictureFragment : Fragment() {
    private   val stringURL by lazy {  arguments.getString("url") }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_picture, container, false)
        val image=v.findViewById<ImageView>(R.id.img)
        Picasso.with(activity).load(stringURL).into(image)


        return v
    }
}