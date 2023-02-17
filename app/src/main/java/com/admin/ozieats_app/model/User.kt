package com.admin.ozieats_app.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class User : BaseObservable() {

    @SerializedName("authId")
    @Expose
    @get:Bindable
    var id: Int = 0
        set(value) {
            field = value
            //notifyPropertyChanged(BR.id)
        }

    @SerializedName("user_name")
    @Expose
    @get:Bindable
    var username: String = ""
        set(value) {
            field = value
            //notifyPropertyChanged(BR.username)
        }

    @SerializedName("email")
    @Expose
    @get:Bindable
     var email: String = ""
        set(value) {
            field = value
            //notifyPropertyChanged(BR.email)
        }

    @SerializedName("password")
    @Expose
    @get:Bindable
     var password: String = ""
        set(value) {
            field = value
            //notifyPropertyChanged(BR.password)
        }

    @get:Bindable
    var confirm_password: String = ""
        set(value) {
            field = value
            //notifyPropertyChanged(BR.confirm_password)
        }


    @SerializedName("phone")
    @Expose
    @get:Bindable
     var phone: String = ""
        set(value) {
            field = value
            //notifyPropertyChanged(BR.phone)
        }


    @SerializedName("device_type")
    @Expose
    @get:Bindable
     var deviceType: String = ""
        set(value) {
            field = value
            //notifyPropertyChanged(BR.deviceType)
        }

    @SerializedName("device_token")
    @Expose
    @get:Bindable
    var device_token: String = ""
        set(value) {
            field = value
            //notifyPropertyChanged(BR.device_token)
        }

    @SerializedName("profile_image")
    @Expose
    @get:Bindable
    var profile_image: String = ""
        set(value) {
            field = value
            //notifyPropertyChanged(BR.profile_image)
        }

    @SerializedName("referral_code")
    @Expose
    @get:Bindable
    var referral_code: String = ""
        set(value) {
            field = value
            //notifyPropertyChanged(BR.referral_code)
        }

    @SerializedName("login_type")
    @Expose
    @get:Bindable
    var login_type: Int = 0
        set(value) {
            field = value
            //notifyPropertyChanged(BR.login_type)
        }

    @SerializedName("authToken")
    @Expose
    @get:Bindable
    var authToken: String = ""
        set(value) {
            field = value
            //notifyPropertyChanged(BR.authToken)
        }



}