package com.admin.ozieats_app.model

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CartItemModel() : BaseObservable(), Parcelable {

    @SerializedName("food_id")
    @Expose
    @get:Bindable
    var item_id: Int = 0

    @SerializedName("food_image")
    @Expose
    @get:Bindable
    var itemImage: String = ""

    @SerializedName("food_name")
    @Expose
    @get:Bindable
    var itemName: String = ""

    @SerializedName("food_price")
    @Expose
    @get:Bindable
    var itemPrice: Double = 0.0

    @SerializedName("food_quantity")
    @Expose
    @get:Bindable
    var itemQuantity: Int = 0
        set(value) {
            field = value
            //notifyPropertyChanged(BR.itemQuantity)
        }

    @SerializedName("amount")
    @Expose
    @get:Bindable
    var amount: Double = 0.0
        set(value) {
            field = value
           // notifyPropertyChanged(BR.amount)
        }

    constructor(parcel: Parcel) : this() {
        item_id = parcel.readInt()
        itemImage = parcel.readString().toString()
        itemName = parcel.readString().toString()
        itemPrice = parcel.readDouble()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(item_id)
        parcel.writeString(itemImage)
        parcel.writeString(itemName)
        parcel.writeDouble(itemPrice)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CartItemModel> {
        override fun createFromParcel(parcel: Parcel): CartItemModel {
            return CartItemModel(parcel)
        }

        override fun newArray(size: Int): Array<CartItemModel?> {
            return arrayOfNulls(size)
        }
    }


}