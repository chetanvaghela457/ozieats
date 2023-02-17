package com.admin.ozieats_app.model

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MenuItemModel() : BaseObservable(), Parcelable {

    @SerializedName("category_id")
    @Expose
    @get:Bindable
    var category_id: Int = 0

    @SerializedName("category_name")
    @Expose
    @get:Bindable
    var categoryName: String = ""

    @SerializedName("items")
    @Expose
    @get:Bindable
    var menuItemChildModel: ArrayList<MenuItemChildModel> ?= null

    constructor(parcel: Parcel) : this() {
        categoryName = parcel.readString().toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(categoryName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MenuItemModel> {
        override fun createFromParcel(parcel: Parcel): MenuItemModel {
            return MenuItemModel(parcel)
        }

        override fun newArray(size: Int): Array<MenuItemModel?> {
            return arrayOfNulls(size)
        }
    }
}