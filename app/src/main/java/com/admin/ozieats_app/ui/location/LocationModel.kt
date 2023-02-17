package com.admin.ozieats_app.ui.location

import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class LocationModel : BaseObservable(),Parcelable{

    @SerializedName("lat")
    @Expose
    @get:Bindable
    var lat:Double=0.0


    @SerializedName("lng")
    @Expose
    @get:Bindable
    var lng:Double=0.0

    @SerializedName("radius")
    @Expose
    @get:Bindable
    var radius:Int=0

    @SerializedName("user_id")
    @Expose
    @get:Bindable
    var user_id:Int=0
}