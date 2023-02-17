package com.admin.ozieats_app.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CarNumberModel: BaseObservable() {

    @SerializedName("request_id")
    @Expose
    @get:Bindable
    var request_id: Int = 0

    @SerializedName("car_number")
    @Expose
    @get:Bindable
    var car_number: String =""
}