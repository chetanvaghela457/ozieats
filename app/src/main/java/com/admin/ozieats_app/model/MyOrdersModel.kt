package com.admin.ozieats_app.model

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MyOrdersModel() : BaseObservable(), Parcelable {

    @SerializedName("request_id")
    @Expose
    @get:Bindable
    var request_id: Int = 0

    @SerializedName("restaurant_name")
    @Expose
    @get:Bindable
    var restaurant_name: String = ""

    @SerializedName("ordered_on")
    @Expose
    @get:Bindable
    var orderDateAndTime: String = ""

    @SerializedName("restaurant_image")
    @Expose
    @get:Bindable
    var restaurantImage: String = ""


    @SerializedName("address")
    @Expose
    @get:Bindable
    var restaurant_address: String = ""

    @SerializedName("order_id")
    @Expose
    @get:Bindable
    var orderId: String = ""

    @SerializedName("bill_amount")
    @Expose
    @get:Bindable
    var totalPrice: Double = 0.0

    @SerializedName("tax")
    @Expose
    @get:Bindable
    var tax: Double = 0.0

    @SerializedName("restaurant_lat")
    @Expose
    @get:Bindable
    var lat: Double = 0.0

    @SerializedName("restaurant_lng")
    @Expose
    @get:Bindable
    var lng: Double = 0.0

    @SerializedName("delivery_address")
    @Expose
    @get:Bindable
    var orderStatus: Boolean = true

    @SerializedName("is_favourite")
    @Expose
    @get:Bindable
    var favourite: Int = 0

    @SerializedName("status")
    @Expose
    @get:Bindable
    var status: Int = 0

    @SerializedName("qr_code")
    @Expose
    @get:Bindable
    var qr_code: String = ""

    @SerializedName("item_list")
    @Expose
    @get:Bindable
    var orderItemModel: ArrayList<OrderItemModel>? = null

    constructor(parcel: Parcel) : this() {
        request_id = parcel.readInt()
        restaurant_name = parcel.readString().toString()
        orderDateAndTime = parcel.readString().toString()
        restaurantImage = parcel.readString().toString()
        restaurant_address = parcel.readString().toString()
        orderId = parcel.readString().toString()
        totalPrice = parcel.readDouble()
        tax = parcel.readDouble()
        lat = parcel.readDouble()
        lng = parcel.readDouble()
        orderStatus = parcel.readByte() != 0.toByte()
        favourite = parcel.readInt()
        status = parcel.readInt()
    }


    override fun writeToParcel(dest: Parcel?, flags: Int) {
        TODO("Not yet implemented")
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    companion object CREATOR : Parcelable.Creator<MyOrdersModel> {
        override fun createFromParcel(parcel: Parcel): MyOrdersModel {
            return MyOrdersModel(parcel)
        }

        override fun newArray(size: Int): Array<MyOrdersModel?> {
            return arrayOfNulls(size)
        }
    }


}