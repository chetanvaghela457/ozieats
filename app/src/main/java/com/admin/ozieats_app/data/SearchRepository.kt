package com.admin.ozieats_app.data

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.admin.ozieats_app.api.ApiService
import com.admin.ozieats_app.model.CategoriesModel
import com.admin.ozieats_app.model.CategoryListModel
import com.admin.ozieats_app.utils.Key
import com.admin.ozieats_app.utils.Result
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SearchRepository(val context: Context) {

    fun categoriesList(): MutableLiveData<Result<ArrayList<CategoriesModel>>> {
        val locationData = MutableLiveData<Result<ArrayList<CategoriesModel>>>()
        val myCompositeDisposable = CompositeDisposable()

        val categoryListModel=CategoryListModel()
        categoryListModel.id=1
        myCompositeDisposable.add(
            ApiService.invoke(context).categoriesList(categoryListModel)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val status = it.asJsonObject.get(Key.STATUS)
                    var data = it.asJsonObject.get(Key.DATA)
                    println("kjfhbhbdjfkhg" + data)
                    if (data != null) {
//                        var restaurantData = cart.asJsonObject.get(Key.DATA)
                        val restaData = object : TypeToken<ArrayList<CategoriesModel>>() {}.type
                        val supplierData: ArrayList<CategoriesModel> =
                            Gson().fromJson(data, restaData)
                        locationData.postValue(Result.success(supplierData))
                        println("kjfhbhbdjfkhg" + supplierData)
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