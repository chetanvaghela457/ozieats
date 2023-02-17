package com.admin.ozieats_app.ui.home.fragments.myorders

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.admin.ozieats_app.R
import com.admin.ozieats_app.databinding.MyorderItemLayoutBinding
import com.admin.ozieats_app.model.OrderItemModel
import com.bumptech.glide.Glide


class OrderItemAdapter(
    private var context: Context,
    private var cartItems: ArrayList<OrderItemModel>
) : RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder>() {

    private var layoutInflater: LayoutInflater? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {

        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.context)
        }
        val binding: MyorderItemLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.myorder_item_layout,
            parent,
            false
        )
        return OrderItemViewHolder(context, binding)
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        holder.itemviewCartItems.orderItemModel = cartItems[position]


        Glide.with(context).asBitmap().load(cartItems[position].itemImage)
            .into(holder.itemviewCartItems.cartItemImage)
    }

    class OrderItemViewHolder(
        val context: Context,
        cartItemsLayouts: MyorderItemLayoutBinding
    ) :
        RecyclerView.ViewHolder(cartItemsLayouts.root) {
        val itemviewCartItems: MyorderItemLayoutBinding = cartItemsLayouts
    }
}