package com.admin.ozieats_app.model

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ToolbarViewModel(val context: Context) : ViewModel() {

    private var toolbarModel = MutableLiveData<ToolbarModel>()

    fun setToolbar(title: String, isVisible: Boolean, backIcon: Boolean) {
        toolbarModel.postValue(
            ToolbarModel(
                title,
                isVisible,
                backIcon
            )
        )
    }

    fun getToolBarModel(): MutableLiveData<ToolbarModel> {
        return toolbarModel
    }

    fun onBackClick(view: View) {
        (context as Activity).onBackPressed()
    }

    class ToolbarViewModelFactory(
        private val context: Context
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ToolbarViewModel(
                context
            ) as T
        }
    }
}