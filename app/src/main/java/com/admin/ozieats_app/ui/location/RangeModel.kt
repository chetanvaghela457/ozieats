package com.admin.ozieats_app.ui.location

import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class RangeModel : BaseObservable(),Parcelable{

    @SerializedName("range_String")
    @Expose
    @get:Bindable
    var range_String:String=""

    @SerializedName("range")
    @Expose
    @get:Bindable
    var range:Int=0
}