package com.admin.ozieats_app.model

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MenuItemChildModel() : BaseObservable(), Parcelable {

    @SerializedName("food_id")
    @Expose
    @get:Bindable
    var food_id: Int = 0

    @SerializedName("name")
    @Expose
    @get:Bindable
    var itemName: String = ""

    @SerializedName("image")
    @Expose
    @get:Bindable
    var itemImage: String = ""

//    @SerializedName("is_veg")
//    @Expose
//    @get:Bindable
//    var is_veg: Int = 0

    @SerializedName("description")
    @Expose
    @get:Bindable
    var itemDescription: String = ""

    @SerializedName("price")
    @Expose
    @get:Bindable
    var itemPrice: Double = 0.0

    @SerializedName("add_ons")
    @Expose
    @get:Bindable
    var add_ons: ArrayList<AddOnsHeaderModel>? = null

    constructor(parcel: Parcel) : this() {
        food_id = parcel.readInt()
        itemName = parcel.readString().toString()
        itemImage = parcel.readString().toString()
        itemDescription = parcel.readString().toString()
        itemPrice = parcel.readDouble()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(food_id)
        parcel.writeString(itemName)
        parcel.writeString(itemImage)
        parcel.writeString(itemDescription)
        parcel.writeDouble(itemPrice)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MenuItemChildModel> {
        override fun createFromParcel(parcel: Parcel): MenuItemChildModel {
            return MenuItemChildModel(parcel)
        }

        override fun newArray(size: Int): Array<MenuItemChildModel?> {
            return arrayOfNulls(size)
        }
    }


}