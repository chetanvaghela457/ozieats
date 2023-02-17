package com.admin.ozieats_app.ui.home.fragments.favourite

import android.R
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.admin.ozieats_app.data.FavouriteRepository
import com.admin.ozieats_app.model.MyOrdersModel
import com.admin.ozieats_app.utils.Loader
import com.admin.ozieats_app.utils.Result
import com.admin.ozieats_app.utils.getUserFromPreference

class FavouriteOrderViewModel(private var context: Context) : ViewModel() {

    private var favouriteModel =
        MutableLiveData<Result<ArrayList<MyOrdersModel>>>()
    var favouriteRepository = FavouriteRepository(context)
    private var favourtieData = MutableLiveData<Result<String>>()

    fun getAllFavouriteList(): LiveData<Result<ArrayList<MyOrdersModel>>> {

        val loader = Loader(context, R.style.Theme_Translucent_NoTitleBar)
        loader.show()
        loader.setCancelable(false)
        loader.setCanceledOnTouchOutside(false)
        favouriteRepository.getFavouriteOrders(getUserFromPreference(context).id).observeForever {

            loader.cancel()
            favouriteModel.postValue(it)

        }
        return favouriteModel
    }

    fun addToFavourite(id: Int, requestId: Int): MutableLiveData<Result<String>>
    {
        val loader = Loader(context, R.style.Theme_Translucent_NoTitleBar)
        loader.show()
        loader.setCancelable(false)
        loader.setCanceledOnTouchOutside(false)
        favouriteRepository.addOrRemoveFromFavouriteOrder(id,requestId).observeForever {

            loader.cancel()
            favourtieData.postValue(it)
        }
        return favourtieData
    }


    class FavouriteOrderModelFactory(private val context: Context) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return FavouriteOrderViewModel(context) as T
        }
    }
}