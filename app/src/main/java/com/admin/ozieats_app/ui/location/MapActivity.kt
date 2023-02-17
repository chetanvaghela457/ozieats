package com.admin.ozieats_app.ui.location

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.admin.ozieats_app.R
import com.admin.ozieats_app.databinding.ActivityMapBinding
import com.admin.ozieats_app.ui.home.item.NetworkConnectionListener
import com.admin.ozieats_app.utils.*
import com.google.android.gms.location.LocationListener
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.activity_map.constraintLayoutMain
import kotlinx.android.synthetic.main.activity_map.noInternetLayout
import kotlinx.android.synthetic.main.activity_restaurant_details.*
import kotlinx.android.synthetic.main.no_internet_layout.view.*


class MapActivity : BaseActivity(), NetworkConnectionListener {

    lateinit var binding: ActivityMapBinding
    lateinit var locationViewModel: LocationViewModel
    lateinit var adapter: RangeAdapter
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    var isInternetConnectd = true
    lateinit var location: Location
    private val EARTH_RADIUS = 6378100.0
    private var offset = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_map)

        locationViewModel =
            ViewModelProvider(this, LocationViewModel.LocationModelFactory(this, this)).get(
                LocationViewModel::class.java
            )

        binding.locationListener = locationViewModel

        MapsInitializer.initialize(this)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(object : OnMapReadyCallback {
            override fun onMapReady(googleMap: GoogleMap?) {
                if (googleMap != null) {
                    mGoogleMap = googleMap

                    val intent = intent
                    if (intent.getIntExtra("Place Number", 0) == 0) {

                        // Zoom into users location
                        locationManager =
                            getSystemService(Context.LOCATION_SERVICE) as LocationManager
                        locationListener = object : LocationListener {
                            override fun onLocationChanged(location: Location) {
                                centreMapOnLocation(location, "Your Location")
                            }

                            fun onStatusChanged(
                                s: String?,
                                i: Int,
                                bundle: Bundle?
                            ) {
                            }

                            fun onProviderEnabled(s: String?) {}
                            fun onProviderDisabled(s: String?) {}
                        }
                    }
                    if ((ContextCompat.checkSelfPermission(
                            this@MapActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                                ) && (ContextCompat.checkSelfPermission(
                            this@MapActivity,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED)
                    ) {

                        val locationManager =
                            getSystemService(Context.LOCATION_SERVICE) as LocationManager

                        val locationProvider = LocationManager.NETWORK_PROVIDER
                        val lastlocation = locationManager.getLastKnownLocation(locationProvider)

                        if (lastlocation != null) {
                            centreMapOnLocation(lastlocation, "Your Location")
                        }
                    } else {
                        ActivityCompat.requestPermissions(
                            this@MapActivity,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            1
                        )
                    }
                }
            }

        })

        fetchData()

        SharedPrefsManager.newInstance(this)
            .putString(Preference.RESTAURANT_DATA, "")

    }

    private fun fetchData() {
        locationViewModel.getAllRangeList().observe(this, Observer {

            adapter = RangeAdapter(this, it, locationViewModel, this)
            recyclerViewRanges.setHasFixedSize(true)
            recyclerViewRanges.isNestedScrollingEnabled = false
            recyclerViewRanges.adapter = adapter
        })
    }

    /*override fun onMapReady(googleMap: GoogleMap?) {
        if (googleMap != null) {
            mGoogleMap = googleMap

            val intent = intent
            if (intent.getIntExtra("Place Number", 0) == 0) {

                // Zoom into users location
                locationManager =
                    this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                locationListener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        centreMapOnLocation(location, "Your Location")
                    }

                    fun onStatusChanged(
                        s: String?,
                        i: Int,
                        bundle: Bundle?
                    ) {
                    }

                    fun onProviderEnabled(s: String?) {}
                    fun onProviderDisabled(s: String?) {}
                }
            }
            if ((ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                        ) && (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED))
            {

                val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

                val locationProvider = LocationManager.NETWORK_PROVIDER
                val lastlocation = locationManager.getLastKnownLocation(locationProvider)

                if (lastlocation != null)
                {
                    centreMapOnLocation(lastlocation, "Your Location")
                }
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )
            }
        }
    }*/

    override fun onResume() {
        super.onResume()

        if (isInternetConnectd) {
            if (!constraintLayoutMain.isVisible()) {
                noInternetLayout.gone()
                constraintLayoutMain.visible()
            }
        }

        MapsInitializer.initialize(this)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(object : OnMapReadyCallback {
            override fun onMapReady(googleMap: GoogleMap?) {
                if (googleMap != null) {
                    mGoogleMap = googleMap

                    val intent = intent
                    if (intent.getIntExtra("Place Number", 0) == 0) {

                        // Zoom into users location
                        locationManager =
                            getSystemService(Context.LOCATION_SERVICE) as LocationManager
                        locationListener = object : LocationListener {
                            override fun onLocationChanged(location: Location) {
                                centreMapOnLocation(location, "Your Location")
                            }

                            fun onStatusChanged(
                                s: String?,
                                i: Int,
                                bundle: Bundle?
                            ) {
                            }

                            fun onProviderEnabled(s: String?) {}
                            fun onProviderDisabled(s: String?) {}
                        }
                    }
                    if ((ContextCompat.checkSelfPermission(
                            this@MapActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                                ) && (ContextCompat.checkSelfPermission(
                            this@MapActivity,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED)
                    ) {

                        val locationManager =
                            getSystemService(Context.LOCATION_SERVICE) as LocationManager

                        val locationProvider = LocationManager.NETWORK_PROVIDER
                        val lastlocation = locationManager.getLastKnownLocation(locationProvider)

                        if (lastlocation != null) {
                            centreMapOnLocation(lastlocation, "Your Location")
                        }
                    } else {
                        ActivityCompat.requestPermissions(
                            this@MapActivity,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            1
                        )
                    }
                }
            }

        })
    }

    fun centreMapOnLocation(
        location: Location,
        title: String?
    ) {
        val userLocation = LatLng(location.latitude, location.longitude)

        SharedPrefsManager.newInstance(this)
            .putFloat(Preference.PREF_LAT, location.latitude.toFloat())
        SharedPrefsManager.newInstance(this)
            .putFloat(Preference.PREF_LNG, location.longitude.toFloat())

        println("vgdfg" + userLocation.latitude)

        val circleOptions = CircleOptions()

        circleOptions.center(userLocation)
        circleOptions.radius(100.0) // radius of circle in meters

        circleOptions.strokeColor(Color.BLUE) //apply stroke with blue

        circleOptions.fillColor(Color.RED) // fill circle with red

        mGoogleMap.addCircle(circleOptions)

        val markerOptions = MarkerOptions().position(userLocation).title(title)
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker))
        mGoogleMap.clear()
        mGoogleMap.addMarker(markerOptions)
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
    }

    override fun changeNetwork(connected: Boolean) {

        isInternetConnectd = connected
        if (connected) {
            if (!constraintLayoutMain.isVisible()) {
                noInternetLayout.retryButton.setOnClickListener {

                    noInternetLayout.gone()
                    constraintLayoutMain.visible()
                    val intent = Intent(this, MapActivity::class.java)
                    startActivity(intent)
                }
            }
        } else {
            noInternetLayout.visible()
            constraintLayoutMain.gone()
        }
    }

}
