package com.admin.ozieats_app.data

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.admin.ozieats_app.api.ApiService
import com.admin.ozieats_app.model.AllIdsModel
import com.admin.ozieats_app.model.ReviewModel
import com.admin.ozieats_app.model.UserIdModel
import com.admin.ozieats_app.utils.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ReviewRepository(val context: Context) {

    fun addRestaurantReview(reviewModel: ReviewModel): MutableLiveData<Result<String>> {
        val locationData = MutableLiveData<Result<String>>()
        val myCompositeDisposable = CompositeDisposable()
        myCompositeDisposable.add(
            ApiService.invoke(context).addRestaurantReview(reviewModel)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val status = it.asJsonObject.get(Key.STATUS)
                    val message = it.asJsonObject.get(Key.MESSAGE)
                    val rewiews = it.asJsonObject.get(Key.REVIEWS)
                    val supplierType = object : TypeToken<ArrayList<ReviewModel>>() {}.type
                    val supplierData: ArrayList<ReviewModel> =
                        Gson().fromJson(rewiews, supplierType)
                    if (rewiews!=null)
                    {
                        SharedPrefsManager.newInstance(context).putString(Preference.REVIEWS,Gson().toJson(rewiews))
                    }


                    println("kjfhbhbdjfkhg" + message)
                    if (message != null) {
                        locationData.postValue(Result.success(message.toString()))
                    } else {
                        locationData.postValue(Result.error(it.asJsonObject.get(Key.MESSAGE).asString))
                    }

                }, {
                    locationData.postValue(Result.error(it.message.toString()))
                })
        )
        return locationData
    }

    fun getRestaurantReview(id:Int): MutableLiveData<Result<ArrayList<ReviewModel>>> {
        val locationData = MutableLiveData<Result<ArrayList<ReviewModel>>>()
        val myCompositeDisposable = CompositeDisposable()

        val allIdsModel=AllIdsModel()
        allIdsModel.restaurant_id=id

        myCompositeDisposable.add(
            ApiService.invoke(context).getRestaurantReview(allIdsModel)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val status = it.asJsonObject.get(Key.STATUS)
                    var data = it.asJsonObject.get(Key.DATA)
                    println("kjfhbhbdjfkhg" + data)
                    if (data != null) {
//                        var restaurantData = cart.asJsonObject.get(Key.DATA)
                        val reviewData = object : TypeToken<ArrayList<ReviewModel>>() {}.type
                        val supplierData: ArrayList<ReviewModel> = Gson().fromJson(data, reviewData)

                        SharedPrefsManager.newInstance(context).putString(Preference.REVIEWS,Gson().toJson(data))
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

    fun getCheckReview(user_id:Int,restaurant_id:Int): MutableLiveData<Result<String>> {
        val locationData = MutableLiveData<Result<String>>()
        val myCompositeDisposable = CompositeDisposable()

        val allIdsModel=AllIdsModel()
        allIdsModel.user_id=user_id
        allIdsModel.restaurant_id=restaurant_id

        myCompositeDisposable.add(
            ApiService.invoke(context).checkReview(allIdsModel)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val status = it.asJsonObject.get(Key.STATUS)
                    val responce = it.asJsonObject.get(Key.CHECK)

                    if (responce != null) {
                        println("adksjdksjk" + responce)

                        locationData.postValue(Result.success(responce.toString()))
                    } else {
                        locationData.postValue(Result.error(it.asJsonObject.get(Key.MESSAGE).asString))
                    }

                }, {
                    locationData.postValue(Result.error(it.message.toString()))
                })
        )
        return locationData
    }

    interface OnNewReviewAdded
    {
        fun onReviewAdd(allReviews : ArrayList<ReviewModel>)


        fun userReview()

    }



}