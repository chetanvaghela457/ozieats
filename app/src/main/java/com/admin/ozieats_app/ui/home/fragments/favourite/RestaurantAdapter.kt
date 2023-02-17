package com.admin.ozieats_app.ui.home.fragments.favourite

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.admin.ozieats_app.R
import com.admin.ozieats_app.databinding.RestaurantItemLayoutBinding
import com.admin.ozieats_app.model.RestaurantModel
import com.admin.ozieats_app.ui.home.fragments.home.HomeViewModel
import com.admin.ozieats_app.utils.*
import com.bumptech.glide.Glide
import com.google.gson.Gson

class RestaurantAdapter(
    private var context: Context,
    private var restaurants: ArrayList<RestaurantModel>,
    private var homeViewModel: HomeViewModel,
    var onRestaurantClick: (restaurantData: RestaurantModel) -> Unit,
    var onFavouriteButtonClick: (restaurantData: RestaurantModel, isFav: Int) -> Unit
) : RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {

    private var layoutInflater: LayoutInflater? = null
    private var mLastClickTime: Long = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.context)
        }
        val binding: RestaurantItemLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.restaurant_item_layout,
            parent,
            false
        )
        binding.homeViewListener = homeViewModel
        return RestaurantViewHolder(context, binding)
    }

    override fun getItemCount(): Int {
        return restaurants.size
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {

        holder.itemViewRestaurant.restaurantModel = restaurants[position]

        Glide.with(context).asBitmap().load(restaurants[position].restaurant_image)
            .into(holder.itemViewRestaurant.restaurantLogo)

        Glide.with(context).asBitmap().load(restaurants[position].background_image)
            .into(holder.itemViewRestaurant.imageViewMainBacgroundImage)

        if (restaurants[position].delivery_charges.isEmpty())
        {
            holder.itemViewRestaurant.textViewDeliveryCharges.gone()
        }

        if (restaurants[position].delivery_address.isEmpty())
        {
            holder.itemViewRestaurant.textViewAddress.gone()
        }

        holder.itemViewRestaurant.mainRatingbar.rating = restaurants[position].totalUserRating

        holder.itemViewRestaurant.constraintRestaurantItem.setOnClickListener {

            if (isOpenRecently()) return@setOnClickListener

            onRestaurantClick(restaurants[holder.adapterPosition])

            SharedPrefsManager.newInstance(context)
                .putInt(Preference.RESTAURANT_ID, restaurants[position].restaurant_id)

            SharedPrefsManager.newInstance(context)
                .putString(Preference.RESTAURANTS, Gson().toJson(restaurants[position]))

        }

        if (restaurants[position].favourites == 1) {
            holder.itemViewRestaurant.favouriteAddOrRemove.setBackgroundResource(R.drawable.heart_active)
        } else {
            holder.itemViewRestaurant.favouriteAddOrRemove.setBackgroundResource(R.drawable.heart_inactive)
        }

        holder.itemViewRestaurant.favouriteAddOrRemove.setOnClickListener {

            if (isOpenRecently()) return@setOnClickListener

            val updatedModel = RestaurantModel()
            updatedModel.restaurant_id = restaurants[position].restaurant_id
            updatedModel.restaurant_name = restaurants[position].restaurant_name
            updatedModel.restaurant_image = restaurants[position].restaurant_image
            updatedModel.background_image = restaurants[position].background_image
            updatedModel.restaurant_category = restaurants[position].restaurant_category
            updatedModel.delivery_charges = restaurants[position].delivery_charges
            updatedModel.delivery_address = restaurants[position].delivery_address
            updatedModel.restaurant_discount = restaurants[position].restaurant_discount
            updatedModel.totalUserRating = restaurants[position].totalUserRating
            updatedModel.totalRating = restaurants[position].totalRating
            updatedModel.restaurant_distance = restaurants[position].restaurant_distance
            updatedModel.lat = restaurants[position].lat
            updatedModel.lng = restaurants[position].lng
            updatedModel.opening_time = restaurants[position].opening_time
            updatedModel.closing_time = restaurants[position].closing_time
            updatedModel.weekend_opening_time = restaurants[position].weekend_opening_time
            updatedModel.weekend_closing_time = restaurants[position].weekend_closing_time
            updatedModel.phone = restaurants[position].phone
            updatedModel.open_status = restaurants[position].open_status
            if (restaurants[position].favourites == 1) {
                updatedModel.favourites = 0
            } else {
                updatedModel.favourites = 1
            }
            updatedModel.cuisines = restaurants[position].cuisines
            updatedModel.food_list = restaurants[position].food_list

            restaurants.set(position, updatedModel)

            onFavouriteButtonClick(restaurants[position], updatedModel.favourites)
            notifyDataSetChanged()
        }
    }

    fun setData(ranges: ArrayList<RestaurantModel>) {
        this.restaurants = ranges
        notifyDataSetChanged()
    }

    class RestaurantViewHolder(
        val context: Context,
        restaurantLayouts: RestaurantItemLayoutBinding
    ) :
        RecyclerView.ViewHolder(restaurantLayouts.root) {
        val itemViewRestaurant: RestaurantItemLayoutBinding = restaurantLayouts
    }

}