package com.admin.ozieats_app.ui.referFriend

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.admin.ozieats_app.BuildConfig
import com.admin.ozieats_app.R
import com.admin.ozieats_app.utils.getUserFromPreference

class ReferFriendViewModel(private var context: Context) : ViewModel() {

    class ReferFriendModelFactory(private val context: Context) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ReferFriendViewModel(context) as T
        }
    }

    fun onInviteButtonClick(view: View) {

        var intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(
            Intent.EXTRA_TEXT,
            context.getString(R.string.share_app_message) + getUserFromPreference(context).referral_code + "----" + Uri.parse(
                "http://play.google.com/store/apps/details?id=" + context.packageName
            )
        )
        intent.type = "text/plain"
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
        context.startActivity(intent)
        if (BuildConfig.DEBUG) Log.i("TAG", "selectItem: " + Build.VERSION.RELEASE)

    }
}