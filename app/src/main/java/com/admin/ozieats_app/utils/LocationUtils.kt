package com.admin.ozieats_app.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import com.admin.ozieats_app.R
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices


class LocationUtils(private val activity: Activity) : LocationListener {

    private var mLocationManager: LocationManager? = null
    private var mLocationRequest: LocationRequest? = null
    private val sUINTERVAL = (2 * 1000).toLong()  /* 10 secs */
    private val sFINTERVAL: Long = 2000 /* 2 sec */
    private lateinit var locationManager: LocationManager
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

    // Code for make call back using interface from utility to activity
    private var pCallback: BaseActivity = BaseActivity()

    override fun onLocationChanged(location: Location) {
        //Call the function of interface define in Activity i.e MainActivity
        /*mLocation.latitude = location.latitude
        mLocation.longitude = location.longitude*/
        pCallback.updateUi(location, activity)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun onProviderEnabled(provider: String?) {
        TODO("Not yet implemented")
    }

    override fun onProviderDisabled(provider: String?) {
        TODO("Not yet implemented")
    }

    fun initLocation() {
        mLocationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        checkLocation()
        startLocationUpdates()
    }

    private fun checkLocation(): Boolean {
        if (!isLocationEnabled()) {
            showAlert(
                activity.getString(R.string.location_title),
                activity.getString(R.string.location_message)
            )
            return isLocationEnabled()
        }
        return isLocationEnabled()
    }

    private fun isLocationEnabled(): Boolean {
        locationManager =
            activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun showAlert(pTitle: String, pMessage: String) {
        val dialog = AlertDialog.Builder(activity)
        dialog.apply {
            setTitle(pTitle)
            setMessage(pMessage)
            setPositiveButton(activity.getString(R.string.location_settings)) { _, _ ->
                val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                activity.startActivity(myIntent)
            }
        }.show()
    }

    private fun startLocationUpdates() {
        mLocationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = sUINTERVAL
            fastestInterval = sFINTERVAL
        }
    }
}