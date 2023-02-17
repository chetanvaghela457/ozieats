package com.admin.ozieats_app.model

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CusinesModel() : BaseObservable(), Parcelable {

    @SerializedName("name")
    @Expose
    @get:Bindable
    var name: String = ""

    constructor(parcel: Parcel) : this() {
        name = parcel.readString().toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CusinesModel> {
        override fun createFromParcel(parcel: Parcel): CusinesModel {
            return CusinesModel(parcel)
        }

        override fun newArray(size: Int): Array<CusinesModel?> {
            return arrayOfNulls(size)
        }
    }
}