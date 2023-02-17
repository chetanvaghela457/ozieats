package com.admin.ozieats_app.model

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AddToCartModel() : BaseObservable(), Parcelable {

    @SerializedName("food_id")
    @Expose
    @get:Bindable
    var food_id:Int=0

    @SerializedName("user_id")
    @Expose
    @get:Bindable
    var user_id:Int=0

    @SerializedName("restaurant_id")
    @Expose
    @get:Bindable
    var restaurant_id:Int=0

    @SerializedName("quantity")
    @Expose
    @get:Bindable
    var quantity:Int=0

    @SerializedName("item_total_price")
    @Expose
    @get:Bindable
    var item_total_price:Int=0

    @SerializedName("force_insert")
    @Expose
    @get:Bindable
    var force_insert:Int=0

    constructor(parcel: Parcel) : this() {
        food_id = parcel.readInt()
        user_id = parcel.readInt()
        restaurant_id = parcel.readInt()
        quantity = parcel.readInt()
        force_insert = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(food_id)
        parcel.writeInt(user_id)
        parcel.writeInt(restaurant_id)
        parcel.writeInt(quantity)
        parcel.writeInt(force_insert)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AddToCartModel> {
        override fun createFromParcel(parcel: Parcel): AddToCartModel {
            return AddToCartModel(parcel)
        }

        override fun newArray(size: Int): Array<AddToCartModel?> {
            return arrayOfNulls(size)
        }
    }
}