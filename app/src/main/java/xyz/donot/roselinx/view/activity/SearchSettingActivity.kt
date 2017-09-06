package xyz.donot.roselinx.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.EditorInfo
import kotlinx.android.synthetic.main.activity_search_setting.*
import twitter4j.Query
import xyz.donot.roselinx.R
import xyz.donot.roselinx.util.extraUtils.hideSoftKeyboard
import xyz.donot.roselinx.util.extraUtils.start
import xyz.donot.roselinx.util.getSerialized
import xyz.donot.roselinx.view.fragment.DatePickFragment


class SearchSettingActivity : AppCompatActivity() {
   fun dateSet(year: Int, monthOfYear: Int, dayOfMonth: Int,isFrom:Boolean) = if(isFrom){
       day_from.text = "$year/$monthOfYear/$dayOfMonth/～"
       day_from.tag=" since:$year-$monthOfYear-$dayOfMonth"
   }
   else {
       day_to.text = "～$year/$monthOfYear/$dayOfMonth/"
       day_to.tag=" until:$year-$monthOfYear-$dayOfMonth"
   }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_setting)
        toolbar.inflateMenu(R.menu.menu_search_setting)
       setSupportActionBar(toolbar)
       supportActionBar?.setDisplayHomeAsUpEnabled(true)
       supportActionBar?.setDisplayShowHomeEnabled(true)

        search_setting_query.setOnEditorActionListener { view, i,_ ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                view.hideSoftKeyboard()
                bt_search.performClick()
            }
            return@setOnEditorActionListener true }
	    search_setting_query_absolute.setOnEditorActionListener { view, i,_ ->
		    if (i == EditorInfo.IME_ACTION_SEARCH) {
			    view.hideSoftKeyboard()
			    bt_search.performClick()
		    }
		    return@setOnEditorActionListener true }
        day_from.setOnClickListener {
            DatePickFragment()
                .apply { arguments= Bundle().apply { putBoolean("isFrom",true) } }
                .show(supportFragmentManager,"") }
        day_to.setOnClickListener {DatePickFragment()
                .apply { arguments= Bundle().apply { putBoolean("isFrom",false) } }
                .show(supportFragmentManager,"") }

        bt_search.setOnClickListener{
            if (search_setting_query.text.isBlank()&&search_setting_query_absolute.text.isBlank())
                return@setOnClickListener
            var querytext=search_setting_query.text.toString()
                val query=Query()
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
         if ( !search_setting_query_absolute.text.toString().isBlank()){
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
              query.resultType=Query.MIXED
            query.query=querytext
            start<SearchActivity>(Bundle().apply {
                putByteArray("query_bundle",query.getSerialized())
                putString("query_text",search_setting_query.text.toString())
            })
    }}
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

}