package com.admin.ozieats_app.ui.home.fragments.favourite

import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class FavouriteOrderModel: BaseObservable(), Parcelable {

    @SerializedName("order_image")
    @Expose
    @get:Bindable
    var orderImage:String=""

    @SerializedName("orderProductName")
    @Expose
    @get:Bindable
    var orderProductName:String=""

    @SerializedName("orderRestaurantName")
    @Expose
    @get:Bindable
    var orderRestaurantName:String=""

    @SerializedName("orderTotalBill")
    @Expose
    @get:Bindable
    var orderTotalBill:String=""

}