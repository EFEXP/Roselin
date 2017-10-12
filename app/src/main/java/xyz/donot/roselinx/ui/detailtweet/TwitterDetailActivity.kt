package xyz.donot.roselinx.ui.detailtweet


import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_twitter_detail.*
import xyz.donot.roselinx.R
import xyz.donot.roselinx.util.extraUtils.Bundle


class TwitterDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_twitter_detail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportFragmentManager.beginTransaction()
                .add(R.id.conversation_container, ConversationFragment().apply { arguments=Bundle{putSerializable("status",intent.extras.getSerializable("Status"))} })
                .commit()

    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }


    }





