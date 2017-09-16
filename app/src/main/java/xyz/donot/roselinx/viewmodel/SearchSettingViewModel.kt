package xyz.donot.roselinx.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import twitter4j.Query
import xyz.donot.roselinx.Roselin
import xyz.donot.roselinx.util.extraUtils.toast

class SearchSettingViewModel(app:Application):AndroidViewModel(app){

    val dayFrom:MutableLiveData<DateCompact> =MutableLiveData()
    val dayTo:MutableLiveData<DateCompact> =MutableLiveData()
    val mQuery:MutableLiveData<Query> =MutableLiveData()

    fun setQuery(queryBundle: QueryBundle){
        var querytext=queryBundle.query
        val query = Query()
        if (!queryBundle.replyFrom.isNullOrEmpty()) {
                querytext += " from:${queryBundle.replyTo}"
        }
        if (!queryBundle.replyTo.isNullOrEmpty()) {
               querytext += " to:${queryBundle.replyTo}"
        }

        if (queryBundle.videos) {
               querytext += " filter:videos"
        }

        if (queryBundle.pictures) {
               querytext += " filter:images"
        }
        if (!queryBundle.queryAbsolute.isNullOrBlank()) {
                 querytext += "\"${queryBundle.queryAbsolute}\""
        }
        if (queryBundle.links) {
                 querytext += " filter:links"
        }
        if (queryBundle.japanese) {
                query.lang = "jpn"
        }
        if (queryBundle.dayFrom!=null) {
            val it=queryBundle.dayFrom
            querytext += " since:${it.y}-${it.m}-${it.d}"
        }
        if (queryBundle.dayTo!=null) {
            val it=queryBundle.dayTo
                 querytext += " until:${it.y}-${it.m}-${it.d}"
        }
        querytext += " -rt"
        query.resultType = Query.MIXED
        query.query = querytext
        mQuery.value=query
        getApplication<Roselin>().toast(querytext.toString())

    }
}
data class DateCompact(val y :Int, val m :Int, val d :Int)
data class QueryBundle(val japanese :Boolean,
                       val links :Boolean,
                       val pictures :Boolean,
                       val videos :Boolean,
                       val dayFrom :DateCompact?,
                       val dayTo :DateCompact?,
                       val replyTo :String?,
                       val replyFrom :String?,
                       val queryAbsolute :String?,
                       val query:String?)