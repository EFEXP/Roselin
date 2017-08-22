package xyz.donot.roselin.extend

import android.os.AsyncTask




abstract class SafeAsyncTask<Arg,Result>:AsyncTask<Arg,Void,Result>(){

    private var exception: Exception? = null

    @Throws(Exception::class)
    protected abstract fun doTask(arg:Arg): Result

    protected abstract fun onSuccess(result: Result)
    protected abstract fun onFailure(exception: Exception)

    override fun doInBackground(vararg p0: Arg): Result? = try {
        doTask(p0[0])
    } catch (e: Exception) {
        exception = e
        cancel(true)
        null
    }



    override fun onCancelled() {
        if (exception != null) {
            onFailure(exception!!)
        }
        exception = null
    }

    override fun onPostExecute(result: Result) = onSuccess(result)
}