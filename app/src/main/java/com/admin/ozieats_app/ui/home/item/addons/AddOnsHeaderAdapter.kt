package com.admin.ozieats_app.ui.home.item.addons

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.admin.ozieats_app.R
import com.admin.ozieats_app.databinding.AddonsHeaderLayoutBinding
import com.admin.ozieats_app.model.AddOnsHeaderModel
import com.admin.ozieats_app.utils.getAddOnsFromPreference
import kotlinx.android.synthetic.main.addons_header_layout.view.*


class AddOnsHeaderAdapter(
    private var context: Context,
    private var listner: AddOnsChildAdapter.OnPriceTotalChange,
    private var addOnsList: ArrayList<AddOnsHeaderModel>
) : RecyclerView.Adapter<AddOnsHeaderAdapter.AddOnsHeaderViewHolder>() {

    private var layoutInflater: LayoutInflater? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddOnsHeaderViewHolder {

        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.context)
        }
        val binding: AddonsHeaderLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.addons_header_layout,
            parent,
            false
        )
        return AddOnsHeaderViewHolder(
            context,
            binding
        )
    }

    override fun getItemCount(): Int {
        return addOnsList.size
    }

    override fun onBindViewHolder(holder: AddOnsHeaderViewHolder, position: Int) {
        holder.itemViewMenuItems.addOnHeader = addOnsList[position]

        Log.e(
            "AddOnsHeaderViewHolder",
            "onBindViewHolder: ==> " + getAddOnsFromPreference(context)[position].add_on_category
        )

        Log.e(
            "itemChildAdd",
            "onBindViewHolder: " + getAddOnsFromPreference(context)[position].add_on_category!!.size
        )
        val adapter =
            AddOnsChildAdapter(context, position, addOnsList[position].add_on_category!!, listner)
        holder.itemView.recyclerViewAddonSubName.itemAnimator = null
        holder.itemView.recyclerViewAddonSubName.setHasFixedSize(true)
        holder.itemView.recyclerViewAddonSubName.isNestedScrollingEnabled = false
        holder.itemView.recyclerViewAddonSubName.adapter = adapter
    }

    class AddOnsHeaderViewHolder(
        val context: Context,
        menuItemLayout: AddonsHeaderLayoutBinding
    ) :
        RecyclerView.ViewHolder(menuItemLayout.root) {
        val itemViewMenuItems: AddonsHeaderLayoutBinding = menuItemLayout
    }


}