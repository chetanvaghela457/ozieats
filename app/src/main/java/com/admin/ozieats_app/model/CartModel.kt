package com.admin.ozieats_app.model

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
//import androidx.databinding.library.baseAdapters.BR
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CartModel() : BaseObservable(), Parcelable {

    @SerializedName("restaurant_name")
    @Expose
    @get:Bindable
    var restaurantName: String = ""

    @SerializedName("restaurant_id")
    @Expose
    @get:Bindable
    var restaurant_id:Int=0

    @SerializedName("restaurant_image")
    @Expose
    @get:Bindable
    var restaurantImage: String = ""

    @SerializedName("restaurant_lat")
    @Expose
    @get:Bindable
    var restaurant_lat: Double = 0.0

    @SerializedName("restaurant_lng")
    @Expose
    @get:Bindable
    var restaurant_lng: Double = 0.0

    @SerializedName("orderNumber")
    @Expose
    @get:Bindable
    var orderNumber: String = ""

    @SerializedName("restaurant_address")
    @Expose
    @get:Bindable
    var restaurantAddress: String = ""

    @SerializedName("restaurant_tax")
    @Expose
    @get:Bindable
    var restaurant_tax: Double = 0.0

    @SerializedName("food_detail")
    @Expose
    @get:Bindable
    var cartItems: ArrayList<CartItemModel>? = null

    @SerializedName("total")
    @Expose
    @get:Bindable
    var totalAmount: Double = 0.0
        set(value) {
            field = value
            //notifyPropertyChanged(BR.totalAmount)
        }

    @SerializedName("subTotal")
    @Expose
    @get:Bindable
    var total: Double = 0.0
        set(value) {
            field = value
            //notifyPropertyChanged(BR.total)
        }

    constructor(parcel: Parcel) : this() {
        restaurantName = parcel.readString().toString()
        restaurant_id = parcel.readInt()
        restaurantImage = parcel.readString().toString()
        restaurant_lat = parcel.readDouble()
        restaurant_lng = parcel.readDouble()
        orderNumber = parcel.readString().toString()
        restaurantAddress = parcel.readString().toString()
        restaurant_tax = parcel.readDouble()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(restaurantName)
        parcel.writeInt(restaurant_id)
        parcel.writeString(restaurantImage)
        parcel.writeDouble(restaurant_lat)
        parcel.writeDouble(restaurant_lng)
        parcel.writeString(orderNumber)
        parcel.writeString(restaurantAddress)
        parcel.writeDouble(restaurant_tax)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CartModel> {
        override fun createFromParcel(parcel: Parcel): CartModel {
            return CartModel(parcel)
        }

        override fun newArray(size: Int): Array<CartModel?> {
            return arrayOfNulls(size)
        }
    }


}