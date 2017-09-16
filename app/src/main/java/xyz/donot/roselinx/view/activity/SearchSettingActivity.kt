package xyz.donot.roselinx.view.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.EditorInfo
import kotlinx.android.synthetic.main.activity_search_setting.*
import xyz.donot.roselinx.R
import xyz.donot.roselinx.util.extraUtils.hideSoftKeyboard
import xyz.donot.roselinx.util.extraUtils.start
import xyz.donot.roselinx.util.getSerialized
import xyz.donot.roselinx.view.fragment.DatePickFragment
import xyz.donot.roselinx.viewmodel.QueryBundle
import xyz.donot.roselinx.viewmodel.SearchSettingViewModel


class SearchSettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_setting)
        toolbar.inflateMenu(R.menu.menu_search_setting)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val viewmodel =
                ViewModelProviders.of(this).get(SearchSettingViewModel::class.java)
        viewmodel.dayFrom.observe(this, Observer {
            it?.let {
                day_from.text = "${it.y}/${it.m}/${it.d}~"
            }
        }
        )
        viewmodel.dayTo.observe(this, Observer {
            it?.let {
                day_to.text = "~${it.y}/${it.m}/${it.d}"
            }
        })
        viewmodel.mQuery.observe(this, Observer {
            it?.let {
                start<SearchActivity>(Bundle().apply {
                    putByteArray("query_bundle", it.getSerialized())
                    putString("query_text", search_setting_query.text.toString())
                })
            }
        })

        search_setting_query.setOnEditorActionListener { view, i, _ ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                view.hideSoftKeyboard()
                bt_search.performClick()
            }
            return@setOnEditorActionListener true
        }
        search_setting_query_absolute.setOnEditorActionListener { view, i, _ ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                view.hideSoftKeyboard()
                bt_search.performClick()
            }
            return@setOnEditorActionListener true
        }
        day_from.setOnClickListener {
            DatePickFragment()
                    .apply { arguments = Bundle().apply { putBoolean("isFrom", true) } }
                    .show(supportFragmentManager, "")
        }
        day_to.setOnClickListener {
            DatePickFragment()
                    .apply { arguments = Bundle().apply { putBoolean("isFrom", false) } }
                    .show(supportFragmentManager, "")
        }

        bt_search.setOnClickListener {
            if (search_setting_query.text.isBlank() && search_setting_query_absolute.text.isBlank())
                return@setOnClickListener
            viewmodel.setQuery(
                    QueryBundle(
                            query = search_setting_query.text.toString() ,
                            queryAbsolute = "\"${search_setting_query_absolute.text}\"",
                            dayFrom = viewmodel.dayFrom.value,
                            dayTo = viewmodel.dayTo.value,
                            videos = search_setting_video.isChecked,
                            pictures = search_setting_image.isChecked,
                            links = search_setting_links.isChecked,
                            japanese = search_setting_only_japanese.isChecked,
                            replyFrom = search_setting_from.text.toString(),
                            replyTo = search_setting_to.text.toString())
            )

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

}
