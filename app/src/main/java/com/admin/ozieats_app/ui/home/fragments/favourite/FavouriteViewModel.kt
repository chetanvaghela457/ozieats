package com.admin.ozieats_app.ui.home.fragments.favourite

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FavouriteViewModel(private var context: Context) : ViewModel() {

    class FavouriteModelFactory(private val context: Context) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return FavouriteViewModel(context) as T
        }
    }
}