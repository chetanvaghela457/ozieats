package com.admin.ozieats_app.ui.home.fragments.favourite

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter


class PagerAdapter(private var fm: FragmentManager, private var tabs: Int) :
    FragmentStatePagerAdapter(fm, tabs) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                PlaceFavouriteFragment()
            }
            1 -> {
                OrderFavouriteFragment()
            }
            else -> PlaceFavouriteFragment()
        }
    }

    override fun getCount(): Int {
        return tabs
    }
}