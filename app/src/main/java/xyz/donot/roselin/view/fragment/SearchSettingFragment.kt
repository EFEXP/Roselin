package xyz.donot.roselin.view.fragment

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_search_setting.view.*
import twitter4j.Query
import xyz.donot.roselin.R
import xyz.donot.roselin.util.extraUtils.toast
import xyz.donot.roselin.view.activity.TabSettingActivity

class SearchSettingFragment:DialogFragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view=inflater.inflate(R.layout.fragment_search_setting,container)
        view.apply {
            day_from.setOnClickListener {
                DatePickFragment()
                        .apply { arguments= Bundle().apply { putBoolean("isFrom",true) } }
                        .show(fragmentManager,"") }
            day_to.setOnClickListener {DatePickFragment()
                    .apply { arguments= Bundle().apply { putBoolean("isFrom",false) } }
                    .show(fragmentManager,"") }
            bt_search.setOnClickListener{
                if (activity is TabSettingActivity){
                    var querytext=search_setting_query.text.toString()
                    val text=querytext
                    val query= Query()
                    if(!search_setting_from.text.isNullOrEmpty()) {
                        querytext +=" from:${search_setting_from.text}"
                    }
                    if(!search_setting_to.text.isNullOrEmpty()) {
                        querytext +=" to:${search_setting_to.text}"
                    }

                    if(search_setting_video.isChecked) {
                        querytext +=" filter:videos"
                    }

                    if(search_setting_image.isChecked) {
                        querytext +=" filter:images"
                    }
                    if ( !search_setting_query_absolute.text.toString().isNullOrBlank()){
                        querytext +="\"${search_setting_query_absolute.text}\""
                    }


                    if(search_setting_links.isChecked) {
                        querytext +=" filter:links"
                    }

                    if(day_from.tag!=null&&day_from.tag is String){
                        querytext +=day_from.tag
                    }
                    if(search_setting_only_japanese.isChecked){
                        query.lang="jpn"
                    }
                    if(day_to.tag!=null&&day_to.tag is String){
                        querytext +=day_to.tag
                    }
                    querytext +=" -rt"
                    query.resultType= Query.RECENT
                    query.query=querytext
                    toast(querytext)

                    (activity as TabSettingActivity).setSearchWord(query,text)

                }

                }
            }

        return view
    }

}