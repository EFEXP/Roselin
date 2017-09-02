package xyz.donot.roselin.view.activity


import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_twitter_detail.*
import xyz.donot.roselin.R
import xyz.donot.roselin.util.extraUtils.Bundle
import xyz.donot.roselin.view.fragment.status.ConversationFragment


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





