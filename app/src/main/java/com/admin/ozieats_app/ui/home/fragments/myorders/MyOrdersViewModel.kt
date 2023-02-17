package com.admin.ozieats_app.ui.home.fragments.myorders

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.admin.ozieats_app.data.OrderRepository
import com.admin.ozieats_app.model.MyOrdersModel
import com.admin.ozieats_app.utils.Loader
import com.admin.ozieats_app.utils.Result
import com.admin.ozieats_app.utils.getUserFromPreference

class MyOrdersViewModel(private var context: Context) : ViewModel() {

    private var myOrders = MutableLiveData<Result<ArrayList<MyOrdersModel>>>()
    var orderRepository = OrderRepository(context)
    val progress = ObservableField<Boolean>()


    fun getAllOrdersList(): LiveData<Result<ArrayList<MyOrdersModel>>> {

        val loader = Loader(context, android.R.style.Theme_Translucent_NoTitleBar)
        loader.show()
        loader.setCancelable(false)
        loader.setCanceledOnTouchOutside(false)
        orderRepository.orders_history(getUserFromPreference(context).id).observeForever {

            loader.cancel()

            myOrders.postValue(it)


        }
        return myOrders
    }

    class MyOrdersModelFactory(private val context: Context) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MyOrdersViewModel(context) as T
        }
    }
}