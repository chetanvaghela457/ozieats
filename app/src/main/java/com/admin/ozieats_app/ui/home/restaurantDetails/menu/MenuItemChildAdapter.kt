package com.admin.ozieats_app.ui.home.restaurantDetails.menu

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.admin.ozieats_app.R
import com.admin.ozieats_app.databinding.MenuItemChildLayoutBinding
import com.admin.ozieats_app.model.MenuItemChildModel
import com.admin.ozieats_app.ui.home.item.ItemDetailsActivity
import com.admin.ozieats_app.utils.Preference
import com.admin.ozieats_app.utils.SendIntents
import com.admin.ozieats_app.utils.SharedPrefsManager
import com.bumptech.glide.Glide
import com.google.gson.Gson


class MenuItemChildAdapter(
    private var context: Context,
    private var menuItemChilds: ArrayList<MenuItemChildModel>
) : RecyclerView.Adapter<MenuItemChildAdapter.MenuItemChildViewHolder>() {

    private var layoutInflater: LayoutInflater? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemChildViewHolder {

        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.context)
        }
        val binding: MenuItemChildLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.menu_item_child_layout,
            parent,
            false
        )
        return MenuItemChildViewHolder(
            context,
            binding
        )
    }

    override fun getItemCount(): Int {
        return menuItemChilds.size
    }

    override fun onBindViewHolder(holder: MenuItemChildViewHolder, position: Int) {
        holder.itemViewMenuItemsChild.menuItemChildModel = menuItemChilds[position]

        Glide.with(context).asBitmap().load(menuItemChilds[position].itemImage)
            .into(holder.itemViewMenuItemsChild.imageViewItemImage)

        holder.itemViewMenuItemsChild.itemClickConstraint.setOnClickListener {

            val intent=Intent(context,ItemDetailsActivity::class.java)
            intent.putExtra(SendIntents.ITEMDETAILS,menuItemChilds[position])
            intent.putExtra(SendIntents.CHECK_PATH,true)

            SharedPrefsManager.newInstance(context).putString(Preference.ADD_ONS,Gson().toJson(menuItemChilds[position].add_ons))
            SharedPrefsManager.newInstance(context).putString(Preference.CHILD_DATA,Gson().toJson(menuItemChilds[position]))

            for (array in menuItemChilds[position].add_ons!!)
            {
                Log.e("Gxdlfkgjfkg", "onBindViewHolder:---- "+array.add_on_category!![0].addon_category)
            }

            context.startActivity(intent)
//            (context as Activity).finish()
        }
    }

    class MenuItemChildViewHolder(
        val context: Context,
        menuItemChildLayout: MenuItemChildLayoutBinding
    ) :
        RecyclerView.ViewHolder(menuItemChildLayout.root) {
        val itemViewMenuItemsChild: MenuItemChildLayoutBinding = menuItemChildLayout


    }
}