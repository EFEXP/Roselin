package xyz.donot.roselinx.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.content_search.*
import twitter4j.Query
import xyz.donot.roselinx.R
import xyz.donot.roselinx.util.getDeserialized
import xyz.donot.roselinx.view.adapter.SearchAdapter

class SearchActivity : AppCompatActivity() {
    private val query_text :String by lazy {  intent.getStringExtra("query_text")}
    override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      val query_bundle=intent.getByteArrayExtra("query_bundle")
      setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        if(query_bundle!=null)
      {
        val q=  query_bundle.getDeserialized<Query>()
        setUpViews(q)
                //Analytics
      }
        else {
        setUpViews(Query(query_text))
          //Analytics

      }


    }
 private fun setUpViews(tweetQuery: Query){
     val ad=SearchAdapter(tweetQuery,query_text,supportFragmentManager)
     search_view_pager.adapter = ad
     search_tabs.setupWithViewPager(search_view_pager)
     search_view_pager.offscreenPageLimit=ad.count
 }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

}
