package com.admin.ozieats_app.data

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.admin.ozieats_app.api.ApiService
import com.admin.ozieats_app.model.AllIdsModel
import com.admin.ozieats_app.model.MenuItemModel
import com.admin.ozieats_app.utils.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MenuRepository(val context: Context) {
    private val sharedPrefsManager = SharedPrefsManager.newInstance(context)
    fun getMenuItems(allIds:AllIdsModel): MutableLiveData<Result<ArrayList<MenuItemModel>>> {
        val locationData = MutableLiveData<Result<ArrayList<MenuItemModel>>>()
        val myCompositeDisposable = CompositeDisposable()
        myCompositeDisposable.add(
            ApiService.invoke(context).getMenuItem(allIds)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val status = it.asJsonObject.get(Key.STATUS)
                    val menus = it.asJsonObject.getAsJsonArray(Key.FOODLIST)

                    if (menus != null) {
                        println("adksjdksjk" + menus)
                        val supplierType = object : TypeToken<ArrayList<MenuItemModel>>() {}.type
                        val supplierData: ArrayList<MenuItemModel> = Gson().fromJson(menus, supplierType)

                        println("cgflgkjlgd" + supplierData.toString())

                        sharedPrefsManager.putString(Preference.FOODDATA, Gson().toJson(menus))
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