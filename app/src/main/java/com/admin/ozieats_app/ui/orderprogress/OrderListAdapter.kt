package com.admin.ozieats_app.ui.orderprogress

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.admin.ozieats_app.R

// TODO: 15-Jun-20 Set another parameter for show order detail
class OrderListAdapter(private val mContext: Context) :
    RecyclerView.Adapter<OrderListAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_order_list,
                    parent,
                    false
                )
        )
    }

    override fun getItemCount(): Int {
        // TODO: 15-Jun-20 Set the list size
        return 0
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

    }

}