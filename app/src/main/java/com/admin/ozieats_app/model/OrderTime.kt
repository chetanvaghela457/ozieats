package com.admin.ozieats_app.model

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class OrderTime() : BaseObservable(), Parcelable {

    @SerializedName("time")
    @Expose
    @get:Bindable
    var time:String=""

    @SerializedName("request_id")
    @Expose
    @get:Bindable
    var request_id:Int=0

    @SerializedName("lat")
    @Expose
    @get:Bindable
    var lat:Double=0.0

    @SerializedName("lng")
    @Expose
    @get:Bindable
    var lng:Double=0.0

    @SerializedName("notify")
    @Expose
    @get:Bindable
    var notify:Boolean=false

    constructor(parcel: Parcel) : this() {
        time = parcel.readString().toString()
        request_id = parcel.readInt()
        lat = parcel.readDouble()
        lng = parcel.readDouble()
        notify = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(time)
        parcel.writeInt(request_id)
        parcel.writeDouble(lat)
        parcel.writeDouble(lng)
        parcel.writeByte(if (notify) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OrderTime> {
        override fun createFromParcel(parcel: Parcel): OrderTime {
            return OrderTime(parcel)
        }

        override fun newArray(size: Int): Array<OrderTime?> {
            return arrayOfNulls(size)
        }
    }
}