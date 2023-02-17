package com.admin.ozieats_app.ui.home.fragments.favourite

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.admin.ozieats_app.R
import com.admin.ozieats_app.databinding.FavouriteOrderItemBinding
import com.admin.ozieats_app.model.MyOrdersModel
import com.admin.ozieats_app.ui.orderprogress.OrderSummaryActivity
import com.admin.ozieats_app.utils.Preference
import com.admin.ozieats_app.utils.SharedPrefsManager
import com.bumptech.glide.Glide
import com.google.gson.Gson

class FavouriteOrderAdapter(
    private var context: Context,
    private var favoriteOrders: ArrayList<MyOrdersModel>,
    private var favouriteOrderViewModel: FavouriteOrderViewModel,
    private var onFavouriteButtonClick: (myOrderModel: MyOrdersModel, position: Int) -> Unit
) : RecyclerView.Adapter<FavouriteOrderAdapter.FavouriteOrderViewHolder>() {

    private var layoutInflater: LayoutInflater? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteOrderViewHolder {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.context)
        }
        val binding: FavouriteOrderItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.favourite_order_item,
            parent,
            false
        )
        return FavouriteOrderViewHolder(context, binding)
    }

    override fun getItemCount(): Int {
        return favoriteOrders.size
    }

    override fun onBindViewHolder(holder: FavouriteOrderViewHolder, position: Int) {
        holder.itemViewFavourite.myordersModel = favoriteOrders[position]

        Glide.with(context).load(favoriteOrders[position].restaurantImage)
            .into(holder.itemViewFavourite.imageViewOrderPhoto)

        holder.itemViewFavourite.favouriteIcon.setOnClickListener {

            /*var myOrderModel=MyOrdersModel()
            myOrderModel.status=favoriteOrders[position].status
            myOrderModel.request_id=favoriteOrders[position].request_id
            myOrderModel.restaurant_name=favoriteOrders[position].restaurant_name
            myOrderModel.orderDateAndTime=favoriteOrders[position].orderDateAndTime
            myOrderModel.restaurantImage=favoriteOrders[position].restaurantImage
            myOrderModel.restaurant_address=favoriteOrders[position].restaurant_address
            myOrderModel.orderId=favoriteOrders[position].orderId
            myOrderModel.totalPrice=favoriteOrders[position].totalPrice
            myOrderModel.tax=favoriteOrders[position].tax
            myOrderModel.lat=favoriteOrders[position].lat
            myOrderModel.lng=favoriteOrders[position].lng
            myOrderModel.orderStatus=favoriteOrders[position].orderStatus

            if (favoriteOrders[position].favourite == 1) {
                myOrderModel.favourite = 0
            } else {
                myOrderModel.favourite = 1
            }
            myOrderModel.orderItemModel=favoriteOrders[position].orderItemModel

            favoriteOrders.set(position, myOrderModel)*/

            onFavouriteButtonClick(favoriteOrders[holder.adapterPosition], holder.adapterPosition)

            notifyDataSetChanged()

        }

        holder.itemView.setOnClickListener {

            SharedPrefsManager.newInstance(context).putString(
                Preference.MY_ORDER,
                Gson().toJson(favoriteOrders[holder.adapterPosition])
            )
            SharedPrefsManager.newInstance(context).putBoolean(Preference.ISFAVOURITE, true)
            var intent = Intent(context, OrderSummaryActivity::class.java)
            context.startActivity(intent)

        }
    }


    class FavouriteOrderViewHolder(
        val context: Context,
        favouriteLayouts: FavouriteOrderItemBinding
    ) :
        RecyclerView.ViewHolder(favouriteLayouts.root) {
        val itemViewFavourite: FavouriteOrderItemBinding = favouriteLayouts
    }


}