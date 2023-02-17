package com.admin.ozieats_app.ui.support

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SupportViewModel(private var context: Context) : ViewModel() {

    class SupportModelFactory(private val context: Context) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SupportViewModel(context) as T
        }
    }
}