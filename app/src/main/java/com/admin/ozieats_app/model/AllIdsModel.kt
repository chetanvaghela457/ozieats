package com.admin.ozieats_app.model

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AllIdsModel() : BaseObservable(), Parcelable {

    @SerializedName("restaurant_id")
    @Expose
    @get:Bindable
    var restaurant_id:Int=0

    @SerializedName("is_veg")
    @Expose
    @get:Bindable
    var checkVeg:Int=0

    @SerializedName("user_id")
    @Expose
    @get:Bindable
    var user_id:Int=0

    constructor(parcel: Parcel) : this() {
        restaurant_id = parcel.readInt()
        checkVeg = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(restaurant_id)
        parcel.writeInt(checkVeg)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AllIdsModel> {
        override fun createFromParcel(parcel: Parcel): AllIdsModel {
            return AllIdsModel(parcel)
        }

        override fun newArray(size: Int): Array<AllIdsModel?> {
            return arrayOfNulls(size)
        }
    }

}