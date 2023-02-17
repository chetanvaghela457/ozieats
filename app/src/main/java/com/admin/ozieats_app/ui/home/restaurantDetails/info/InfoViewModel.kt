package com.admin.ozieats_app.ui.home.restaurantDetails.info

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class InfoViewModel(private var content: Context) : ViewModel() {

    class InfoViewModelFactory(private val context: Context) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return InfoViewModel(
                context
            ) as T
        }
    }
}