package xyz.donot.roselinx.view.custom

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean


class SingleLiveEvent<T> : MutableLiveData<T>() {
    private val mPending = AtomicBoolean(false)

    override  fun observe(owner: LifecycleOwner, observer: Observer<T>) {
        // Observe the internal MutableLiveData
        super.observe(owner, Observer<T> {
            if (mPending.compareAndSet(true, false)) {
                observer.onChanged(it)
            }
        })
    }

    override fun setValue(t: T?) {
        mPending.set(true)
        super.setValue(t)
    }
    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    fun call() {
        value = null
    }


}