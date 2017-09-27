package xyz.donot.roselinx.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.content_search.*
import twitter4j.Query
import xyz.donot.roselinx.R
import xyz.donot.roselinx.util.extraUtils.newIntent
import xyz.donot.roselinx.util.getDeserialized
import xyz.donot.roselinx.util.getSerialized
import xyz.donot.roselinx.view.adapter.SearchAdapter

class SearchActivity : AppCompatActivity() {
    private val queryText: String by lazy { intent.getStringExtra("query_text") }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val queryBundle = intent.getByteArrayExtra("query_bundle")
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        if (queryBundle != null) {
            val q = queryBundle.getDeserialized<Query>()
            setUpViews(q)
            //Analytics
        } else {
            setUpViews(Query(queryText))
            //Analytics

        }
    }
   companion object {
       //UserのときにQueryTextがいる
        fun createIntent(context: Context,query: Query,queryText:String?):Intent
        {
            return context.newIntent<SearchActivity>(Bundle().apply {
                putByteArray("query_bundle", query.getSerialized())
                putString("query_text",queryText.toString())
            })
        }
    }
    private fun setUpViews(tweetQuery: Query) {
        val ad = SearchAdapter(tweetQuery, queryText, supportFragmentManager)
        search_view_pager.adapter = ad
        search_tabs.setupWithViewPager(search_view_pager)
        search_view_pager.offscreenPageLimit = ad.count
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

}
