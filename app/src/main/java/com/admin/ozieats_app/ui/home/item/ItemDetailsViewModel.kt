package com.admin.ozieats_app.ui.home.item

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.Window
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.admin.ozieats_app.R
import com.admin.ozieats_app.data.CartRepository
import com.admin.ozieats_app.data.LocationRepository
import com.admin.ozieats_app.model.AddToCartModel
import com.admin.ozieats_app.ui.home.cart.CartActivity
import com.admin.ozieats_app.utils.*
import libs.mjn.prettydialog.PrettyDialog

class ItemDetailsViewModel(private var context: Context) : ViewModel() {

    var cartRepository = CartRepository(context)
    val progress = ObservableField<Boolean>()
    var grandTotal:Int=0


    class ItemDetailsModelFactory(private val context: Context) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ItemDetailsViewModel(context) as T
        }
    }

    val cartItem = MutableLiveData<Int>().apply {
        value = 1
    }





    fun showCartAddAlert(context: Context, message: String) {

        val dialog = PrettyDialog(context)
        dialog
            .setTitleColor(R.color.colorPrimary)
            .setMessage(message)
            .setMessageColor(R.color.pdlg_color_black)
            .setAnimationEnabled(true)
            .setIcon(R.drawable.pdlg_icon_success, R.color.colorPrimary) {
                dialog.dismiss()


            }
            .addButton(
                "Okay",
                R.color.pdlg_color_white,
                R.color.colorPrimary
            ) {
                dialog.dismiss()
                val intent= Intent(context,CartActivity::class.java)
                context.startActivity(intent)
                (context as Activity).finish()
            }

        dialog.show()
        dialog.setCancelable(false)
    }

    fun orderPickUpSuccessfully(
        mContext: Context,
        addToCart: AddToCartModel,
        loader: Loader,
        cartItemTotalGet: LocationRepository.CartItemTotalGet
    ) {
        loader.cancel()
        val dialog = Dialog(mContext)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_cart_item_force_insert)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val okayButton = dialog.findViewById<Button>(R.id.Okay)
        val discasrdButton = dialog.findViewById<Button>(R.id.discard)

        okayButton.setOnClickListener {
            dialog.dismiss()
            val loader = Loader(context, android.R.style.Theme_Translucent_NoTitleBar)
        loader.show()
        loader.setCancelable(false)
        loader.setCanceledOnTouchOutside(false)

            cartRepository.addToCart(addToCart).observeForever {

                loader.cancel()
                var countTotal=SharedPrefsManager.newInstance(mContext).getInt(Preference.CART_COUNT,0)
                cartItemTotalGet.getItemTotal(cartItem.value!!.toInt())
                SharedPrefsManager.newInstance(context).putInt(Preference.CART_COUNT,cartItem.value!!.toInt())
            }
        }

        discasrdButton.setOnClickListener {

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
}