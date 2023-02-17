package com.admin.ozieats_app.ui.home.fragments.searchproducts

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.admin.ozieats_app.data.SearchRepository
import com.admin.ozieats_app.model.CategoriesModel
import com.admin.ozieats_app.utils.*

class SearchViewModel(private var mContext: Context) : ViewModel() {

    private var categoriesData = MutableLiveData<ArrayList<CategoriesModel>>()
    private var searchRepository = SearchRepository(mContext)
    val progress = ObservableField<Boolean>()

    fun getAllCategoriesItems(): LiveData<ArrayList<CategoriesModel>> {

        var catData= getAllCategoriesPreference(mContext)

        if (catData.size>0)
        {
            categoriesData.postValue(catData)
        }
        return categoriesData
    }

    class SearchFactoryModel(private val mContext: Context) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SearchViewModel(mContext) as T
        }
    }
}