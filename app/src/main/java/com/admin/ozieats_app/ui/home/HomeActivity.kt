package com.admin.ozieats_app.ui.home

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.admin.ozieats_app.R
import com.admin.ozieats_app.databinding.ActivityHomeBinding
import com.admin.ozieats_app.ui.home.fragments.favourite.FavouriteFragment
import com.admin.ozieats_app.ui.home.fragments.home.HomeFragment
import com.admin.ozieats_app.ui.home.fragments.menu.MenuFragment
import com.admin.ozieats_app.ui.home.fragments.myorders.MyOrdersFragment
import com.admin.ozieats_app.ui.home.item.NetworkConnectionListener
import com.admin.ozieats_app.utils.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.constraintLayoutMain
import kotlinx.android.synthetic.main.activity_home.noInternetLayout
import kotlinx.android.synthetic.main.activity_restaurant_details.*
import kotlinx.android.synthetic.main.no_internet_layout.view.*


class HomeActivity : BaseActivity(), NetworkConnectionListener {

    lateinit var binding: ActivityHomeBinding
    lateinit var homeViewModel: HomeViewModel
    lateinit var firebaseDatabase: FirebaseDatabase
    var networkConnectionListener: NetworkConnectionListener? = null
    var isInternetConnectd = true
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mLocationRequest: LocationRequest? = null
    private val FASTEST_UPDATE_INTERVAL: Long = 30000
    private val MAX_WAIT_TIME: Long = 60000
    private val UPDATE_INTERVAL: Long = 60000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_home)

        homeViewModel =
            ViewModelProvider(this, HomeViewModel.HomeModelFactory(this)).get(
                HomeViewModel::class.java
            )

        binding.homeListener = homeViewModel

        networkConnectionListener = this

        val exploreFragment = HomeFragment.newInstance()
        homeViewModel.openFragment(exploreFragment)
        mFusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)

        binding.navigationView.setOnNavigationItemSelectedListener(homeViewModel.mOnNavigationItemSelectedListener)

        createLocationRequest()
    }

    override fun changeNetwork(connected: Boolean) {

        isInternetConnectd = connected

        if (connected) {
            if (!constraintLayoutMain.isVisible()) {
                noInternetLayout.retryButton.setOnClickListener {

                    Log.e("dgkjsfhgsfkjg", "changeNetwork: " + connected)
                    noInternetLayout.gone()
                    constraintLayoutMain.visible()

                    if (GlobalVariables.bottomMenuIndex == 0) {
                        val exploreFragment = HomeFragment.newInstance()
                        homeViewModel.openFragment(exploreFragment)
                        binding.navigationView.setOnNavigationItemSelectedListener(homeViewModel.mOnNavigationItemSelectedListener)

                    } else if (GlobalVariables.bottomMenuIndex == 1) {
                        val exploreFragment = MyOrdersFragment.newInstance()
                        homeViewModel.openFragment(exploreFragment)
                        binding.navigationView.setOnNavigationItemSelectedListener(homeViewModel.mOnNavigationItemSelectedListener)
                    } else if (GlobalVariables.bottomMenuIndex == 2) {
                        val exploreFragment = FavouriteFragment.newInstance()
                        homeViewModel.openFragment(exploreFragment)
                        binding.navigationView.setOnNavigationItemSelectedListener(homeViewModel.mOnNavigationItemSelectedListener)
                    } else if (GlobalVariables.bottomMenuIndex == 3) {
                        val exploreFragment = MenuFragment.newInstance()
                        homeViewModel.openFragment(exploreFragment)
                        binding.navigationView.setOnNavigationItemSelectedListener(homeViewModel.mOnNavigationItemSelectedListener)
                    }
                }
            }
        } else {
            Log.e("dzglkdjlkj=====>", "changeNetwork: " + connected)
            noInternetLayout.visible()
            constraintLayoutMain.gone()
        }
    }

    override fun onResume() {
        super.onResume()

        if (isInternetConnectd) {
            if (!constraintLayoutMain.isVisible()) {
                noInternetLayout.gone()
                constraintLayoutMain.visible()
            }
        }
    }


    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest?.interval = 60000
        mLocationRequest?.fastestInterval = FASTEST_UPDATE_INTERVAL
        mLocationRequest?.priority = 100
        mLocationRequest?.maxWaitTime = 60000
        Log.e("UpdateLocation", "createLocationRequest: ")
        locationUpdateRequest()
    }

    private fun locationUpdateRequest() {
        try {
            Log.i("requestLocationUpdates", "Starting location updates")
            Utils.setRequestingLocationUpdates(this, true)
            mFusedLocationClient!!.requestLocationUpdates(
                mLocationRequest,
                getPendingIntent()
            )
        } catch (e: SecurityException) {
            Log.e("UpdateLocation", "SecurityException: " + e.message)
            Utils.setRequestingLocationUpdates(this, false)
            e.printStackTrace()
        }
    }

    private fun getPendingIntent(): PendingIntent? {
        Log.e("getPendingIntent", "getPendingIntent: ")
        val intent = Intent(this@HomeActivity, LocationUpdatesBroadcastReceiver::class.java)
        intent.action = LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun updateUi(location: Location, context: Context) {

        Log.e("LOCATIONS", "updateUi: " + location.latitude + "------" + location.longitude)
        SharedPrefsManager.newInstance(context)
            .putFloat(Preference.PREF_LAT, location.latitude.toFloat())
        SharedPrefsManager.newInstance(context)
            .putFloat(Preference.PREF_LNG, location.longitude.toFloat())
    }

 /*   override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }*/
}