package com.admin.ozieats_app.ui.home.restaurantDetails.menu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.admin.ozieats_app.R
import com.admin.ozieats_app.databinding.MenuItemParentLayoutBinding
import com.admin.ozieats_app.model.MenuItemModel
import com.admin.ozieats_app.utils.runAnimationAgain


class MenuItemAdapter(
    private var context: Context,
    private var menuItems: ArrayList<MenuItemModel>
) : RecyclerView.Adapter<MenuItemAdapter.MenuItemViewHolder>() {

    private var layoutInflater: LayoutInflater? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemViewHolder {

        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.context)
        }
        val binding: MenuItemParentLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.menu_item_parent_layout,
            parent,
            false
        )
        return MenuItemViewHolder(
            context,
            binding
        )
    }

    override fun getItemCount(): Int {
        return menuItems.size
    }

    override fun onBindViewHolder(holder: MenuItemViewHolder, position: Int) {
        holder.itemViewMenuItems.menuItemModel = menuItems[position]

        holder.itemViewMenuItems.parentConstraint.setOnClickListener {

            if (holder.itemViewMenuItems.menuItemChildRecycler.visibility == View.VISIBLE) {
                holder.itemViewMenuItems.menuItemChildRecycler.visibility = View.GONE
                holder.itemViewMenuItems.imageViewArrow.setImageDrawable(
                    context.resources.getDrawable(
                        R.drawable.arrow_down
                    )
                )
            } else {

                holder.itemViewMenuItems.imageViewArrow.setImageDrawable(
                    context.resources.getDrawable(
                        R.drawable.arrow_up
                    )
                )
                holder.itemViewMenuItems.menuItemChildRecycler.visibility = View.VISIBLE
                val adapter =
                    MenuItemChildAdapter(context, menuItems[position].menuItemChildModel!!)
                holder.itemViewMenuItems.menuItemChildRecycler.adapter = adapter
                holder.itemViewMenuItems.menuItemChildRecycler.setHasFixedSize(true)
                holder.itemViewMenuItems.menuItemChildRecycler.isNestedScrollingEnabled = false
                runAnimationAgain(context,holder.itemViewMenuItems.menuItemChildRecycler)
                adapter.notifyDataSetChanged()

            }


        }

    }

    class MenuItemViewHolder(
        val context: Context,
        menuItemLayout: MenuItemParentLayoutBinding
    ) :
        RecyclerView.ViewHolder(menuItemLayout.root) {
        val itemViewMenuItems: MenuItemParentLayoutBinding = menuItemLayout
    }
}