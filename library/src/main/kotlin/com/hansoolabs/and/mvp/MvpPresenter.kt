package com.hansoolabs.and.mvp

import android.os.Handler
import android.os.Looper
import com.hansoolabs.and.RequestCallback
import com.hansoolabs.and.error.ExceptionHandler
import java.lang.ref.WeakReference

/**
 *
 * Created by brownsoo on 2017. 5. 10..
 */

open class MvpPresenter<out T : MvpContract.View>(presentingView: T,
                                                  protected val exceptionHandler: ExceptionHandler)
    : MvpContract.Presenter, MvpContract.ViewForegroundListener {

    private val delayedCallbacks = ArrayList<DelayedCallback<*>>()
    private val viewRef: WeakReference<T> = WeakReference(presentingView)
    protected val view: T? = viewRef.get()
    
    protected fun viewAccessibleDo(run: () -> Unit) {
        if (view != null) run.invoke()
    }
    override fun initialize() {
        view?.addForegroundListener(this)
    }

    override fun terminate() {
        view?.removeForegroundListener(this)
        viewRef.clear()
    }

    override fun onViewForeground() {
        synchronized(delayedCallbacks) {
            for (callback in delayedCallbacks) {
                callback.fire()
            }
            delayedCallbacks.clear()
        }
    }

    override fun onViewBackground() {
        //
    }

    fun getString(resId: Int): String = view?.getString(resId) ?: ""

    fun getString(resId: Int, vararg args: Any): String = view?.getString(resId, args) ?: ""


    fun <K> delayUntilViewForeground(callback: RequestCallback<K>) : RequestCallback<K> {

        return object : RequestCallback<K> {
            override fun onSuccess(result: K?) {
                if (view?.isForeground == true) {
                    callback.onSuccess(result)
                }
                else {
                    synchronized(delayedCallbacks) {
                        delayedCallbacks.add(DelayedCallback(result, callback))
                    }
                }
            }

            override fun onFailure(e: Exception) {
                if (view?.isForeground == true) {
                    callback.onFailure(e)
                }
                else {
                    synchronized(delayedCallbacks) {
                        delayedCallbacks.add(DelayedCallback(e, callback))
                    }
                }
            }
        }
    }



    private class DelayedCallback<T> {
        private val success: Boolean
        private val callback: RequestCallback<T>
        private val value: T?
        private val e: Exception?
        private val handler: Handler

        constructor(value: T?, callback: RequestCallback<T>) {
            this.value = value
            this.e = null
            this.callback = callback
            success = true
            handler = Handler(Looper.myLooper())
        }

        constructor(e: Exception, callback: RequestCallback<T>) {
            this.value = null
            this.e = e
            this.callback = callback
            success = false
            handler = Handler(Looper.myLooper())
        }

        fun fire() {
            handler.post {
                if (success) {
                    callback.onSuccess(value)
                } else {
                    callback.onFailure(e as Exception)
                }
            }
        }
    }
}
