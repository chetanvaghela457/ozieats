package com.admin.ozieats_app.ui.orderprogress

import android.R
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.admin.ozieats_app.data.FavouriteRepository
import com.admin.ozieats_app.utils.Loader
import com.admin.ozieats_app.utils.Result

class OrderSummaryViewModel(private val mContext : Context) : ViewModel()  {

    var favouriteRepository=FavouriteRepository(mContext)
    private var favourtieData = MutableLiveData<Result<String>>()

    class OrderSummaryModelFactory(private val mContext: Context) :
            ViewModelProvider.NewInstanceFactory(){
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return OrderSummaryViewModel(mContext) as T
        }
    }

    fun addToFavourite(id: Int, requestId: Int): MutableLiveData<Result<String>>
    {
        val loader = Loader(mContext, R.style.Theme_Translucent_NoTitleBar)
        loader.show()
        loader.setCancelable(false)
        loader.setCanceledOnTouchOutside(false)
        favouriteRepository.addOrRemoveFromFavouriteOrder(id,requestId).observeForever {

            loader.cancel()
                favourtieData.postValue(it)
        }
        return favourtieData
    }


}