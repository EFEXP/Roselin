package xyz.donot.roselin.view.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_help.*
import kotlinx.android.synthetic.main.content_base_fragment.*
import kotlinx.android.synthetic.main.content_help.*
import kotlinx.android.synthetic.main.item_changelog.view.*
import xyz.donot.roselin.R
import xyz.donot.roselin.util.extraUtils.onClick
import xyz.donot.roselin.util.extraUtils.start
import xyz.donot.roselin.view.custom.MyBaseRecyclerAdapter
import xyz.donot.roselin.view.custom.MyViewHolder
import xyz.donot.roselin.view.fragment.BaseListFragment

class HelpActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_help)
		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.setDisplayShowHomeEnabled(true)
		val pack = packageManager.getPackageInfo(packageName, 0)
		tv_version_name.text = "Ver." + pack.versionName
		rate_this_app.onClick {
			val uri = Uri.parse("http://play.google.com/store/apps/details?id="+packageName)
			startActivity(Intent(Intent.ACTION_VIEW,uri))
		}

		whats_new.setOnClickListener {
			featuresFragment().show(supportFragmentManager,"")
		}
		contact_support.onClick {
			val bundle = xyz.donot.roselin.util.extraUtils.Bundle { putString("user_screen_name","""JlowoiL""") }
			start<TweetEditActivity>(bundle)
		}

	}

}

class featuresFragment:BaseListFragment<ChangeLog>(){
	override fun adapterFun(): MyBaseRecyclerAdapter<ChangeLog, MyViewHolder> =ChangeLogAdapter()
	override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		refresh.isEnabled=false
		adapter.setEnableLoadMore(false)
	}

	override fun GetData(): MutableList<ChangeLog>? = mutableListOf(
			ChangeLog("1.0", arrayListOf("・初回リリース"))
	)
	inner class ChangeLogAdapter:MyBaseRecyclerAdapter<ChangeLog,MyViewHolder> (R.layout.item_changelog){
		override fun convert(helper: MyViewHolder, item: ChangeLog, position: Int) {
			helper.getView<LinearLayout>(R.id.item_changelog_root).apply{
				title_version.text=item.version
				for (i in item.features){
					addView(TextView(mContext).apply { text=i })
				}

			}

		}
	}

}

data class ChangeLog(val version:String,val features:List<String>)