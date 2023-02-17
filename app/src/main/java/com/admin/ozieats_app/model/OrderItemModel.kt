package com.admin.ozieats_app.model

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class OrderItemModel() : BaseObservable(), Parcelable {

    @SerializedName("food_image")
    @Expose
    @get:Bindable
    var itemImage: String = ""

    @SerializedName("food_name")
    @Expose
    @get:Bindable
    var itemName: String = ""

    @SerializedName("food_quantity")
    @Expose
    @get:Bindable
    var itemQuantity: Int = 0

    @SerializedName("food_price")
    @Expose
    @get:Bindable
    var itemPrice: Int = 0

    constructor(parcel: Parcel) : this() {
        itemImage = parcel.readString().toString()
        itemName = parcel.readString().toString()
        itemQuantity = parcel.readInt()
        itemPrice = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(itemImage)
        parcel.writeString(itemName)
        parcel.writeInt(itemQuantity)
        parcel.writeInt(itemPrice)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OrderItemModel> {
        override fun createFromParcel(parcel: Parcel): OrderItemModel {
            return OrderItemModel(parcel)
        }

        override fun newArray(size: Int): Array<OrderItemModel?> {
            return arrayOfNulls(size)
        }
    }

}