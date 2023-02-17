package com.admin.ozieats_app.model

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CheckForReviewModel() : BaseObservable(), Parcelable {

    @SerializedName("restaurant_id")
    @Expose
    @get:Bindable
    var restaurant_id: Int =0

    @SerializedName("status")
    @Expose
    @get:Bindable
    var status: Int = 0

    constructor(parcel: Parcel) : this() {
        restaurant_id = parcel.readInt()
        status = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(restaurant_id)
        parcel.writeInt(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CheckForReviewModel> {
        override fun createFromParcel(parcel: Parcel): CheckForReviewModel {
            return CheckForReviewModel(parcel)
        }

        override fun newArray(size: Int): Array<CheckForReviewModel?> {
            return arrayOfNulls(size)
        }
    }
}