package com.admin.ozieats_app.ui.sendFeedBack

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FeedbackViewModel(private var context: Context) : ViewModel() {

    class FeedbackModelFactory(private val context: Context) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return FeedbackViewModel(context) as T
        }
    }
}