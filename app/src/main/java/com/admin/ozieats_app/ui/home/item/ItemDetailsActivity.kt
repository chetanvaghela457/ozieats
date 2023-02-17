package com.admin.ozieats_app.ui.home.item

import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.admin.ozieats_app.R
import com.admin.ozieats_app.data.LocationRepository
import com.admin.ozieats_app.databinding.ActivityItemDetailsBinding
import com.admin.ozieats_app.model.AddOnsHeaderModel
import com.admin.ozieats_app.model.AddToCartModel
import com.admin.ozieats_app.model.MenuItemChildModel
import com.admin.ozieats_app.ui.home.cart.CartActivity
import com.admin.ozieats_app.ui.home.item.addons.AddOnsChildAdapter
import com.admin.ozieats_app.ui.home.item.addons.AddOnsHeaderAdapter
import com.admin.ozieats_app.utils.*
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_item_details.*
import kotlinx.android.synthetic.main.activity_item_details.constraintLayoutMain
import kotlinx.android.synthetic.main.activity_item_details.noInternetLayout
import kotlinx.android.synthetic.main.activity_restaurant_details.*
import kotlinx.android.synthetic.main.no_internet_layout.view.*


class ItemDetailsActivity : BaseActivity(), NetworkConnectionListener,
    AddOnsChildAdapter.OnPriceTotalChange, LocationRepository.CartItemTotalGet,LocationCallback {

    lateinit var binding: ActivityItemDetailsBinding
    lateinit var itemDetailsViewModel: ItemDetailsViewModel
    lateinit var priceArray: IntArray
    lateinit var menuItem: MenuItemChildModel
    lateinit var cartItemTotalGet: LocationRepository.CartItemTotalGet
    var isInternetConnectd = true
    var cartItemTotal = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_item_details)

        itemDetailsViewModel =
            ViewModelProvider(
                this,
                ItemDetailsViewModel.ItemDetailsModelFactory(this)
            ).get(
                ItemDetailsViewModel::class.java
            )

        cartItemTotalGet = this

        binding.itemListener = itemDetailsViewModel

        menuItem = getMenuItemChildFromPreference(this)

        binding.menuItemChildModel = menuItem
        binding.tvTotalPrice.text = "$" + menuItem.itemPrice
        binding.productName.text = menuItem.itemName
        itemDetailsViewModel.grandTotal = menuItem.itemPrice.toInt()



        binding.imageViewCartButton.setOnClickListener {

            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.imgShoppingCartItemTotal.text =
            SharedPrefsManager.newInstance(this).getInt(Preference.CART_COUNT, 0)
                .toString()

        binding.addToCartTextView.setOnClickListener {

            cartItemButtonClick(menuItem)

        }

        binding.minusButton.setOnClickListener {

            cartItemDecrement(binding.textViewItemCount)
        }

        binding.plusButton.setOnClickListener {
            cartItemIncrement(binding.textViewItemCount)
        }

        Glide.with(this).asBitmap().load(menuItem.itemImage)
            .into(binding.imageViewBackgroundImage)
        val addOnsHeader: ArrayList<AddOnsHeaderModel> = getAddOnsFromPreference(this)

        priceArray = IntArray(addOnsHeader.size)

        binding.mainBackButton.setOnClickListener {
            onBackPressed()
        }

        Log.e("ItemAddOn", "onCreate: " + addOnsHeader.size)
        val adapter = AddOnsHeaderAdapter(this, this, addOnsHeader)
        binding.mainAddOnsRecycler.adapter = adapter
        binding.mainAddOnsRecycler.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.mainAddOnsRecycler.isNestedScrollingEnabled = false

        binding.moreInfo.setOnClickListener {

            if (binding.mainAddOnsRecycler.isVisible()) {
                binding.mainAddOnsRecycler.gone()
                binding.moreInfoArrow.setImageResource(R.drawable.arrow_up)
            } else {
                binding.mainAddOnsRecycler.visible()
                binding.moreInfoArrow.setImageResource(R.drawable.arrow_down)
            }
        }
    }

    fun cartItemButtonClick(menuItemChildModel: MenuItemChildModel) {

        val loader = Loader(this, android.R.style.Theme_Translucent_NoTitleBar)
        loader.show()
        loader.setCancelable(false)
        loader.setCanceledOnTouchOutside(false)



        itemDetailsViewModel.cartRepository.checkCartItem(getUserFromPreference(this).id)
            .observeForever {
                if (it.data != null) {
                    if (it.data.restaurantName == getRestaurantFromPreference(this).restaurant_name) {

                        /*for (i in it.data.cartItems!!)
                        {
                            if (menuItemChildModel.food_id==i.item_id)
                            {
                                SharedPrefsManager.newInstance(this).putInt(Preference.CART_COUNT,it.data.cartItems!!.size)
                                binding.imgShoppingCartItemTotal.text=it.data.cartItems!!.size.toString()
                            }else
                            {
                                SharedPrefsManager.newInstance(this).putInt(Preference.CART_COUNT,it.data.cartItems!!.size+1)
                                binding.imgShoppingCartItemTotal.text=(it.data.cartItems!!.size+1).toString()
                            }
                        }*/

                        val user_id = getUserFromPreference(this).id
                        val quantity = itemDetailsViewModel.cartItem.value

                        val restaurant_id =
                            SharedPrefsManager.newInstance(this).getInt(Preference.RESTAURANT_ID, 0)
                        val force_insert = 0
                        val food_id = menuItemChildModel.food_id

                        val addToCart = AddToCartModel()
                        addToCart.food_id = food_id
                        addToCart.user_id = user_id
                        addToCart.restaurant_id = restaurant_id
                        addToCart.item_total_price = itemDetailsViewModel.grandTotal
                        if (quantity != null) {
                            addToCart.quantity = quantity
                        }
                        addToCart.force_insert = force_insert

                        itemDetailsViewModel.cartRepository.addToCart(addToCart).observeForever {

                            loader.cancel()
                            val countTotal = SharedPrefsManager.newInstance(this)
                                .getInt(Preference.CART_COUNT, 0)
                            cartItemTotalGet.getItemTotal(countTotal + itemDetailsViewModel.cartItem.value!!.toInt())
                            SharedPrefsManager.newInstance(this).putInt(
                                Preference.CART_COUNT,
                                countTotal + itemDetailsViewModel.cartItem.value!!.toInt()
                            )
                        }
                    } else {
                        val user_id = getUserFromPreference(this).id
                        val quantity = itemDetailsViewModel.cartItem.value
                        val restaurant_id =
                            SharedPrefsManager.newInstance(this).getInt(Preference.RESTAURANT_ID, 0)
                        val force_insert = 1

                        val food_id = menuItemChildModel.food_id

                        val addToCart = AddToCartModel()
                        addToCart.food_id = food_id
                        addToCart.user_id = user_id
                        addToCart.restaurant_id = restaurant_id
                        addToCart.item_total_price = itemDetailsViewModel.grandTotal
                        if (quantity != null) {
                            addToCart.quantity = quantity
                        }
                        addToCart.force_insert = force_insert
                        itemDetailsViewModel.orderPickUpSuccessfully(
                            this,
                            addToCart,
                            loader,
                            cartItemTotalGet
                        )
                    }

                } else {


                    val user_id = getUserFromPreference(this).id
                    val quantity = itemDetailsViewModel.cartItem.value
                    val restaurant_id =
                        SharedPrefsManager.newInstance(this).getInt(Preference.RESTAURANT_ID, 0)
                    val force_insert = 0
                    val food_id = menuItemChildModel.food_id

                    val addToCart = AddToCartModel()
                    addToCart.food_id = food_id
                    addToCart.user_id = user_id
                    addToCart.restaurant_id = restaurant_id
                    addToCart.item_total_price = itemDetailsViewModel.grandTotal
                    if (quantity != null) {
                        addToCart.quantity = quantity
                    }
                    addToCart.force_insert = force_insert
                    itemDetailsViewModel.cartRepository.addToCart(addToCart).observeForever {

                        loader.cancel()
                        var countTotal =
                            SharedPrefsManager.newInstance(this).getInt(Preference.CART_COUNT, 0)
                        cartItemTotalGet.getItemTotal(countTotal + itemDetailsViewModel.cartItem.value!!.toInt())

                        SharedPrefsManager.newInstance(this).putInt(
                            Preference.CART_COUNT,
                            countTotal + itemDetailsViewModel.cartItem.value!!.toInt()
                        )
                    }
                }
            }
    }

    fun cartItemIncrement(view: TextView) {
        itemDetailsViewModel.cartItem.value?.let { a ->
//            if (a < cart.itemPrice) {
            itemDetailsViewModel.cartItem.value = a + 1
            view.text = itemDetailsViewModel.cartItem.value.toString()
            if (itemDetailsViewModel.cartItem.value!! > 1) {
                binding.minusButton.setImageResource(R.drawable.qty_minus_active)
            } else {
                binding.minusButton.setImageResource(R.drawable.qty_minus)
            }
//                cart.itemQuantity = cartItem.value.toString().toInt()
//                cart.itemPrice = (cart.itemQuantity * cart.itemPrice)
//                editCartItem(cart, view)
            println("jahjadj" + itemDetailsViewModel.cartItem.value)
//            }
        }
    }

    fun cartItemDecrement(view: TextView) {
        itemDetailsViewModel.cartItem.value?.let { a ->
            if (a > 1) {
                itemDetailsViewModel.cartItem.value = a - 1
                view.text = itemDetailsViewModel.cartItem.value.toString()
                if (itemDetailsViewModel.cartItem.value!! > 1) {
                    binding.minusButton.setImageResource(R.drawable.qty_minus_active)
                } else {
                    binding.minusButton.setImageResource(R.drawable.qty_minus)
                }
//                cart.itemQuantity = cartItem.value.toString().toInt()
//                cart.amount = (cart.itemQuantity * cart.itemPrice)
//                editCartItem(cart, view)
                println("jahjadj" + itemDetailsViewModel.cartItem.value)
            }
        }
    }

    override fun onTotalChange(headerIndex: Int, price: Int) {
        Log.e("CHeck_kariye", "onTotalChange: $price==> $headerIndex")
        priceArray[headerIndex] = price
        var totalPrice = 0
        for (i in 0 until priceArray.size) {
            totalPrice += priceArray[i]
        }
        itemDetailsViewModel.grandTotal = totalPrice + menuItem.itemPrice.toInt()

        binding.tvTotalPrice.text = "$${itemDetailsViewModel.grandTotal}"
    }

    override fun onResume() {
        super.onResume()

        if (isInternetConnectd) {
            if (!constraintLayoutMain.isVisible()) {
                noInternetLayout.gone()
                constraintLayoutMain.visible()
            }
        }

        //binding.imgShoppingCartItemTotal.text = GlobalVariables.cartItemTotal.toString()
    }

    override fun getItemTotal(itemTotal: Int) {
        binding.imgShoppingCartItemTotal.text = itemTotal.toString()
    }

    override fun changeNetwork(connected: Boolean) {

        isInternetConnectd = connected
        if (connected) {
            if (!constraintLayoutMain.isVisible()) {
                noInternetLayout.retryButton.setOnClickListener {
                    noInternetLayout.gone()
                    constraintLayoutMain.visible()
                    val intent = Intent(this, ItemDetailsActivity::class.java)
                    startActivity(intent)
                }
            }
        } else {
            noInternetLayout.visible()
            constraintLayoutMain.gone()
        }
    }

    override fun updateUi(location: Location, context: Context) {
        SharedPrefsManager.newInstance(context).putFloat(Preference.PREF_LAT, location.latitude.toFloat())
        SharedPrefsManager.newInstance(context).putFloat(Preference.PREF_LNG, location.longitude.toFloat())
    }
}