package com.admin.ozieats_app.model

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AddressModel() : BaseObservable(), Parcelable {

    @SerializedName("address_singleLine")
    @Expose
    @get:Bindable
    var addressSingleLine: String = ""

    @SerializedName("address_full")
    @Expose
    @get:Bindable
    var addressFull: String = ""

    @SerializedName("lat")
    @Expose
    @get:Bindable
    var lat: Double = 0.0

    @SerializedName("lng")
    @Expose
    @get:Bindable
    var lng: Double = 0.0

    constructor(parcel: Parcel) : this() {
        addressSingleLine = parcel.readString().toString()
        addressFull = parcel.readString().toString()
        lat = parcel.readDouble()
        lng = parcel.readDouble()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(addressSingleLine)
        parcel.writeString(addressFull)
        parcel.writeDouble(lat)
        parcel.writeDouble(lng)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AddressModel> {
        override fun createFromParcel(parcel: Parcel): AddressModel {
            return AddressModel(parcel)
        }

        override fun newArray(size: Int): Array<AddressModel?> {
            return arrayOfNulls(size)
        }
    }

}