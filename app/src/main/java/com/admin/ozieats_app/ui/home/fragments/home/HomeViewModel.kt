package com.admin.ozieats_app.ui.home.fragments.home

import android.content.Context
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.admin.ozieats_app.data.CartRepository
import com.admin.ozieats_app.data.FavouriteRepository
import com.admin.ozieats_app.data.LocationRepository
import com.admin.ozieats_app.data.SearchRepository
import com.admin.ozieats_app.model.CategoriesModel
import com.admin.ozieats_app.model.RestaurantModel
import com.admin.ozieats_app.ui.location.LocationModel
import com.admin.ozieats_app.utils.*
import com.google.gson.Gson
import pub.devrel.easypermissions.AfterPermissionGranted

class HomeViewModel(
    private var context: Context,
    var cartItemTotalGet: LocationRepository.CartItemTotalGet
) : ViewModel() {

    private var restaurantModel = MutableLiveData<ArrayList<RestaurantModel>>()

    private var favourtieData = MutableLiveData<Result<String>>()
    private var categoriesData = MutableLiveData<ArrayList<CategoriesModel>>()
    private var searchRepository = SearchRepository(context)
    val progress = ObservableField<Boolean>()
    val dataNotFound = ObservableField<Boolean>()
    var favouriteRepository = FavouriteRepository(context)
    var locationRepository = LocationRepository(context)
    var cartRepository = CartRepository(context)
    //lateinit var cartItemTotalGet:LocationRepository.CartItemTotalGet


    @AfterPermissionGranted(Permissions.LOCATION_PERMISSION)
    fun getAllRestaurantList(locationModel: LocationModel): LiveData<ArrayList<RestaurantModel>> {

        val dataBool =
            SharedPrefsManager.newInstance(context).getBoolean(Preference.HOME_DATA, false)
        if (dataBool) {
            val loader = Loader(context, android.R.style.Theme_Translucent_NoTitleBar)
            loader.show()
            loader.setCancelable(false)
            loader.setCanceledOnTouchOutside(false)
            locationRepository.getNearByRestaurant(locationModel)
                .observeForever {

                    loader.cancel()
                    loader.dismiss()
                    loader.hideProgress()
                    restaurantModel.postValue(it.data)

                }

            searchRepository.categoriesList().observeForever {
                if (it.status == Result.Status.SUCCESS) {
                    categoriesData.postValue(it.data)
                    SharedPrefsManager.newInstance(context)
                        .putString(Preference.SEARCH_DATA, Gson().toJson(it.data))

                } else {
                    //showAlert(context, it.message.toString())
                }

            }
        } else {

            if (getTotalRestaurantPreference(context).size > 0) {
                restaurantModel.postValue(getTotalRestaurantPreference(context))
            }
            searchRepository.categoriesList().observeForever {
                if (it.status == Result.Status.SUCCESS) {
                    categoriesData.postValue(it.data)
                    SharedPrefsManager.newInstance(context)
                        .putString(Preference.SEARCH_DATA, Gson().toJson(it.data))

                } else {
                   // showAlert(context, it.message.toString())
                }

            }
        }

        cartRepository.checkCartItem(getUserFromPreference(context).id).observeForever {

            if (it.status == Result.Status.SUCCESS) {
                if (it.data!!.cartItems!!.size > 0) {
                    val totalQuantity =
                        it.data.cartItems?.map { cartItemModel -> cartItemModel.itemQuantity }!!
                            .sum()

                    Log.e("TOTALITEMCART", "getAllRestaurantList: " + totalQuantity)
                    SharedPrefsManager.newInstance(context)
                        .putInt(Preference.CART_COUNT, totalQuantity)

                    cartItemTotalGet.getItemTotal(totalQuantity)

                } else {
                    val totalQuantity = 0
                    cartItemTotalGet.getItemTotal(totalQuantity)
                    SharedPrefsManager.newInstance(context)
                        .putInt(Preference.CART_COUNT, totalQuantity)
                }
            }
        }
        return restaurantModel
    }

    fun addOrRemoveToFavourite(restaurant_id: Int): MutableLiveData<Result<String>> {

        favouriteRepository.addOrRemoveFromFavourite(
            getUserFromPreference(context).id,
            restaurant_id
        ).observeForever {

            favourtieData.postValue(it)

        }
        return favourtieData
    }


    class HomeViewModelFactory(
        private val context: Context,
        var cartItemTotalGet: LocationRepository.CartItemTotalGet
    ) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return HomeViewModel(context, cartItemTotalGet) as T
        }
    }


    @AfterPermissionGranted(Permissions.LOCATION_PERMISSION)
    fun geLocationChange(locationModel: LocationModel): LiveData<ArrayList<RestaurantModel>> {

        val loader = Loader(context, android.R.style.Theme_Translucent_NoTitleBar)
        loader.show()
        loader.setCancelable(false)
        loader.setCanceledOnTouchOutside(false)
        locationRepository.getNearByRestaurant(locationModel)
            .observeForever {

                loader.cancel()
                loader.dismiss()
                loader.hideProgress()
                restaurantModel.postValue(it.data)

            }

        searchRepository.categoriesList().observeForever {
            if (it.status == Result.Status.SUCCESS) {
                categoriesData.postValue(it.data)
                SharedPrefsManager.newInstance(context)
                    .putString(Preference.SEARCH_DATA, Gson().toJson(it.data))

            } else {
                showAlert(context, it.message.toString())
            }

        }

        cartRepository.checkCartItem(getUserFromPreference(context).id).observeForever {

            if (it.status == Result.Status.SUCCESS) {
                if (it.data!!.cartItems!!.size > 0) {
                    val totalQuantity =
                        it.data.cartItems?.map { cartItemModel -> cartItemModel.itemQuantity }!!
                            .sum()

                    Log.e("TOTALITEMCART", "getAllRestaurantList: " + totalQuantity)
                    SharedPrefsManager.newInstance(context)
                        .putInt(Preference.CART_COUNT, totalQuantity)

                    cartItemTotalGet.getItemTotal(totalQuantity)

                } else {
                    val totalQuantity = 0
                    cartItemTotalGet.getItemTotal(totalQuantity)
                    SharedPrefsManager.newInstance(context)
                        .putInt(Preference.CART_COUNT, totalQuantity)
                }
            }
        }
        return restaurantModel
    }
}