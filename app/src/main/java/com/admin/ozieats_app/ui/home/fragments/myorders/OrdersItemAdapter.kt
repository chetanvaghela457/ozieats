package com.admin.ozieats_app.ui.home.fragments.myorders

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.admin.ozieats_app.R
import com.admin.ozieats_app.databinding.OrderItemLayoutBinding
import com.admin.ozieats_app.model.OrderItemModel
import com.bumptech.glide.Glide


class OrdersItemAdapter(
    private var context: Context,
    private var ordersItems: ArrayList<OrderItemModel>
) : RecyclerView.Adapter<OrdersItemAdapter.OrderItemViewHolder>() {

    private var layoutInflater: LayoutInflater? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {

        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.context)
        }
        val binding: OrderItemLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.order_item_layout,
            parent,
            false
        )
        return OrderItemViewHolder(context, binding)
    }

    override fun getItemCount(): Int {
        return ordersItems.size
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        holder.itemviewOrdersItem.orderItemModel = ordersItems[position]

        holder.itemviewOrdersItem.itemPrice.text="$"+ordersItems[holder.adapterPosition].itemPrice*ordersItems[holder.adapterPosition].itemQuantity

        Glide.with(context).asBitmap().load(ordersItems[position].itemImage).into(holder.itemviewOrdersItem.imageViewOrderPhoto)

    }

    class OrderItemViewHolder(
        val context: Context,
        ordersItemLayouts: OrderItemLayoutBinding
    ) :
        RecyclerView.ViewHolder(ordersItemLayouts.root) {
        val itemviewOrdersItem: OrderItemLayoutBinding = ordersItemLayouts
    }
}