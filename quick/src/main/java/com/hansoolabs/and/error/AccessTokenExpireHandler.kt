package com.hansoolabs.and.error

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.appcompat.app.AppCompatActivity
import com.hansoolabs.and.app.QuickDialogFragment
import com.hansoolabs.and.R
import com.hansoolabs.and.app.QuickDialog
import com.hansoolabs.and.utils.StringUtil

/**
 * Created by brownsoo on 2017. 8. 17..
 */

class AccessTokenExpireHandler : ExceptionHandler {

    override var resolving: Boolean = false

    constructor(activity: AppCompatActivity) : super(activity)

    constructor(fragment: Fragment) : super(fragment)

    override fun addHandler(tag: String?, handler: (throwable: Throwable, data: Bundle?) -> Boolean): AccessTokenExpireHandler {
        return super.addHandler(tag, handler) as AccessTokenExpireHandler
    }

    override fun removeHandler(tag: String): AccessTokenExpireHandler {
        return super.removeHandler(tag) as AccessTokenExpireHandler
    }

    override fun removeHandler(handler: (throwable: Throwable, data: Bundle?) -> Boolean): AccessTokenExpireHandler {
        return super.removeHandler(handler) as AccessTokenExpireHandler
    }

    override fun onError(throwable: Throwable, tag: String?, data: Bundle?): Boolean {
        val e: BaseException? = BaseExceptionHandler.toCommonException(throwable)
        val code = e!!.error.code
        if (code == BaseError.Code.SessionExpired ||
                code == BaseError.Code.ConcurrentLogin ||
                code == BaseError.Code.DeactivatedUser) {
            val key = tag ?: TAG_NULL
            val handler = handlers[key]
            if (handler != null && handler.invoke(e, data)) {
                return true
            }
            val context = delegate.context
            val fragmentManager = delegate.fragmentManager
            if (context!= null && fragmentManager != null) {
                resolving = true
                val default = Bundle()
                data?.let {
                    default.putAll(it)
                }
                // todo Change texts by code
                val title = context.getString(R.string.error__invalid_credential_dialog__title)
                val message = context.getString(R.string.error__invalid_credential_dialog__msg)
                QuickDialogFragment.BasicBuilder(context)
                        .setCancelable(false)
                        .setTitle(title)
                        .setMessage(message)
                        .setPositiveButton(R.string.and__confirm)
                        .setDefaultResultData(default)
                        .show(fragmentManager, TAG_DIALOG)
                return true
            }
        }
        return false
    }

    override fun onAlertDialogResult(tag: String, resultCode: Int, resultData: Bundle?): Boolean {
        if(super.onAlertDialogResult(tag, resultCode, resultData)) {
            return true
        }
        if (tag == TAG_DIALOG) {
            resolving = false
            val positive = resultData!=null && QuickDialog.isPositiveClick(resultData)
            if (positive) {
                notifyLogoutRequired()
                return true
            }
        }
        return false
    }

    private fun notifyLogoutRequired() {
        delegate.context?.let {
            LocalBroadcastManager.getInstance(it).sendBroadcast(Intent(ACTION_LOGOUT_REQUIRED))
        }
    }

    companion object {
        @JvmField
        val ACTION_LOGOUT_REQUIRED: String = StringUtil.constant("ACTION_LOGOUT_REQUIRED")
        private const val TAG_DIALOG = "AccessTokenExpireDialog"
    }
}