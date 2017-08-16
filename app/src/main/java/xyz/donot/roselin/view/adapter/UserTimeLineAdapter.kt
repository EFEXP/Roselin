package xyz.donot.quetzal.viewmodel.adapter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import twitter4j.User
import xyz.donot.quetzal.view.fragment.UserDetailFragment



class UserTimeLineAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm)
{
var user: User?=null
override fun getItem(position: Int): Fragment {
  return when(position){
    0->
    {
      val fragment=UserDetailFragment()
        val bundle= Bundle()
        bundle.putSerializable("user",user)
      fragment.arguments= bundle
      fragment
    }

    else->throw  IllegalStateException()
  }}


override fun getPageTitle(position: Int): CharSequence {
  return when(position){
    0->"Info"
    else->throw IllegalStateException()
  }}

override fun getCount(): Int {
  return 1
}


}