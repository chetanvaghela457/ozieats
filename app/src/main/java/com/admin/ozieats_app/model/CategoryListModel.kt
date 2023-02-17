package com.admin.ozieats_app.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CategoryListModel: BaseObservable() {

    @SerializedName("id")
    @Expose
    @get:Bindable
    var id: Int = 0
}