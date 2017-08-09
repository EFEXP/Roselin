package xyz.donot.roselin.view

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.content_main.*
import twitter4j.ResponseList
import twitter4j.Status
import twitter4j.Twitter
import xyz.donot.roselin.R
import xyz.donot.roselin.util.extraUtils.getActivity
import xyz.donot.roselin.util.extraUtils.intent
import xyz.donot.roselin.util.getTwitterInstance
import xyz.donot.roselin.util.haveToken
import xyz.donot.roselin.view.adapter.StatusAdapter


class MainActivity : AppCompatActivity() {
    val twitter by lazy { getTwitterInstance() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        if (!haveToken()) {
            startActivity(intent<OauthActivity>())
            this.finish()
        }
        else{
            recycler.layoutManager = LinearLayoutManager(this)

            val task: AsyncTask< Twitter,Void, ResponseList<Status>> = object : AsyncTask< Twitter,Void, ResponseList<Status>>() {
               override fun doInBackground(vararg params: Twitter): ResponseList<twitter4j.Status>{
                  return params[0].homeTimeline
               }

               override fun onPostExecute(result: ResponseList<twitter4j.Status>) {
                   super.onPostExecute(result)
                   val adapter=StatusAdapter(getActivity(),result)
                   adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT)
                   adapter.isFirstOnly(false);
                   recycler.adapter=adapter

               }
           }
            task.execute(twitter)


}}}
