package com.admin.ozieats_app.ui.home.cart

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.admin.ozieats_app.R
import com.admin.ozieats_app.data.CartRepository
import com.admin.ozieats_app.data.LocationRepository
import com.admin.ozieats_app.databinding.CartItemLayoutBinding
import com.admin.ozieats_app.model.CartButtons
import com.admin.ozieats_app.model.CartItemModel
import com.admin.ozieats_app.model.CartModel
import com.admin.ozieats_app.utils.*
import com.bumptech.glide.Glide


class CartItemAdapter(
    private var context: Context,
    private var cartItems: ArrayList<CartItemModel>,
    private var cartModel: CartModel,
    private var cartViewModel: CartViewModel,
    private var onAddTotal: CartRepository.OnTotalPriceCount,
    private var onTotalCount: LocationRepository.CartItemTotalGet
) : RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder>() {

    private var layoutInflater: LayoutInflater? = null
    //var cartItemTotal:Int=0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {

        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.context)
        }
        val binding: CartItemLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.cart_item_layout,
            parent,
            false
        )
        binding.cartItemListener = cartViewModel
        return CartItemViewHolder(context, binding)
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        holder.itemviewCartItems.cartItemModel = cartItems[position]
        holder.itemviewCartItems.cartModel = cartModel

        cartModel.cartItems = cartItems

        cartItems[position].amount =
            (cartItems[position].itemQuantity * cartItems[position].itemPrice)

        cartModel.totalAmount =
            cartItems.map { cartItemModel: CartItemModel -> cartItemModel.amount }.sum()

        cartModel.total = cartModel.restaurant_tax + cartModel.totalAmount

        onAddTotal.onPriceCount(cartModel.totalAmount, cartModel.total)

        Glide.with(context).asBitmap().load(cartItems[position].itemImage)
            .into(holder.itemviewCartItems.cartItemImage)

        if (cartItems[position].itemQuantity == 1) {
            holder.itemviewCartItems.minusButton.setImageResource(R.drawable.qty_minus)
        } else {
            holder.itemviewCartItems.minusButton.setImageResource(R.drawable.qty_minus_active)
        }

        holder.itemviewCartItems.plusButton.setOnClickListener {

            cartItemIncrement(
                holder.itemviewCartItems.textViewItemCount,
                cartItems[position],
                cartModel,
                holder.itemviewCartItems.minusButton
            )

        }

        holder.itemviewCartItems.minusButton.setOnClickListener {

            cartItemDecrement(
                holder.itemviewCartItems.textViewItemCount,
                cartItems[position],
                cartModel,
                holder.itemviewCartItems.minusButton
            )

        }

        holder.itemviewCartItems.imageViewItemRemove.setOnClickListener {

            val cartBUttons = CartButtons()
            cartBUttons.user_id = getUserFromPreference(context).id
            cartBUttons.food_id = cartItems[holder.adapterPosition].item_id
            cartBUttons.quantity = 0

            val loader = Loader(context, android.R.style.Theme_Translucent_NoTitleBar)
            loader.show()
            loader.setCancelable(false)
            loader.setCanceledOnTouchOutside(false)
            (context as Activity).window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            cartViewModel.cartRepository.reduceFromCart(cartBUttons).observeForever {

                val cartItemTotal =
                    SharedPrefsManager.newInstance(context).getInt(Preference.CART_COUNT, 0)

                val cartTotal = cartItemTotal - cartItems[holder.adapterPosition].itemQuantity
                onTotalCount.getItemTotal(cartTotal)

                cartItems.remove(cartItems[holder.adapterPosition])
                notifyItemRemoved(holder.adapterPosition)

                cartModel.totalAmount = cartModel.cartItems?.map { cartItemModel -> cartItemModel.amount }!!.sum()

                cartModel.total = cartModel.totalAmount + cartModel.restaurant_tax

                onAddTotal.onPriceCount(cartModel.totalAmount, cartModel.total)

                if (cartTotal == 0) {
                    onAddTotal.onAllItemRemove()
                }

                SharedPrefsManager.newInstance(context).putInt(Preference.CART_COUNT, cartTotal)

                loader.cancel()
            }
        }

    }

    class CartItemViewHolder(
        val context: Context,
        cartItemsLayouts: CartItemLayoutBinding
    ) :
        RecyclerView.ViewHolder(cartItemsLayouts.root) {
        val itemviewCartItems: CartItemLayoutBinding = cartItemsLayouts
    }

    private fun cartItemIncrement(
        view: TextView, cart: CartItemModel,
        cartModel: CartModel, minusImage: ImageView) {

        val cartBUttons = CartButtons()
        cartBUttons.user_id = getUserFromPreference(context).id
        cartBUttons.food_id = cart.item_id
        cartBUttons.quantity = 1

        val loader = Loader(context, android.R.style.Theme_Translucent_NoTitleBar)
        loader.show()
        loader.setCancelable(false)
        loader.setCanceledOnTouchOutside(false)
        (context as Activity).window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        cartViewModel.cartRepository.incrementToCart(cartBUttons).observeForever {

            loader.cancel()
            if (it.status == Result.Status.SUCCESS) {

                val cartItemTotal =
                    SharedPrefsManager.newInstance(context).getInt(Preference.CART_COUNT, 0)

                SharedPrefsManager.newInstance(context)
                    .putInt(Preference.CART_COUNT, cartItemTotal + 1)

                onTotalCount.getItemTotal(cartItemTotal + 1)

                val itemCount = cart.itemQuantity + 1
                view.text = itemCount.toString()
                cart.amount = (cart.itemQuantity * cart.itemPrice)

                cartModel.totalAmount =
                    cartModel.cartItems?.map { cartItemModel -> cartItemModel.amount }!!.sum()

                cartModel.total = cartModel.totalAmount + cartModel.restaurant_tax

                onAddTotal.onPriceCount(cartModel.totalAmount, cartModel.total)

                if (itemCount > 1) {
                    minusImage.setImageResource(R.drawable.qty_minus_active)
                } else {
                    minusImage.setImageResource(R.drawable.qty_minus)
                }
            } else {
                showAlert(context, it.message.toString())
            }
        }
    }

    fun cartItemDecrement(
        view: TextView,
        cart: CartItemModel,
        cartModel: CartModel,
        minusImage: ImageView
    ) {

        if (cart.itemQuantity > 1) {

            var cartBUttons = CartButtons()
            cartBUttons.user_id = getUserFromPreference(context).id
            cartBUttons.food_id = cart.item_id
            cartBUttons.quantity = 1

            val loader = Loader(context, android.R.style.Theme_Translucent_NoTitleBar)
            loader.show()
            loader.setCancelable(false)
            loader.setCanceledOnTouchOutside(false)
            (context as Activity).window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

            cartViewModel.cartRepository.reduceFromCart(cartBUttons).observeForever {

                loader.cancel()
                if (it.status == Result.Status.SUCCESS) {

                    val cartItemTotal =
                        SharedPrefsManager.newInstance(context).getInt(Preference.CART_COUNT, 0)
                    SharedPrefsManager.newInstance(context)
                        .putInt(Preference.CART_COUNT, cartItemTotal - 1)

                    onTotalCount.getItemTotal(cartItemTotal - 1)

                    var itemCount = cart.itemQuantity - 1
                    view.text = itemCount.toString()
                    cart.amount = (cart.itemQuantity * cart.itemPrice)

                    cartModel.totalAmount =
                        cartModel.cartItems?.map { cartItemModel -> cartItemModel.amount }!!.sum()

                    //var mainFinal=cartModel.totalAmount+cartModel.restaurant_tax
                    cartModel.total = cartModel.restaurant_tax + cartModel.totalAmount

                    onAddTotal.onPriceCount(cartModel.totalAmount, cartModel.total)

                    if (itemCount > 1) {
                        minusImage.setImageResource(R.drawable.qty_minus_active)
                    } else {
                        minusImage.setImageResource(R.drawable.qty_minus)
                    }

                } else {
                    showAlert(context, it.message.toString())
                }
            }
        }

    }


}