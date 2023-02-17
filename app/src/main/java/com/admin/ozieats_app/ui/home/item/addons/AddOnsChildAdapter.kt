package com.admin.ozieats_app.ui.home.item.addons

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.admin.ozieats_app.R
import com.admin.ozieats_app.databinding.AddonsChildLayoutBinding
import com.admin.ozieats_app.model.AddOnChildModel
import kotlinx.android.synthetic.main.addons_child_layout.view.*


class AddOnsChildAdapter(
    private var context: Context,
    private val headerIndex: Int,
    private var addOnChildList: ArrayList<AddOnChildModel>,
    private var listener: OnPriceTotalChange
) : RecyclerView.Adapter<AddOnsChildAdapter.AddOnsChildViewHolder>() {

    private var layoutInflater: LayoutInflater? = null
    private var mSelectedItem = -1
    private var defaultPrice = "0"


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddOnsChildViewHolder {

        Log.e("Check_price", "onCreateViewHolder")

        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.context)
        }
        val binding: AddonsChildLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.addons_child_layout,
            parent,
            false
        )
        return AddOnsChildViewHolder(
            context,
            binding
        )
    }

    override fun getItemCount(): Int {
        return addOnChildList.size
    }

    override fun onBindViewHolder(holder: AddOnsChildViewHolder, position: Int) {

        if (addOnChildList[holder.adapterPosition].addon_price == "0") {
            holder.itemViewMenuItemsChild.radioAdOnsChildButton.text =
                addOnChildList[holder.adapterPosition].addon_category
        } else {
            holder.itemViewMenuItemsChild.radioAdOnsChildButton.text =
                addOnChildList[holder.adapterPosition].addon_category + " ($" + addOnChildList[holder.adapterPosition].addon_price + ")"
        }

        if (addOnChildList[position].addon_price == defaultPrice) {
            mSelectedItem = holder.adapterPosition
        }

        holder.itemView.radioAdOnsChildButton.setOnClickListener {
            defaultPrice = addOnChildList[position].addon_price
            mSelectedItem = holder.adapterPosition
            listener.onTotalChange(
                headerIndex,
                addOnChildList[holder.adapterPosition].addon_price.toInt()
            )
            notifyDataSetChanged()
        }

        holder.itemViewMenuItemsChild.radioAdOnsChildButton.isChecked =
            holder.adapterPosition == mSelectedItem

    }

    class AddOnsChildViewHolder(
        val context: Context,
        menuItemChildLayout: AddonsChildLayoutBinding
    ) :
        RecyclerView.ViewHolder(menuItemChildLayout.root) {
        val itemViewMenuItemsChild: AddonsChildLayoutBinding = menuItemChildLayout
    }

    interface OnPriceTotalChange {
        fun onTotalChange(headerIndex: Int, price: Int)
    }

}