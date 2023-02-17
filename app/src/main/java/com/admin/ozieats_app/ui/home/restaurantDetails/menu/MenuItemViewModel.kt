package com.admin.ozieats_app.ui.home.restaurantDetails.menu

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.admin.ozieats_app.data.MenuRepository
import com.admin.ozieats_app.model.AllIdsModel
import com.admin.ozieats_app.model.MenuItemModel
import com.admin.ozieats_app.utils.*

class MenuItemViewModel(private var context: Context) : ViewModel() {

    private var menuItems = MutableLiveData<ArrayList<MenuItemModel>>()
    var menuRepository:MenuRepository= MenuRepository(context)
    val progress = ObservableField<Boolean>()

    fun getAllMenuItems(): LiveData<ArrayList<MenuItemModel>> {

        var restaurant_id=SharedPrefsManager.newInstance(context).getInt(Preference.RESTAURANT_ID,0)

        var allIdsModel=AllIdsModel()
        allIdsModel.restaurant_id=restaurant_id
        allIdsModel.user_id= getUserFromPreference(context).id
        allIdsModel.checkVeg=1

        val loader = Loader(context, android.R.style.Theme_Translucent_NoTitleBar)
        loader.show()
        loader.setCancelable(false)
        loader.setCanceledOnTouchOutside(false)
        menuRepository.getMenuItems(allIdsModel).observeForever {

            loader.cancel()
            if (it.status==Result.Status.SUCCESS)
            {
                menuItems.postValue(it.data)
            }else
            {
                showAlert(context, it.message.toString())
            }
        }
//        val jsonFileString = getJsonDataFromAsset(context, "menu_items.json")
//        val type = object : TypeToken<ArrayList<MenuItemModel>>() {}.type
//        menuItems.postValue(Gson().fromJson(jsonFileString, type))
        return menuItems
    }

    class MenuItemModelFactory(private val context: Context) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MenuItemViewModel(context) as T
        }
    }
}