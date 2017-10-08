package xyz.donot.roselinx.view.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_mute_setting.*
import xyz.donot.roselinx.R
import xyz.donot.roselinx.model.room.MuteFilter
import xyz.donot.roselinx.view.fragment.realm.MuteUserFragment
import xyz.donot.roselinx.view.fragment.realm.MuteWordFragment


class MuteSettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mute_setting)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        mute_pager.adapter = MuteViewPager(supportFragmentManager)
        mute_tab.setupWithViewPager(mute_pager)
        fab.setOnClickListener { _ ->
            val editView = EditText(this@MuteSettingActivity)
            AlertDialog.Builder(this@MuteSettingActivity)
                    .setTitle("ミュートワードを入力してください")
                    .setView(editView)
                    .setPositiveButton("OK", { _, _ ->
                        MuteFilter.save(MuteFilter(text = editView.text.toString(),user = null,kichitsui = 0,accountId = 0L))
                    })
                    .setNegativeButton("キャンセル", { _, _ -> })
                    .show()
        }
    }


    inner class MuteViewPager(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getCount(): Int = 2

        override fun getItem(position: Int): Fragment = when (position) {
            0 -> MuteUserFragment()
            1 -> MuteWordFragment()
            else -> throw Exception()
        }

        override fun getPageTitle(position: Int): CharSequence = when (position) {
            0 -> "ユーザー"
            1 -> "ワード"
            else -> throw Exception()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }


}
