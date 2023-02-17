package com.admin.ozieats_app.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserIdModel: BaseObservable() {

    @SerializedName("user_id")
    @Expose
    @get:Bindable
    var user_id: Int = 0

}