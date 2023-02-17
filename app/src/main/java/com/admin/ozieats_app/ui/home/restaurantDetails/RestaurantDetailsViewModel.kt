package com.admin.ozieats_app.ui.home.restaurantDetails

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class RestaurantDetailsViewModel(private var context: Context) : ViewModel() {

    class RestaurantDetailsModelFactory(private val context: Context) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return RestaurantDetailsViewModel(context) as T
        }
    }
}