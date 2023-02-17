package com.admin.ozieats_app.model

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PaynowResoponce() : BaseObservable(), Parcelable {

    @SerializedName("request_id")
    @Expose
    @get:Bindable
    var request_id:Int=0

    @SerializedName("order_id")
    @Expose
    @get:Bindable
    var order_id:String=""

    constructor(parcel: Parcel) : this() {
        request_id = parcel.readInt()
        order_id = parcel.readString().toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(request_id)
        parcel.writeString(order_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PaynowResoponce> {
        override fun createFromParcel(parcel: Parcel): PaynowResoponce {
            return PaynowResoponce(parcel)
        }

        override fun newArray(size: Int): Array<PaynowResoponce?> {
            return arrayOfNulls(size)
        }
    }
}