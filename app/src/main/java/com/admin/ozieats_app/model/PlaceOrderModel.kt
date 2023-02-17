package com.admin.ozieats_app.model

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PlaceOrderModel() : BaseObservable(), Parcelable {

    @SerializedName("restaurant_id")
    @Expose
    @get:Bindable
    var restaurant_id:Int=0

    @SerializedName("user_id")
    @Expose
    @get:Bindable
    var user_id:Int=0

    @SerializedName("device_token")
    @Expose
    @get:Bindable
    var device_token:String=""

    @SerializedName("qr_code")
    @Expose
    @get:Bindable
    var qr_code:String=""

    @SerializedName("bill_amount")
    @Expose
    @get:Bindable
    var bill_amount:Double=0.0

    constructor(parcel: Parcel) : this() {
        restaurant_id = parcel.readInt()
        user_id = parcel.readInt()
        device_token = parcel.readString().toString()
        qr_code = parcel.readString().toString()
        bill_amount = parcel.readDouble()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(restaurant_id)
        parcel.writeInt(user_id)
        parcel.writeString(device_token)
        parcel.writeString(qr_code)
        parcel.writeDouble(bill_amount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PlaceOrderModel> {
        override fun createFromParcel(parcel: Parcel): PlaceOrderModel {
            return PlaceOrderModel(parcel)
        }

        override fun newArray(size: Int): Array<PlaceOrderModel?> {
            return arrayOfNulls(size)
        }
    }


}