package xyz.donot.roselin.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import xyz.donot.roselin.R
import xyz.donot.roselin.util.extraUtils.intent
import xyz.donot.roselin.util.haveToken
import xyz.donot.roselin.view.fragment.HomeTimeLineFragment


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        if (!haveToken()) {
            startActivity(intent<OauthActivity>())
            this.finish()
        }
        else{
            toolbar.inflateMenu(R.menu.menu_main)
            val fragment = HomeTimeLineFragment()
            supportFragmentManager.beginTransaction().add(R.id.container, fragment).commit()

}}


}
