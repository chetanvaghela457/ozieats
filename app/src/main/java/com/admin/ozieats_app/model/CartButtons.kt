package com.admin.ozieats_app.model

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CartButtons() : BaseObservable(), Parcelable {

    @SerializedName("user_id")
    @Expose
    @get:Bindable
    var user_id:Int=0

    @SerializedName("food_id")
    @Expose
    @get:Bindable
    var food_id:Int=0

    @SerializedName("quantity")
    @Expose
    @get:Bindable
    var quantity:Int=0

    constructor(parcel: Parcel) : this() {
        user_id = parcel.readInt()
        food_id = parcel.readInt()
        quantity = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(user_id)
        parcel.writeInt(food_id)
        parcel.writeInt(quantity)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CartButtons> {
        override fun createFromParcel(parcel: Parcel): CartButtons {
            return CartButtons(parcel)
        }

        override fun newArray(size: Int): Array<CartButtons?> {
            return arrayOfNulls(size)
        }
    }

}