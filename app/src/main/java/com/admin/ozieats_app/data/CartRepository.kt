package com.admin.ozieats_app.data

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.admin.ozieats_app.api.ApiService
import com.admin.ozieats_app.model.AddToCartModel
import com.admin.ozieats_app.model.CartButtons
import com.admin.ozieats_app.model.CartModel
import com.admin.ozieats_app.model.UserIdModel
import com.admin.ozieats_app.utils.Key
import com.admin.ozieats_app.utils.Result
import com.admin.ozieats_app.utils.SharedPrefsManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class CartRepository(val context: Context) {
    private val sharedPrefsManager = SharedPrefsManager.newInstance(context)
    fun addToCart(addToCartModel: AddToCartModel): MutableLiveData<Result<String>> {
        val locationData = MutableLiveData<Result<String>>()
        val myCompositeDisposable = CompositeDisposable()
        myCompositeDisposable.add(
            ApiService.invoke(context).addToCart(addToCartModel)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val status = it.asJsonObject.get(Key.STATUS)
                    var message = it.asJsonObject.get(Key.MESSAGE)
                    if (message != null) {
                        locationData.postValue(Result.success(message.asString))
                    } else {
                        locationData.postValue(Result.error(it.asJsonObject.get(Key.MESSAGE).asString))
                    }

                }, {
                    locationData.postValue(Result.error(it.message.toString()))
                })
        )
        return locationData
    }

    fun reduceFromCart(cartButtons: CartButtons): MutableLiveData<Result<String>> {
        val locationData = MutableLiveData<Result<String>>()
        val myCompositeDisposable = CompositeDisposable()
        myCompositeDisposable.add(
            ApiService.invoke(context).reduceFromCart(cartButtons)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val status = it.asJsonObject.get(Key.STATUS)
                    var message = it.asJsonObject.get(Key.MESSAGE)
                    if (message != null) {
                        locationData.postValue(Result.success(message.asString))
                    } else {
                        locationData.postValue(Result.error(it.asJsonObject.get(Key.MESSAGE).asString))
                    }

                }, {
                    locationData.postValue(Result.error(it.message.toString()))
                })
        )
        return locationData
    }

    fun incrementToCart(cartButtons: CartButtons): MutableLiveData<Result<String>> {
        val locationData = MutableLiveData<Result<String>>()
        val myCompositeDisposable = CompositeDisposable()
        myCompositeDisposable.add(
            ApiService.invoke(context).incrementToCart(cartButtons)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val status = it.asJsonObject.get(Key.STATUS)
                    var message = it.asJsonObject.get(Key.MESSAGE)
                    if (message != null) {
                        locationData.postValue(Result.success(message.asString))
                    } else {
                        locationData.postValue(Result.error(it.asJsonObject.get(Key.MESSAGE).asString))
                    }

                }, {

                    locationData.postValue(Result.error(it.message.toString()))
                })
        )
        return locationData
    }

    fun checkCartItem(user_id: Int): MutableLiveData<Result<CartModel>> {
        val locationData = MutableLiveData<Result<CartModel>>()
        val myCompositeDisposable = CompositeDisposable()

        val userIdModel = UserIdModel()
        userIdModel.user_id = user_id
        myCompositeDisposable.add(
            ApiService.invoke(context).checkCart(userIdModel)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val status = it.asJsonObject.get(Key.STATUS)
                    val cart = it.asJsonObject.get(Key.CART)
                    println("kjfhdjfkhg" + status)
                    if (cart != null) {
                        var restaurantData = cart.asJsonObject.get(Key.RESTAURANT_DATA)
                        val restaData = object : TypeToken<CartModel>() {}.type
                        val supplierData: CartModel =
                            Gson().fromJson(restaurantData, restaData)
                        locationData.postValue(Result.success(supplierData))
                        println("kjfhdjfkhg" + supplierData)
                    } else {
                        locationData.postValue(Result.error(it.asJsonObject.get(Key.MESSAGE).asString))
                    }
//                    var message=it.asJsonObject.get(Key.MESSAGE)
//                    if (message!=null){
//                        locationData.postValue(Result.success(message.asString))
//                    } else {
//                        locationData.postValue(Result.error(it.asJsonObject.get(Key.MESSAGE).asString))
//                    }

                }, {

                    locationData.postValue(Result.error(it.message.toString()))
                })
        )
        return locationData
    }

    interface OnTotalPriceCount {
        fun onPriceCount(totalPrice: Double, finalPrice: Double)

        fun onAllItemRemove()

    }
}