package com.admin.ozieats_app.ui.profile

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ChangePasswordModel : BaseObservable() {

    @SerializedName("user_id")
    @Expose
    @get:Bindable
    var userId: String = ""
        set(value) {
            field = value
            //notifyPropertyChanged(BR.userId)
        }

    @SerializedName("old_password")
    @Expose
    @get:Bindable
    var oldPassword: String = ""
        set(value) {
            field = value
            //notifyPropertyChanged(BR.oldPassword)
        }

    @SerializedName("new_password")
    @Expose
    @get:Bindable
    var newPassword: String = ""
        set(value) {
            field = value
            //notifyPropertyChanged(BR.newPassword)
        }

    @get:Bindable
    var confirmPassword: String = ""
        set(value) {
            field = value
            //notifyPropertyChanged(BR.confirmPassword)
        }
}