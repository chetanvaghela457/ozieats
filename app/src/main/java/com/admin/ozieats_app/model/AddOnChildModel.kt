package com.admin.ozieats_app.model

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AddOnChildModel() : BaseObservable(), Parcelable {

    @SerializedName("addon_category")
    @Expose
    @get:Bindable
    var addon_category: String = ""

    @SerializedName("addon_price")
    @Expose
    @get:Bindable
    var addon_price: String = ""

    constructor(parcel: Parcel) : this() {
        addon_category = parcel.readString().toString()
        addon_price = parcel.readString().toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(addon_category)
        parcel.writeString(addon_price)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AddOnChildModel> {
        override fun createFromParcel(parcel: Parcel): AddOnChildModel {
            return AddOnChildModel(parcel)
        }

        override fun newArray(size: Int): Array<AddOnChildModel?> {
            return arrayOfNulls(size)
        }
    }
}