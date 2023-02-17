package com.admin.ozieats_app.ui.home.fragments.searchproducts

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.admin.ozieats_app.R
import com.admin.ozieats_app.model.CategoriesModel
import com.admin.ozieats_app.model.RestaurantModel
import com.admin.ozieats_app.utils.getTotalRestaurantPreference
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_search_food_list.view.*

class FoodCategoriesAdapter(
    private val mContext: Context,
    private val mRestaurantList: ArrayList<CategoriesModel>,
    var onCategoryClick : (restaurantArray:ArrayList<RestaurantModel>) -> Unit,
    var noDataFound : () -> Unit
) : RecyclerView.Adapter<FoodCategoriesAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_search_food_list,
                    parent,
                    false
                )
        )
    }

    override fun getItemCount(): Int {
        return mRestaurantList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(mContext).asBitmap().load(mRestaurantList[position].cuisine_image)
            .into(holder.itemView.iv_restaurant)

        holder.itemView.tv_foodName.text = mRestaurantList[position].categoriesName

        holder.itemView.setOnClickListener {

            var resta=getTotalRestaurantPreference(mContext)

            if (resta.size>0)
            {
                val restaurantList=ArrayList<RestaurantModel>()
                for (data in resta)
                {
                    for (cuisine_name in data.cuisines!!)
                    {
                        if (cuisine_name.name==mRestaurantList[position].categoriesName)
                        {

                            Log.e("fhkbjfshgkjs", "onBindViewHolder: "+cuisine_name.name)
                            restaurantList.add(data)

                            break

                        }else
                        {
                            noDataFound()
                        }
                    }
                }
                onCategoryClick(restaurantList)
            }else
            {
                noDataFound()
            }
        }

    }
}