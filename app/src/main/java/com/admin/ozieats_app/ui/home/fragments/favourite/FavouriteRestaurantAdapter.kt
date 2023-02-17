package com.admin.ozieats_app.ui.home.fragments.favourite

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.admin.ozieats_app.R
import com.admin.ozieats_app.databinding.RestaurantItemLayoutBinding
import com.admin.ozieats_app.model.RestaurantModel
import com.admin.ozieats_app.ui.home.fragments.home.HomeViewModel
import com.admin.ozieats_app.ui.home.restaurantDetails.RestaurantDetailsActivity
import com.admin.ozieats_app.utils.Preference
import com.admin.ozieats_app.utils.SendIntents
import com.admin.ozieats_app.utils.SharedPrefsManager
import com.admin.ozieats_app.utils.gone
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.android.synthetic.main.restaurant_item_layout.view.*

class FavouriteRestaurantAdapter(
    private var context: Context,
    private var restaurants: ArrayList<RestaurantModel>,
    private var homeViewModel: HomeViewModel,
    private var favouritePlacesViewModel: FavouritePlacesViewModel,
    private var onFavouriteButtonClick: (restaurantModel: RestaurantModel, position: Int) -> Unit

) : RecyclerView.Adapter<FavouriteRestaurantAdapter.RestaurantViewHolder>() {

    private var layoutInflater: LayoutInflater? = null

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

        println("fmbdhdsjf" + restaurants[position])
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
        holder.itemViewRestaurant.favouriteAddOrRemove.setImageResource(R.drawable.heart_active)

        holder.itemViewRestaurant.constraintRestaurantItem.setOnClickListener {

            SharedPrefsManager.newInstance(context)
                .putInt(Preference.RESTAURANT_ID, restaurants[position].restaurant_id)

            SharedPrefsManager.newInstance(context)
                .putString(Preference.RESTAURANTS, Gson().toJson(restaurants[position]))

            val intent = Intent(context, RestaurantDetailsActivity::class.java)
            intent.putExtra(SendIntents.RESTAURANT_NAME, restaurants[position].restaurant_name)
            intent.putExtra(SendIntents.RESTAURANT_ADDRESS, restaurants[position].delivery_address)
            intent.putExtra(SendIntents.RESTAURANT_IMAGE, restaurants[position].restaurant_image)
            intent.putExtra(
                SendIntents.RESTAURANT_DISCOUNT,
                restaurants[position].restaurant_discount
            )
            intent.putExtra(
                SendIntents.RESTAURANT_DELIVERY_CHARGE,
                restaurants[position].delivery_charges
            )
            intent.putExtra(SendIntents.RESTAURANT_ID, restaurants[position].restaurant_id)
            intent.putExtra(SendIntents.FAVOURITE, restaurants[position].favourites)
            intent.putExtra(SendIntents.TOTAL_RATING, restaurants[position].totalRating)
            intent.putExtra(SendIntents.RATING, restaurants[position].totalUserRating)
            intent.putExtra(SendIntents.BACKGROUND_IMAGE, restaurants[position].background_image)
            context.startActivity(intent)
        }

        holder.itemView.favouriteAddOrRemove.setOnClickListener {

            onFavouriteButtonClick(restaurants[position], holder.adapterPosition)
        }
    }


    class RestaurantViewHolder(
        val context: Context,
        restaurantLayouts: RestaurantItemLayoutBinding
    ) :
        RecyclerView.ViewHolder(restaurantLayouts.root) {
        val itemViewRestaurant: RestaurantItemLayoutBinding = restaurantLayouts
    }


}