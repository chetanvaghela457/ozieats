package com.admin.ozieats_app.data

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.admin.ozieats_app.api.ApiService
import com.admin.ozieats_app.model.*
import com.admin.ozieats_app.utils.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class FavouriteRepository(val context: Context) {

    private val sharedPrefsManager = SharedPrefsManager.newInstance(context)

    fun addOrRemoveFromFavourite(user_id:Int,restaurant_id:Int): MutableLiveData<Result<String>> {
        val locationData = MutableLiveData<Result<String>>()
        val myCompositeDisposable = CompositeDisposable()

        val allIdsModel=AllIdsModel()
        allIdsModel.restaurant_id=restaurant_id
        allIdsModel.user_id=user_id

        myCompositeDisposable.add(
            ApiService.invoke(context).addToFavourite(allIdsModel)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val status = it.asJsonObject.get(Key.STATUS)
                    var message = it.asJsonObject.get(Key.MESSAGE)

                    println("fdgsg"+message)
//                    if (message != null) {
                        locationData.postValue(Result.success(message.toString()))
//                    } else {
//                        locationData.postValue(Result.error(it.asJsonObject.get(Key.MESSAGE).asString))
//                    }

                }, {
                    locationData.postValue(Result.error(it.message.toString()))
                })
        )
        return locationData
    }

    fun getFavouriteRestaurants(user_id:Int): MutableLiveData<Result<ArrayList<RestaurantModel>>> {
        val locationData = MutableLiveData<Result<ArrayList<RestaurantModel>>>()
        val myCompositeDisposable = CompositeDisposable()

        val userIdsModel=UserIdModel()
        userIdsModel.user_id=user_id

        myCompositeDisposable.add(
            ApiService.invoke(context).getFavouriteRestaurant(userIdsModel)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val status = it.asJsonObject.get(Key.STATUS)
                    val favourites_list = it.asJsonObject.getAsJsonArray(Key.FAVOURITES_LIST)

                    if (favourites_list != null) {
                        println("adksjdksjk" + favourites_list)
                        val supplierType = object : TypeToken<ArrayList<RestaurantModel>>() {}.type
                        val supplierData: ArrayList<RestaurantModel> =
                            Gson().fromJson(favourites_list, supplierType)

                        locationData.postValue(Result.success(supplierData))
                    }else
                    {
                        locationData.postValue(Result.error(it.asJsonObject.get(Key.MESSAGE).asString))
                    }

                }, {
                    locationData.postValue(Result.error(it.message.toString()))
                })
        )
        return locationData
    }

    fun addOrRemoveFromFavouriteOrder(user_id:Int,order_id:Int): MutableLiveData<Result<String>> {
        val locationData = MutableLiveData<Result<String>>()
        val myCompositeDisposable = CompositeDisposable()

        val favouriteOrderModel=FavouriteOrderModel()
        favouriteOrderModel.order_id=order_id
        favouriteOrderModel.user_id=user_id

        myCompositeDisposable.add(
            ApiService.invoke(context).addToFavouriteOrders(favouriteOrderModel)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val status = it.asJsonObject.get(Key.STATUS)
                    var message = it.asJsonObject.get(Key.MESSAGE)

                    println("fdgsg"+message)
//                    if (message != null) {
                    locationData.postValue(Result.success(message.toString()))
//                    } else {
//                        locationData.postValue(Result.error(it.asJsonObject.get(Key.MESSAGE).asString))
//                    }

                }, {
                    locationData.postValue(Result.error(it.message.toString()))
                })
        )
        return locationData
    }

    fun getFavouriteOrders(user_id:Int): MutableLiveData<Result<ArrayList<MyOrdersModel>>> {
        val locationData = MutableLiveData<Result<ArrayList<MyOrdersModel>>>()
        val myCompositeDisposable = CompositeDisposable()

        val userIdModel=UserIdModel()
        userIdModel.user_id=user_id

        myCompositeDisposable.add(
            ApiService.invoke(context).getFavouriteOrders(userIdModel)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val status = it.asJsonObject.get(Key.STATUS)
                    val favourites_list = it.asJsonObject.getAsJsonArray(Key.ORDERS_FAV)

                    if (favourites_list != null) {
                        println("adksjdksjk" + favourites_list)
                        val supplierType = object : TypeToken<ArrayList<MyOrdersModel>>() {}.type
                        val supplierData: ArrayList<MyOrdersModel> =
                            Gson().fromJson(favourites_list, supplierType)

                        locationData.postValue(Result.success(supplierData))
                    }else
                    {
                        locationData.postValue(Result.error(it.asJsonObject.get(Key.MESSAGE).asString))
                    }

                }, {
                    locationData.postValue(Result.error(it.message.toString()))
                })
        )
        return locationData
    }


}