package com.admin.ozieats_app.utils

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.admin.ozieats_app.ui.home.item.NetworkConnectionListener
import com.admin.ozieats_app.utils.ConnectivityChangeReceiver.OnConnectivityChangedListener
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import java.util.*


open class BaseActivity : AppCompatActivity(), OnConnectivityChangedListener,
    NetworkConnectionListener, LocationListener, LocationCallback {

    lateinit var locationUtils: LocationUtils
    private var mLocationManager: LocationManager? = null
    private var connectivityChangeReceiver: ConnectivityChangeReceiver? = null
    var networkInterface: NetworkConnectionListener? = null
    lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        networkInterface = this
        connectivityChangeReceiver = ConnectivityChangeReceiver(this)
        locationCallback = this
        val filter = IntentFilter()
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(connectivityChangeReceiver, filter)

        locationUtils = LocationUtils(this@BaseActivity)

        val permissions =
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        val rationale =
            "Please provide location permission so that you can checkout the near by supplier."
        val options =
            Permissions.Options()
                .setRationaleDialogTitle("Permission")
                .setSettingsDialogTitle("Warning")
        Permissions.check(
            this@BaseActivity,
            permissions,
            rationale,
            options,
            object : PermissionHandler() {
                override fun onGranted() {
                    locationUtils.initLocation()
                    mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    val locationProvider = LocationManager.NETWORK_PROVIDER

                    if (ActivityCompat.checkSelfPermission(
                            this@BaseActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            this@BaseActivity,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                    val lastlocation = mLocationManager!!.getLastKnownLocation(locationProvider)
                    if (lastlocation != null) {
                        SharedPrefsManager.newInstance(this@BaseActivity)
                            .putFloat(Preference.PREF_LAT, lastlocation.latitude.toFloat())
                        SharedPrefsManager.newInstance(this@BaseActivity)
                            .putFloat(Preference.PREF_LNG, lastlocation.longitude.toFloat())
                    }
                }

                override fun onDenied(
                    context: Context,
                    deniedPermissions: ArrayList<String>
                ) {
                    super.onDenied(context, deniedPermissions)
                    if (deniedPermissions[0] == permissions[0]) {
                        val ad =
                            AlertDialog.Builder(this@BaseActivity).create()
                        ad.setCancelable(false) // This blocks the 'BACK' button
                        ad.setMessage("Please allow the location permission to checkout the near by supplier.")
                        ad.setButton(
                            DialogInterface.BUTTON_POSITIVE,
                            "OK"
                        ) { dialog, which ->
                            dialog.dismiss()
                            finish()
                        }
                        ad.show()
                    }
                }
            })
    }

    override fun onConnectivityChanged(isConnected: Boolean) {

        networkInterface?.changeNetwork(isConnected)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(connectivityChangeReceiver)
    }

    override fun changeNetwork(connected: Boolean) {
        Log.e("NETWORKCHANGED", "changeNetwork: " + connected)
    }

    override fun onLocationChanged(location: Location?) {

        Log.e(
            "LOCATIONS",
            "updateUi-------->: " + location!!.latitude + "------" + location.longitude
        )
        if (location != null) {
            locationCallback.updateUi(location, this)
        }

        if (location != null) {
            SharedPrefsManager.newInstance(this)
                .putFloat(Preference.PREF_LAT, location.latitude.toFloat())
            SharedPrefsManager.newInstance(this)
                .putFloat(Preference.PREF_LNG, location.longitude.toFloat())
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        Log.e("onStatusChanged===>", "onStatusChanged: " + status + "-------" + provider)
    }

    override fun onProviderEnabled(provider: String?) {
        Log.e("onProviderEnabled===>", "onProviderEnabled: " + provider)
    }

    override fun onProviderDisabled(provider: String?) {
        Log.e("onProviderDisabled===>", "onProviderEnabled: " + provider)
    }

    override fun updateUi(location: Location, context: Context) {
        SharedPrefsManager.newInstance(context)
            .putFloat(Preference.PREF_LAT, location.latitude.toFloat())
        SharedPrefsManager.newInstance(context)
            .putFloat(Preference.PREF_LNG, location.longitude.toFloat())
    }
}