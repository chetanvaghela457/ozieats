package com.admin.ozieats_app.model

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RestaurantModel() : BaseObservable(), Parcelable {

    @SerializedName("id")
    @Expose
    @get:Bindable
    var restaurant_id: Int = 0

    @SerializedName("name")
    @Expose
    @get:Bindable
    var restaurant_name: String = ""

    @SerializedName("image")
    @Expose
    @get:Bindable
    var restaurant_image: String = ""

    @SerializedName("background_image")
    @Expose
    @get:Bindable
    var background_image: String = ""

    @SerializedName("restaurant_category")
    @Expose
    @get:Bindable
    var restaurant_category: String = ""

    @SerializedName("price")
    @Expose
    @get:Bindable
    var delivery_charges: String = ""

    @SerializedName("address")
    @Expose
    @get:Bindable
    var delivery_address: String = ""

    @SerializedName("discount")
    @Expose
    @get:Bindable
    var restaurant_discount: String = ""


    @SerializedName("rating")
    @Expose
    @get:Bindable
    var totalUserRating: Float = 0f

    @SerializedName("total_rating")
    @Expose
    @get:Bindable
    var totalRating: Int = 0

    @SerializedName("distance")
    @Expose
    @get:Bindable
    var restaurant_distance: String = ""

    @SerializedName("lat")
    @Expose
    @get:Bindable
    var lat: Double = 0.0

    @SerializedName("lng")
    @Expose
    @get:Bindable
    var lng: Double = 0.0

    @SerializedName("opening_time")
    @Expose
    @get:Bindable
    var opening_time: String = ""

    @SerializedName("closing_time")
    @Expose
    @get:Bindable
    var closing_time: String = ""

    @SerializedName("weekend_opening_time")
    @Expose
    @get:Bindable
    var weekend_opening_time: String = ""

    @SerializedName("weekend_closing_time")
    @Expose
    @get:Bindable
    var weekend_closing_time: String = ""

    @SerializedName("phone")
    @Expose
    @get:Bindable
    var phone: String = ""

    @SerializedName("is_open")
    @Expose
    @get:Bindable
    var open_status: Int = 0

    @SerializedName("is_favourite")
    @Expose
    @get:Bindable
    var favourites: Int = 0

    @SerializedName("cuisines")
    @Expose
    @get:Bindable
    var cuisines: ArrayList<CusinesModel>? = null

    @SerializedName("food_list")
    @Expose
    @get:Bindable
    var food_list: ArrayList<MenuItemModel>? = null

    constructor(parcel: Parcel) : this() {
        restaurant_id = parcel.readInt()
        restaurant_name = parcel.readString().toString()
        restaurant_image = parcel.readString().toString()
        restaurant_category = parcel.readString().toString()
        delivery_charges = parcel.readString().toString()
        delivery_address = parcel.readString().toString()
        restaurant_discount = parcel.readString().toString()
        totalUserRating = parcel.readFloat()
        restaurant_distance = parcel.readString().toString()
        lat = parcel.readDouble()
        lng = parcel.readDouble()
        opening_time = parcel.readString().toString()
        closing_time = parcel.readString().toString()
        weekend_opening_time = parcel.readString().toString()
        weekend_closing_time = parcel.readString().toString()
        phone = parcel.readString().toString()
        open_status = parcel.readInt()
        favourites = parcel.readInt()
        totalRating = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(restaurant_id)
        parcel.writeString(restaurant_name)
        parcel.writeString(restaurant_image)
        parcel.writeString(restaurant_category)
        parcel.writeString(delivery_charges)
        parcel.writeString(delivery_address)
        parcel.writeString(restaurant_discount)
        parcel.writeFloat(totalUserRating)
        parcel.writeString(restaurant_distance)
        parcel.writeDouble(lat)
        parcel.writeDouble(lng)
        parcel.writeString(opening_time)
        parcel.writeString(closing_time)
        parcel.writeString(weekend_opening_time)
        parcel.writeString(weekend_closing_time)
        parcel.writeString(phone)
        parcel.writeInt(open_status)
        parcel.writeInt(favourites)
        parcel.writeInt(totalRating)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RestaurantModel> {
        override fun createFromParcel(parcel: Parcel): RestaurantModel {
            return RestaurantModel(parcel)
        }

        override fun newArray(size: Int): Array<RestaurantModel?> {
            return arrayOfNulls(size)
        }
    }


}