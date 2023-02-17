package com.admin.ozieats_app.model

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ProfileImageModel() : BaseObservable() {

    @SerializedName("user_id")
    @Expose
    @get:Bindable
    var id: Int = 0

    @SerializedName("profile_image")
    @Expose
    @get:Bindable
    var profile_image: String = ""

    constructor(parcel: Parcel) : this() {
        profile_image = parcel.readString().toString()
        id = parcel.readInt()
    }

  /*  override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(profile_image)
        parcel.writeInt(id)
    }

    override fun describeContents(): Int {
        return 0
    }*/

    companion object CREATOR : Parcelable.Creator<ProfileImageModel> {
        override fun createFromParcel(parcel: Parcel): ProfileImageModel {
            return ProfileImageModel(parcel)
        }

        override fun newArray(size: Int): Array<ProfileImageModel?> {
            return arrayOfNulls(size)
        }
    }


}