package com.admin.ozieats_app.model

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CategoriesModel() : BaseObservable(), Parcelable {

    @SerializedName("id")
    @Expose
    @get:Bindable
    var id: Int =0

    @SerializedName("name")
    @Expose
    @get:Bindable
    var categoriesName: String = ""

    @SerializedName("cuisine_image")
    @Expose
    @get:Bindable
    var cuisine_image: String = ""

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        categoriesName = parcel.readString().toString()
        cuisine_image = parcel.readString().toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(categoriesName)
        parcel.writeString(cuisine_image)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CategoriesModel> {
        override fun createFromParcel(parcel: Parcel): CategoriesModel {
            return CategoriesModel(parcel)
        }

        override fun newArray(size: Int): Array<CategoriesModel?> {
            return arrayOfNulls(size)
        }
    }
}