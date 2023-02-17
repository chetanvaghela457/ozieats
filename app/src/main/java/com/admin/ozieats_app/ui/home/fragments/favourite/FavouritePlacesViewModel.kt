package com.admin.ozieats_app.ui.home.fragments.favourite

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.admin.ozieats_app.data.FavouriteRepository
import com.admin.ozieats_app.model.RestaurantModel
import com.admin.ozieats_app.utils.Loader
import com.admin.ozieats_app.utils.Result
import com.admin.ozieats_app.utils.getUserFromPreference

class FavouritePlacesViewModel(private var context: Context) : ViewModel() {

    private var restaurantModel = MutableLiveData<Result<ArrayList<RestaurantModel>>>()
    var favouriteRepository = FavouriteRepository(context)
    val progress = ObservableField<Boolean>()
    val notFound = ObservableField<Boolean>()


    fun getAllFavouritePlaceList(): LiveData<Result<ArrayList<RestaurantModel>>> {

        val loader = Loader(context, android.R.style.Theme_Translucent_NoTitleBar)
        loader.show()
        loader.setCancelable(false)
        loader.setCanceledOnTouchOutside(false)
        favouriteRepository.getFavouriteRestaurants(getUserFromPreference(context).id)
            .observeForever {

                loader.cancel()

                restaurantModel.postValue(it)

            }
        return restaurantModel
    }

    class FavouritePlacesModelFactory(private val context: Context) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return FavouritePlacesViewModel(context) as T
        }
    }

}