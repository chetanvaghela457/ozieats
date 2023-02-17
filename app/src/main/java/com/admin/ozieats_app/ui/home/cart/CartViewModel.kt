package com.admin.ozieats_app.ui.home.cart

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Gravity
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.admin.ozieats_app.R
import com.admin.ozieats_app.data.CartRepository
import com.admin.ozieats_app.data.OrderRepository
import com.admin.ozieats_app.model.CartModel
import com.admin.ozieats_app.model.MyOrdersModel
import com.admin.ozieats_app.model.PlaceOrderModel
import com.admin.ozieats_app.ui.home.HomeActivity
import com.admin.ozieats_app.ui.orderprogress.OrderProgressActivity
import com.admin.ozieats_app.utils.*
import com.google.gson.Gson

class CartViewModel(private var context: Context) : ViewModel() {

    private var cart = MutableLiveData<Result<CartModel>>()
    var cartRepository = CartRepository(context)
    var orderRepository = OrderRepository(context)
    val progress = ObservableField<Boolean>()


    fun getAllCartItem(): LiveData<Result<CartModel>> {

        cartRepository.checkCartItem(getUserFromPreference(context).id).observeForever {

            Log.e("CHECKCARTAPI", "getAllCartItem: " + it.status)
            cart.postValue(it)
        }
        return cart
    }

    fun onCheckoutButtonClick(cartModel: CartModel) {

        Log.e("fsdkjfhsdkj", "onCheckoutButtonClick: ")

        GlobalVariables.orderTiming = getCurrentTime().toString()

        val placeOrderModel = PlaceOrderModel()
        placeOrderModel.restaurant_id = cartModel.restaurant_id
        placeOrderModel.user_id = getUserFromPreference(context).id
        placeOrderModel.device_token = getDeviceToken(context)
        placeOrderModel.bill_amount = cartModel.total

        for (items in cartModel.cartItems!!) {

            placeOrderModel.qr_code = getUserFromPreference(context).id.toString() +
                    getDeviceToken(context) + GlobalVariables.orderTiming + "        Order Amount :- $" + cartModel.total

        }

        SharedPrefsManager.newInstance(context).putString(
            Preference.CURRENT_TIME,
            getCurrentTime().toString()
        )

        Log.e("dfhdghdgh", "onCheckoutButtonClick: " + placeOrderModel.toString())

        val loader = Loader(context, android.R.style.Theme_Translucent_NoTitleBar)
        loader.show()
        loader.setCancelable(false)
        loader.setCanceledOnTouchOutside(false)
        orderRepository.placeOrder(placeOrderModel, cartModel).observeForever {

            loader.cancel()
            if (it.status == Result.Status.SUCCESS) {

                SharedPrefsManager.newInstance(context).putInt(Preference.CART_COUNT, 0)

                orderPlaceSuccessfully(context, it.data)


            } else {
                showAlert(context, it.message.toString())
            }

        }

    }

    private fun orderPlaceSuccessfully(
        mContext: Context,
        data: MyOrdersModel?
    ) {

        val dialog = Dialog(mContext)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_successfully_place_order)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        val ivClose = dialog.findViewById<ImageView>(R.id.ic_close_dialog)
        val order_no = dialog.findViewById<TextView>(R.id.tv_order_number)
        order_no.text =
            "#" + SharedPrefsManager.newInstance(context).getString(Preference.ORDER_ID_STRING)
        val navigate_restaurant = dialog.findViewById<TextView>(R.id.navigate_restaurant)

        navigate_restaurant.setOnClickListener {

            if (data != null) {
                SharedPrefsManager.newInstance(context)
                    .putString(Preference.MY_ORDER, Gson().toJson(data))
                SharedPrefsManager.newInstance(context)
                    .putInt(Preference.ORDER_REQUEST_ID, data.request_id)
                SharedPrefsManager.newInstance(context).putBoolean(Preference.ISFAVOURITE, false)

                val intent = Intent(context, OrderProgressActivity::class.java)
                SharedPrefsManager.newInstance(context)
                    .putBoolean(Preference.ISFROMDIRECTORDER, true)
                context.startActivity(intent)
                (context as Activity).finish()
            }
        }


        ivClose.setOnClickListener {

            val intent = Intent(context, HomeActivity::class.java)
            context.startActivity(intent)
            (context as Activity).finish()
            dialog.dismiss()
        }

        val window = dialog.window
        window!!.setGravity(Gravity.CENTER)
        window.setLayout(
            (0.9 * DisplayMetricsHandler.getScreenWidth()).toInt(),
            Toolbar.LayoutParams.WRAP_CONTENT
        )

        if (!dialog.isShowing) dialog.show()
    }

    class CartModelFactory(private val context: Context) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return CartViewModel(context) as T
        }
    }
}