package com.admin.ozieats_app.ui.home.fragments.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.admin.ozieats_app.R
import com.admin.ozieats_app.model.AddressModel
import kotlinx.android.synthetic.main.recent_address_layout.view.*

class RecentLocationsAdapter(
    private val mContext: Context,
    private var addressList: ArrayList<AddressModel>,
    var onAddressClick: (addressModel: AddressModel) -> Unit
) : RecyclerView.Adapter<RecentLocationsAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.recent_address_layout,
                    parent,
                    false
                )
        )
    }

    override fun getItemCount(): Int {
        return addressList.size
    }

    fun setData(ranges: ArrayList<AddressModel>) {
        this.addressList = ranges
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.itemView.textViewAddressName.text = addressList[position].addressSingleLine

        holder.itemView.textViewAddressName.setOnClickListener {

            onAddressClick(addressList[holder.adapterPosition])

        }
    }
}