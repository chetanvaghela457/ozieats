package com.admin.ozieats_app.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.admin.ozieats_app.api.ApiService
import com.admin.ozieats_app.model.*
import com.admin.ozieats_app.ui.home.fragments.myorders.MyOrdersFragment
import com.admin.ozieats_app.utils.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class OrderRepository(val context: Context) {

    private val mMyOrdersFragment = MyOrdersFragment()

    fun orders_history(user_id: Int): MutableLiveData<Result<ArrayList<MyOrdersModel>>> {
        val locationData = MutableLiveData<Result<ArrayList<MyOrdersModel>>>()
        val myCompositeDisposable = CompositeDisposable()

        val userIdModel=UserIdModel()
        userIdModel.user_id=user_id

        myCompositeDisposable.add(
            ApiService.invoke(context).orderHistory(userIdModel)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val status = it.asJsonObject.get(Key.STATUS)
                    val orders = it.asJsonObject.getAsJsonArray(Key.ORDERS)

                    if (orders != null) {
                        println("adksjdksjk" + orders)
                        val supplierType = object : TypeToken<ArrayList<MyOrdersModel>>() {}.type
                        val supplierData: ArrayList<MyOrdersModel> =
                            Gson().fromJson(orders, supplierType)

                        println("cgflgkjlgd" + supplierData.toString())

//                        SharedPrefsManager.newInstance(context).putString(Preference.ORDERS, Gson().toJson(orders))
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

    fun placeOrder(
        placeOrderModel: PlaceOrderModel,
        cartModel: CartModel
    ): MutableLiveData<Result<MyOrdersModel>> {
        val locationData = MutableLiveData<Result<MyOrdersModel>>()
        val myCompositeDisposable = CompositeDisposable()
        myCompositeDisposable.add(
            ApiService.invoke(context).orderPlace(placeOrderModel)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val status = it.asJsonObject.get(Key.STATUS)
                    val message = it.asJsonObject.get(Key.MESSAGE)
                    val data = it.asJsonObject.get(Key.DATA)
                    val orders = it.asJsonObject.get(Key.ORDERS)
                    //val orders_list = it.asJsonObject.get(Key.ORDERS_LIST)

                    if (data != null) {
                        val supplierType = object : TypeToken<PaynowResoponce>() {}.type
                        val supplierData: PaynowResoponce =
                            Gson().fromJson(data, supplierType)
                        Log.e("placed_order_id", "placeOrder: " + supplierData.request_id)
                        SharedPrefsManager.newInstance(context).putString(
                            Preference.ORDER_ID,
                            supplierData.request_id.toString()
                        )

                        val data = FirebaseDataGet()
                        data.dataFromFirebase(
                            supplierData.request_id,
                            object : FirebaseDataGet.OnOrderStatusChanged {
                                override fun changeOrderStatus(orderID: Int, orderStatus: Int) {
                                    Log.e("CalledInterface", "changeOrderStatus: Checking...")
                                    mMyOrdersFragment.changedStatus(orderID, orderStatus)
                                }

                            })

                        Log.e("AJHFHJSDHF", "placeOrder: " + orders)

                        val orderTiming = OrderTime()
                        orderTiming.request_id = supplierData.request_id
                        orderTiming.time = GlobalVariables.orderTiming
                        orderTiming.lat = cartModel.restaurant_lat
                        orderTiming.lng = cartModel.restaurant_lng
                        orderTiming.notify = true

                        val arrayList = ArrayList<OrderTime>()

                        val arrayTime = getOrderTimePreference(context)
                        arrayList.addAll(arrayTime)

                        arrayList.add(orderTiming)

                        SharedPrefsManager.newInstance(context)
                            .putString(Preference.ORDERTIMING, Gson().toJson(arrayList))

                        SharedPrefsManager.newInstance(context).putString(
                            Preference.ORDER_ID_STRING,
                            supplierData.order_id
                        )

                        val myOrders = object : TypeToken<MyOrdersModel>() {}.type
                        val orderDetails: MyOrdersModel =
                            Gson().fromJson(orders, myOrders)

                        locationData.postValue(Result.success(orderDetails))
                    } else {
                        locationData.postValue(Result.error(it.asJsonObject.get(Key.MESSAGE).asString))
//                        showAlert(context,it.asJsonObject.get(Key.MESSAGE).asString)
                    }


                }, {
                    locationData.postValue(Result.error(it.message.toString()))
                })
        )
        return locationData
    }

    fun updateOrderStatus(
        user_id: Int,
        request_id: Int,
        status: Int
    ): MutableLiveData<Result<String>> {
        val locationData = MutableLiveData<Result<String>>()
        val myCompositeDisposable = CompositeDisposable()

        val updateStatusModel=UpdateStatusModel()
        updateStatusModel.user_id=user_id
        updateStatusModel.request_id=request_id
        updateStatusModel.status=status

        myCompositeDisposable.add(
            ApiService.invoke(context).updateOrderStatus(updateStatusModel)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val status = it.asJsonObject.get(Key.STATUS)
                    val message = it.asJsonObject.get(Key.MESSAGE)

                    if (status.asBoolean) {
                        locationData.postValue(Result.success("Order Completed"))
                    } else {
                        locationData.postValue(Result.error(it.asJsonObject.get(Key.MESSAGE).asString))
                    }

                }, {
                    locationData.postValue(Result.error(it.message.toString()))
                })
        )
        return locationData
    }

    fun pushNotifications(
        user_id: Int,
        request_id: Int,
        status: Int,
        message: String
    ): MutableLiveData<Result<String>> {
        val locationData = MutableLiveData<Result<String>>()
        val myCompositeDisposable = CompositeDisposable()

        val pushNotificationModel=PushNotificationModel()
        pushNotificationModel.user_id=user_id
        pushNotificationModel.request_id=request_id
        pushNotificationModel.message=message
        pushNotificationModel.status=status


        myCompositeDisposable.add(
            ApiService.invoke(context).pushNotification(pushNotificationModel)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val status = it.asJsonObject.get(Key.STATUS)
                    val message = it.asJsonObject.get(Key.MESSAGE)

                    if (status.asBoolean) {
                        locationData.postValue(Result.success("Order Notification Sent"))
                    } else {
                        locationData.postValue(Result.error(it.asJsonObject.get(Key.MESSAGE).asString))
                    }

                }, {
                    locationData.postValue(Result.error(it.message.toString()))
                })
        )
        return locationData
    }

    fun addCarNumber(request_id: Int, car_number: String): MutableLiveData<Result<String>> {
        val locationData = MutableLiveData<Result<String>>()
        val myCompositeDisposable = CompositeDisposable()

        val carNumberModel=CarNumberModel()
        carNumberModel.request_id=request_id
        carNumberModel.car_number=car_number

        myCompositeDisposable.add(
            ApiService.invoke(context).addCarNumber(carNumberModel)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val status = it.asJsonObject.get(Key.STATUS)
                    val message = it.asJsonObject.get(Key.MESSAGE)

                    if (status.asBoolean) {
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

}