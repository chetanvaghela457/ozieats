package com.admin.ozieats_app.ui.home.fragments.home

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.admin.ozieats_app.R
import com.admin.ozieats_app.databinding.AdvertisementItemLayoutBinding
import com.admin.ozieats_app.model.AdvertisementModel
import com.admin.ozieats_app.model.MenuItemChildModel
import com.admin.ozieats_app.ui.home.item.ItemDetailsActivity
import com.admin.ozieats_app.utils.Preference
import com.admin.ozieats_app.utils.SendIntents
import com.admin.ozieats_app.utils.SharedPrefsManager
import com.bumptech.glide.Glide
import com.google.gson.Gson

class AdvertisementAdapter(
    private var context: Context,
    private var advertisement: ArrayList<AdvertisementModel>
) : RecyclerView.Adapter<AdvertisementAdapter.AdvertisementViewHolder>() {

    private var layoutInflater: LayoutInflater? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdvertisementViewHolder {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.context)
        }
        val binding: AdvertisementItemLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.advertisement_item_layout,
            parent,
            false
        )
        return AdvertisementViewHolder(context, binding)
    }

    override fun getItemCount(): Int {
        return advertisement.size
    }

    override fun onBindViewHolder(holder: AdvertisementViewHolder, position: Int) {
        holder.itemViewAdvertisement.advertisementModel = advertisement[position]

        holder.itemViewAdvertisement.restaurantRatings.rating =
            advertisement[position].restaurant_rating.toFloat()
        holder.itemViewAdvertisement.textViewItemPrice.text =
            "$" + advertisement[position].restaurant_item_price.toString()

        Glide.with(context).asBitmap().load(advertisement[position].restaurant_item_image)
            .into(holder.itemViewAdvertisement.imageViewItemImage)

        holder.itemViewAdvertisement.mainAdvertise.setOnClickListener {

            /*SharedPrefsManager.newInstance(context).putString(
                Preference.ADVERTISMENT_ITEM, Gson().toJson(
                    advertisement[position]
                )
            )*/


            var menuItemChildModel=MenuItemChildModel()
            menuItemChildModel.food_id=advertisement[position].item_id
            menuItemChildModel.itemName=advertisement[position].restaurant_item_name
            menuItemChildModel.itemImage=advertisement[position].restaurant_item_image
            menuItemChildModel.itemPrice=advertisement[position].restaurant_item_price.toDouble()

            SharedPrefsManager.newInstance(context).putString(Preference.CHILD_DATA,Gson().toJson(menuItemChildModel))
            SharedPrefsManager.newInstance(context).putInt(Preference.RESTAURANT_ID, advertisement[position].restaurant_id)
            SharedPrefsManager.newInstance(context)
                .putString(Preference.ADD_ONS, Gson().toJson(advertisement[position].add_ons))
            val intent = Intent(context, ItemDetailsActivity::class.java)
            intent.putExtra(SendIntents.CHECK_PATH, false)
            context.startActivity(intent)
        }
    }


    class AdvertisementViewHolder(
        val context: Context,
        advertisementLayouts: AdvertisementItemLayoutBinding
    ) :
        RecyclerView.ViewHolder(advertisementLayouts.root) {
        val itemViewAdvertisement: AdvertisementItemLayoutBinding = advertisementLayouts
    }
}