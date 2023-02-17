package com.admin.ozieats_app.data

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.admin.ozieats_app.api.ApiService
import com.admin.ozieats_app.model.RestaurantModel
import com.admin.ozieats_app.ui.location.LocationModel
import com.admin.ozieats_app.utils.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class LocationRepository(val context: Context) {

    private val sharedPrefsManager = SharedPrefsManager.newInstance(context)
    fun getNearByRestaurant(locationModel: LocationModel): MutableLiveData<Result<ArrayList<RestaurantModel>>> {
        val locationData = MutableLiveData<Result<ArrayList<RestaurantModel>>>()
        val myCompositeDisposable = CompositeDisposable()
        myCompositeDisposable.add(
            ApiService.invoke(context).getNearByRestaurants(locationModel)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val status = it.asJsonObject.get(Key.STATUS)
                    val restaurants = it.asJsonObject.getAsJsonArray(Key.RESTAURANTS)
                    var advertisment=it.asJsonObject.getAsJsonArray(Key.ADVERTISMENT)

                    if (restaurants != null) {
                        println("adksjdksjk" + restaurants)
                        val supplierType = object : TypeToken<ArrayList<RestaurantModel>>() {}.type
                        val supplierData: ArrayList<RestaurantModel> =
                            Gson().fromJson(restaurants, supplierType)

                        println("cgflgkjlgd" + supplierData.toString())

                        locationData.postValue(Result.success(supplierData))
                    } else {
                        locationData.postValue(Result.error(it.asJsonObject.get(Key.MESSAGE).asString))
                    }

                    if (advertisment!=null)
                    {
                        sharedPrefsManager.putString(Preference.ADVERTISMENT_DATA, Gson().toJson(advertisment))
                    }

                }, {
                    locationData.postValue(Result.error(it.message.toString()))
                })
        )
        return locationData
    }

    interface CartItemTotalGet
    {
        fun getItemTotal(itemTotal:Int)
    }
}