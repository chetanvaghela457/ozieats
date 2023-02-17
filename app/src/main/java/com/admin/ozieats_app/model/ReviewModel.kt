package com.admin.ozieats_app.model

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ReviewModel() : BaseObservable(), Parcelable {

    @SerializedName("user_id")
    @Expose
    @get:Bindable
    var user_id:Int=0

    @SerializedName("restaurant_id")
    @Expose
    @get:Bindable
    var restaurant_id:Int=0

    @SerializedName("rating")
    @Expose
    @get:Bindable
    var rating:Float=0f

    @SerializedName("message")
    @Expose
    @get:Bindable
    var message:String=""

    @SerializedName("user_image")
    @Expose
    @get:Bindable
    var user_image:String=""

    @SerializedName("user_name")
    @Expose
    @get:Bindable
    var user_name:String=""

    @SerializedName("created_at")
    @Expose
    @get:Bindable
    var date:String=""

    constructor(parcel: Parcel) : this() {
        user_id = parcel.readInt()
        restaurant_id = parcel.readInt()
        rating = parcel.readFloat()
        message = parcel.readString().toString()
        user_image = parcel.readString().toString()
        user_name = parcel.readString().toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(user_id)
        parcel.writeInt(restaurant_id)
        parcel.writeFloat(rating)
        parcel.writeString(message)
        parcel.writeString(user_image)
        parcel.writeString(user_name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ReviewModel> {
        override fun createFromParcel(parcel: Parcel): ReviewModel {
            return ReviewModel(parcel)
        }

        override fun newArray(size: Int): Array<ReviewModel?> {
            return arrayOfNulls(size)
        }
    }


}