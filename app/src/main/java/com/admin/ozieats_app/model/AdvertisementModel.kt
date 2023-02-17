package com.admin.ozieats_app.model

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AdvertisementModel() : BaseObservable(), Parcelable {

    @SerializedName("restaurant_name")
    @Expose
    @get:Bindable
    var restaurant_name: String = ""

    @SerializedName("restaurant_id")
    @Expose
    @get:Bindable
    var restaurant_id: Int = 0

    @SerializedName("item_name")
    @Expose
    @get:Bindable
    var restaurant_item_name: String = ""

    @SerializedName("item_image")
    @Expose
    @get:Bindable
    var restaurant_item_image: String = ""

    @SerializedName("rating")
    @Expose
    @get:Bindable
    var restaurant_rating: Int = 0

    @SerializedName("item_price")
    @Expose
    @get:Bindable
    var restaurant_item_price: Int = 0

    @SerializedName("item_id")
    @Expose
    @get:Bindable
    var item_id: Int = 0

    @SerializedName("item_addon")
    @Expose
    @get:Bindable
    var add_ons: ArrayList<AddOnsHeaderModel>? = null

    constructor(parcel: Parcel) : this() {
        restaurant_name = parcel.readString().toString()
        restaurant_item_name = parcel.readString().toString()
        restaurant_item_image = parcel.readString().toString()
        restaurant_rating = parcel.readInt()
        restaurant_id = parcel.readInt()
        restaurant_item_price = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(restaurant_name)
        parcel.writeString(restaurant_item_name)
        parcel.writeString(restaurant_item_image)
        parcel.writeInt(restaurant_rating)
        parcel.writeInt(restaurant_id)
        parcel.writeInt(restaurant_item_price)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AdvertisementModel> {
        override fun createFromParcel(parcel: Parcel): AdvertisementModel {
            return AdvertisementModel(parcel)
        }

        override fun newArray(size: Int): Array<AdvertisementModel?> {
            return arrayOfNulls(size)
        }
    }

}