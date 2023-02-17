package com.admin.ozieats_app.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PushNotificationModel: BaseObservable() {

    @SerializedName("user_id")
    @Expose
    @get:Bindable
    var user_id: Int = 0

    @SerializedName("order_id")
    @Expose
    @get:Bindable
    var request_id: Int = 0

    @SerializedName("status")
    @Expose
    @get:Bindable
    var status: Int = 0

    @SerializedName("message")
    @Expose
    @get:Bindable
    var message: String = ""
}