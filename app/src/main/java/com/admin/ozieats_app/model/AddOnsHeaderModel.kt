package com.admin.ozieats_app.model

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AddOnsHeaderModel() : BaseObservable(), Parcelable {

    @SerializedName("add_on_name")
    @Expose
    @get:Bindable
    var add_on_name: String = ""

    @SerializedName("add_on_category")
    @Expose
    @get:Bindable
    var add_on_category: ArrayList<AddOnChildModel> ?=null

    constructor(parcel: Parcel) : this() {
        add_on_name = parcel.readString().toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(add_on_name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AddOnsHeaderModel> {
        override fun createFromParcel(parcel: Parcel): AddOnsHeaderModel {
            return AddOnsHeaderModel(parcel)
        }

        override fun newArray(size: Int): Array<AddOnsHeaderModel?> {
            return arrayOfNulls(size)
        }
    }
}