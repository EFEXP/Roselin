package xyz.donot.roselin.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_search_setting.*
import twitter4j.Query
import xyz.donot.roselin.R
import xyz.donot.roselin.util.extraUtils.start
import xyz.donot.roselin.util.extraUtils.toast
import xyz.donot.roselin.util.getSerialized
import xyz.donot.roselin.view.fragment.DatePickFragment


class SearchSettingActivity : AppCompatActivity() {
   fun dateSet(year: Int, monthOfYear: Int, dayOfMonth: Int,isFrom:Boolean) {
       if(isFrom){
           day_from.text = "${year}年${monthOfYear}月${dayOfMonth}日～"
           day_from.tag=" since:${year}-${monthOfYear}-${dayOfMonth}"
       }
       else {
           day_to.text = "～${year}年${monthOfYear}月${dayOfMonth}日"
           day_to.tag=" until:${year}-${monthOfYear}-${dayOfMonth}"
       }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_setting)
        toolbar.inflateMenu(R.menu.menu_search_setting)
       setSupportActionBar(toolbar)
       supportActionBar?.setDisplayHomeAsUpEnabled(true)
       supportActionBar?.setDisplayShowHomeEnabled(true)
        day_from.setOnClickListener {
            DatePickFragment()
                .apply { arguments= Bundle().apply { putBoolean("isFrom",true) } }
                .show(supportFragmentManager,"") }
        day_to.setOnClickListener {DatePickFragment()
                .apply { arguments= Bundle().apply { putBoolean("isFrom",false) } }
                .show(supportFragmentManager,"") }
        bt_search.setOnClickListener{
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
            toast(querytext)
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
