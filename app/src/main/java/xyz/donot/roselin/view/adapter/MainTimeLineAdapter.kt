package xyz.donot.roselin.view.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import xyz.donot.roselin.model.realm.*
import xyz.donot.roselin.util.extraUtils.Bundle
import xyz.donot.roselin.view.fragment.status.HomeTimeLineFragment
import xyz.donot.roselin.view.fragment.status.ListTimeLine
import xyz.donot.roselin.view.fragment.status.MentionTimeLine


class MainTimeLineAdapter(fm: FragmentManager, private val realmResults:ArrayList<DBTabData>) : FragmentPagerAdapter(fm) {



    override fun getItem(i: Int): Fragment = when(realmResults[i].type){
        HOME->HomeTimeLineFragment()
        MENTION->MentionTimeLine()
        LIST->ListTimeLine().apply { arguments= Bundle { putLong("listId",realmResults[i].listId) } }
    else->throw IllegalStateException()
    }

    override fun getCount(): Int = realmResults.size

    override fun getPageTitle(position: Int): CharSequence = ConvertToName(realmResults[position].type)
}