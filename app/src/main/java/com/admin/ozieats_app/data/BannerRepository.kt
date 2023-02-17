package com.admin.ozieats_app.data

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.admin.ozieats_app.api.ApiService
import com.admin.ozieats_app.model.AdvertisementModel
import com.admin.ozieats_app.ui.location.LocationModel
import com.admin.ozieats_app.utils.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class BannerRepository(val context: Context) {

    private val sharedPrefsManager = SharedPrefsManager.newInstance(context)
    fun getBannerRepository(locationModel: LocationModel): MutableLiveData<Result<ArrayList<AdvertisementModel>>> {
        val locationData = MutableLiveData<Result<ArrayList<AdvertisementModel>>>()
        val myCompositeDisposable = CompositeDisposable()
        myCompositeDisposable.add(
            ApiService.invoke(context).getBannersList(locationModel)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val status = it.asJsonObject.get(Key.STATUS)
                    val data = it.asJsonObject.getAsJsonArray(Key.DATA)

                    if (data != null) {
                        println("adksjdksjk" + data)
                        val supplierType = object : TypeToken<ArrayList<AdvertisementModel>>() {}.type
                        val supplierData: ArrayList<AdvertisementModel> =
                            Gson().fromJson(data, supplierType)

                        println("cgflgkjlgd" + supplierData.toString())

//                        sharedPrefsManager.putString(Preference.FOODDATA, Gson().toJson(data))
                        locationData.postValue(Result.success(supplierData))
                    } else {
                        locationData.postValue(Result.error(it.asJsonObject.get(Key.MESSAGE).asString))
                    }

                }, {
                    locationData.postValue(Result.error(it.message.toString()))
                })
        )
        return locationData
    }
}