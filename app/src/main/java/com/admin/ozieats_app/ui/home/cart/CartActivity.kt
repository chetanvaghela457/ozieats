package com.admin.ozieats_app.ui.home.cart

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.admin.ozieats_app.R
import com.admin.ozieats_app.data.CartRepository
import com.admin.ozieats_app.data.LocationRepository
import com.admin.ozieats_app.databinding.ActivityCartBinding
import com.admin.ozieats_app.model.CartModel
import com.admin.ozieats_app.ui.home.item.NetworkConnectionListener
import com.admin.ozieats_app.utils.*
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.activity_cart.constraintLayoutMain
import kotlinx.android.synthetic.main.activity_cart.noInternetLayout
import kotlinx.android.synthetic.main.activity_restaurant_details.*
import kotlinx.android.synthetic.main.no_internet_layout.view.*

class CartActivity : BaseActivity(), NetworkConnectionListener, CartRepository.OnTotalPriceCount,
    LocationRepository.CartItemTotalGet {

    lateinit var binding: ActivityCartBinding
    lateinit var cartViewModel: CartViewModel
    lateinit var itemTotalCount: CartRepository.OnTotalPriceCount
    lateinit var onReviewGet: LocationRepository.CartItemTotalGet
    var isInternetConnectd = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart)

        cartViewModel =
            ViewModelProvider(this, CartViewModel.CartModelFactory(this)).get(
                CartViewModel::class.java
            )

        onReviewGet = this

        binding.cartListener = cartViewModel
        //binding.cartModel = CartModel()

        itemTotalCount = this

        binding.backButton.setOnClickListener {

            finish()

        }

        binding.noItemBackButton.setOnClickListener {
            finish()
        }



        fetchData()

    }

    private fun fetchData() {

        val loader = Loader(this, android.R.style.Theme_Translucent_NoTitleBar)
        loader.show()
        loader.setCancelable(false)
        loader.setCanceledOnTouchOutside(false)
        cartViewModel.getAllCartItem().observe(this, Observer {

            loader.cancel()

            if (it.status == Result.Status.SUCCESS) {
                binding.emptyLayout.gone()
                binding.itemVisibleLayout.visible()
                binding.cartModel = it.data

                Glide.with(this).asBitmap().load(it.data!!.restaurantImage)
                    .into(binding.imageViewRestaurantImage)
                val totalQuantity =
                    it.data.cartItems?.map { cartItemModel -> cartItemModel.itemQuantity }!!.sum()

                SharedPrefsManager.newInstance(this).putInt(Preference.CART_COUNT, totalQuantity)

                val adapter = CartItemAdapter(
                    this,
                    it.data.cartItems!!,
                    it.data,
                    cartViewModel,
                    this@CartActivity, this
                )
                cartItemsRecycler.adapter = adapter

                cartViewModel.progress.set(false)
                Log.e(
                    "sdgjghs",
                    "onResume: " + binding.cartModel!!.totalAmount + "----" + binding.cartModel!!.restaurant_tax
                )
                binding.totalAmountTextView.text =  it.data.totalAmount.toString()
                binding.restaurantTax.text =  it.data.restaurant_tax.toString()

                binding.textViewFinalAmount.text =  it.data.total.toString()



            } else {
                binding.emptyLayout.visible()
                binding.itemVisibleLayout.gone()
            }

        })
    }

    override fun onPriceCount(totalPrice: Double, finalPrice: Double) {

        binding.totalAmountTextView.text = totalPrice.toString()

        binding.textViewFinalAmount.text = finalPrice.toString()

    }

    override fun onAllItemRemove() {
        binding.emptyLayout.visible()
        binding.itemVisibleLayout.gone()
    }

    override fun getItemTotal(itemTotal: Int) {
        SharedPrefsManager.newInstance(this).putInt(Preference.CART_COUNT, itemTotal)
    }

    override fun changeNetwork(connected: Boolean) {

        isInternetConnectd = connected
        if (connected) {
            if (!constraintLayoutMain.isVisible()) {
                noInternetLayout.retryButton.setOnClickListener {

                    noInternetLayout.gone()
                    constraintLayoutMain.visible()
                    val intent = Intent(this, CartActivity::class.java)
                    startActivity(intent)
                }
            }
        } else {
            noInternetLayout.visible()
            constraintLayoutMain.gone()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isInternetConnectd) {
            if (!constraintLayoutMain.isVisible()) {
                noInternetLayout.gone()
                constraintLayoutMain.visible()
            }
        }
    }


}