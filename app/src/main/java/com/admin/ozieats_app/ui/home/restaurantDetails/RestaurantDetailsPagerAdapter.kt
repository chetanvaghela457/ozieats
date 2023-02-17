package com.admin.ozieats_app.ui.home.restaurantDetails

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.admin.ozieats_app.ui.home.restaurantDetails.info.InfoFragment
import com.admin.ozieats_app.ui.home.restaurantDetails.menu.MenuItemFragment
import com.admin.ozieats_app.ui.home.restaurantDetails.review.ReviewFragment


class RestaurantDetailsPagerAdapter(
    private var fm: FragmentManager,
    private var tabs: Int,
    private var restaurantDetailsActivity: RestaurantDetailsActivity
) :
    FragmentStatePagerAdapter(fm, tabs) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                MenuItemFragment.newInstance()
            }
            1 -> {
                ReviewFragment.newInstance()
            }
            2 -> {
                InfoFragment.newInstance()
            }
            else -> MenuItemFragment.newInstance()
        }
    }

    override fun getCount(): Int {
        return tabs
    }
}