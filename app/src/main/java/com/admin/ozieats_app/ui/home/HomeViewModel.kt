package com.admin.ozieats_app.ui.home

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.admin.ozieats_app.R
import com.admin.ozieats_app.ui.home.fragments.favourite.FavouriteFragment
import com.admin.ozieats_app.ui.home.fragments.home.HomeFragment
import com.admin.ozieats_app.ui.home.fragments.menu.MenuFragment
import com.admin.ozieats_app.ui.home.fragments.myorders.MyOrdersFragment
import com.admin.ozieats_app.utils.GlobalVariables
import com.admin.ozieats_app.utils.isOpenRecently
import com.google.android.material.bottomnavigation.BottomNavigationView


class HomeViewModel(private var context: Context):ViewModel() {

    private var context1=context.applicationContext

    val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.explore -> {

                if(!isOpenRecently()){
                    GlobalVariables.bottomMenuIndex=0
                    val exploreFragment = HomeFragment.newInstance()
//                val exploreFragment = PickUpOrderFragment.newInstance()
                    openFragment(exploreFragment)
                    return@OnNavigationItemSelectedListener true
                }

            }
            R.id.my_orders -> {
                if(!isOpenRecently()) {
                    GlobalVariables.bottomMenuIndex=1
                    val bundle = Bundle()
                    bundle.putBoolean("backButtonHide", true)
                    val myordersFragment = MyOrdersFragment.newInstance()
                    myordersFragment.arguments = bundle
                    openFragment(myordersFragment)

                    return@OnNavigationItemSelectedListener true
                }
            }
            R.id.favourite -> {
                GlobalVariables.bottomMenuIndex=2
                val favouriteFragment = FavouriteFragment.newInstance()
                openFragment(favouriteFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.menu -> {
                GlobalVariables.bottomMenuIndex=3
                val menuFragment = MenuFragment.newInstance()
                openFragment(menuFragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    fun openFragment(fragment: Fragment) {
        val transaction = (context as HomeActivity).supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
//        transaction.addToBackStack(null)
        transaction.commit()
    }

    class HomeModelFactory(private val context: Context) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return HomeViewModel(context) as T
        }
    }
}