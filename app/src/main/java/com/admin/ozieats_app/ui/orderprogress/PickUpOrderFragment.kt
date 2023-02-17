package com.admin.ozieats_app.ui.orderprogress

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.admin.ozieats_app.R
import com.admin.ozieats_app.data.OrderRepository
import com.admin.ozieats_app.utils.*
import com.admin.ozieats_app.utils.directionhelpers.FetchURL
import com.admin.ozieats_app.utils.directionhelpers.TaskLoadedCallback
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.fragment_pickup_order.view.*
import java.util.*


class PickUpOrderFragment : Fragment(), OnMapReadyCallback, TaskLoadedCallback {

    private lateinit var orderProgress: OrderProgressActivity
    private lateinit var mGoogleMap: GoogleMap
    lateinit var place1: MarkerOptions
    lateinit var place2: MarkerOptions
    lateinit var orderRepository: OrderRepository
    private var currentPolyline: Polyline? = null
    var request_id = 0
    lateinit var taskLoadedCallback: TaskLoadedCallback

    val LOCATION_UPDATE_MIN_DISTANCE = 10
    val LOCATION_UPDATE_MIN_TIME = 5000
    private var mLocationManager: LocationManager? = null
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val mHandler = Handler()
    private var mRunnable: Runnable? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pickup_order, container, false)

        orderProgress = activity as OrderProgressActivity
        taskLoadedCallback = this
        mLocationManager =
            orderProgress.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(orderProgress)

        val isFromWhere = SharedPrefsManager.newInstance(orderProgress)
            .getBoolean(Preference.ISFROMDIRECTORDER, false)

        if (isFromWhere) {
            view.mapConstraint.visibility = View.VISIBLE
            view.textConstraint.visibility = View.GONE
        } else {
            view.mapConstraint.visibility = View.GONE
            view.textConstraint.visibility = View.VISIBLE
        }

        //cl_main
        orderRepository = OrderRepository(requireActivity())
        request_id =
            SharedPrefsManager.newInstance(requireContext()).getInt(Preference.ORDER_REQUEST_ID, 0)

        initClickListener(view)
        getLocationPeriodically()

        val lat = SharedPrefsManager.newInstance(requireContext()).getFloat(Preference.PREF_LAT, 0f)
        val lng = SharedPrefsManager.newInstance(requireContext()).getFloat(Preference.PREF_LNG, 0f)

        place1 =
            MarkerOptions().position(LatLng(lat.toDouble(), lng.toDouble())).title("My Location")

        place2 = MarkerOptions().position(
            LatLng(
                getMyOrderPreference(requireContext()).lat,
                getMyOrderPreference(requireContext()).lng
            )
        ).title("Location 2")

        val myData = getMyOrderPreference(requireContext())
        view.textViewRestaurantName.text = "Go To " + myData.restaurant_name
        view.textViewRestaurantAddress.text = myData.restaurant_address
        view.tvOrderIdpick.text = "#" + myData.orderId

        MapsInitializer.initialize(requireContext())
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.myorderAddressMap) as SupportMapFragment
        mapFragment.getMapAsync(this)

        if (checkConnection())
        {
            FetchURL(requireContext() as Activity, taskLoadedCallback).execute(
                getUrl(place1.position, place2.position, "driving"),
                "driving"
            )
        }
        return view
    }

    fun checkConnection():Boolean
    {
        val cm = App.getInstance().applicationContext
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        val isConnected = (activeNetwork != null
                && activeNetwork.isConnectedOrConnecting)
        return isConnected
    }

    private fun getLocationPeriodically() {
        Log.e("onRequestResult", ": run out")
        mRunnable = object : Runnable {
            override fun run() {
                Log.e("onRequestResult", ": run in")
                getLatLng()
                mHandler.postDelayed(this, 5000)
            }
        }
        //Start
        mHandler.postDelayed(mRunnable!!, 5000)
    }

    private fun initClickListener(view: View) {

        view.tvHere.setOnClickListener {

            updateFirebase()
        }

        view.tv_imHere.setOnClickListener {
            updateFirebase()
        }

        view.tv_DeliverToCar.setOnClickListener {

            val exploreFragment = EnterCarNumberFragment.newInstance()
            openFragment(exploreFragment)
        }

        view.tvDeliverToCar.setOnClickListener {

            val exploreFragment = EnterCarNumberFragment.newInstance()
            openFragment(exploreFragment)
        }

        view.tv_navigateRestaurant.setOnClickListener {

            view.mapConstraint.visibility = View.VISIBLE
            view.textConstraint.visibility = View.GONE

        }

        view.tv_viewDetail.setOnClickListener {

            val intent = Intent(requireContext(), OrderSummaryActivity::class.java)
            startActivity(intent)

        }

    }

    private fun getUrl(
        origin: LatLng,
        dest: LatLng,
        directionMode: String
    ): String? {
        // Origin of route
        val str_origin = "origin=" + origin.latitude + "," + origin.longitude
        // Destination of route
        val str_dest = "destination=" + dest.latitude + "," + dest.longitude
        // Mode
        val mode = "mode=$directionMode"
        // Building the parameters to the web service
        val parameters = "$str_origin&$str_dest&$mode"
        // Output format
        val output = "json"
        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters&key=AIzaSyD48pWnEEpb3jWUgiaYgef3mItabsHIXKs"
    }

    private fun updateFirebase() {
        val loader = Loader(context, android.R.style.Theme_Translucent_NoTitleBar)
        loader.show()
        loader.setCancelable(false)
        loader.setCanceledOnTouchOutside(false)
        orderRepository.pushNotifications(
            getUserFromPreference(orderProgress).id,
            request_id,
            1,
            getUserFromPreference(orderProgress).username + " is here to pickup their order."
        ).observeForever {
            loader.cancel()
            if (it.status == Result.Status.SUCCESS) {

                Log.e("sgsjhsgjks", "updateFirebase: ")

                SharedPrefsManager.newInstance(orderProgress)
                    .putBoolean(Preference.ORDER_PROGRESS, true)
                val exploreFragment = PickupProgressFragment.newInstance()
                openFragment(exploreFragment)
            } else {
                showAlert(requireContext(), it.message.toString())
            }
        }
    }

    fun addLocationOnMap(place: MarkerOptions) {
        val location = LatLng(
            getMyOrderPreference(orderProgress).lat,
            getMyOrderPreference(orderProgress).lng
        )
        mGoogleMap.addMarker(
            MarkerOptions().position(location)
                .title(getMyOrderPreference(orderProgress).restaurant_name)
        )

        mGoogleMap.addMarker(place)
        val cameraPosition = CameraPosition.Builder()
            .target(location)
            .zoom(15f)
            .build()
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        if (googleMap != null) {
            mGoogleMap = googleMap

            addLocationOnMap(place1)
        }
    }

    override fun onResume() {
        super.onResume()

        val lat = SharedPrefsManager.newInstance(requireContext()).getFloat(Preference.PREF_LAT, 0f)
        val lng = SharedPrefsManager.newInstance(requireContext()).getFloat(Preference.PREF_LNG, 0f)

        Log.e("fdhfdfdfh", "onLocationChanged:===> " + lat + "-----" + lng)
        place1 =
            MarkerOptions().position(LatLng(lat.toDouble(), lng.toDouble())).title("My Location")

        //addLocationOnMap(place1)

        if (checkConnection())
        {
            FetchURL(requireContext() as Activity, taskLoadedCallback).execute(
                getUrl(place1.position, place2.position, "driving"),
                "driving"
            )
        }
    }

    fun openFragment(fragment: Fragment) {
        val transaction =
            orderProgress.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.progressContainer, fragment)
//        transaction.addToBackStack(null)
        transaction.commit()
    }

    companion object {
        fun newInstance(): PickUpOrderFragment = PickUpOrderFragment()
    }

    override fun onTaskDone(vararg values: Any?) {
        if (currentPolyline != null) {
            currentPolyline!!.remove()
        }
        currentPolyline = mGoogleMap.addPolyline(values[0] as PolylineOptions?)
    }

    @SuppressLint("MissingPermission")
    private fun getLatLng() {

        mFusedLocationClient.lastLocation.addOnSuccessListener(orderProgress) { location: Location? ->
            if (location != null) {
                Log.e(
                    "onRequestResult",
                    "onRequestPermissionsResult: " + String.format(
                        Locale.US,
                        "%s -- %s",
                        location.latitude,
                        location.longitude
                    )
                )
                val newUserLocation =
                    MarkerOptions().position(LatLng(location.latitude, location.longitude))
                        .title("My Location")
                if (checkConnection())
                {
                    FetchURL(requireContext() as Activity, taskLoadedCallback).execute(
                        getUrl(newUserLocation.position, place2.position, "driving"),
                        "driving"
                    )
                }
                //addLocationOnMap(newUserLocation)
            } else {
                Log.e("onRequestResult", "getLatLng: Null ")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("CallBack", "onDestroy ")

        if (mRunnable != null) {
            mHandler.removeCallbacks(mRunnable!!)
        }
    }

    /* override fun onLocationChanged(location: Location?) {

         if (location != null) {
             Log.e("fdhfdfdfh", "onLocationChanged: " + location.latitude)
             val newUserLocation =
                 MarkerOptions().position(LatLng(location.latitude, location.longitude))
                     .title("My Location")
             addLocationOnMap(newUserLocation)

             FetchURL(requireContext() as Activity, taskLoadedCallback).execute(
                 getUrl(newUserLocation.position, place2.position, "driving"),
                 "driving"
             )
         }
     }

     override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
         Toast.makeText(requireContext(), provider + "------" + status, Toast.LENGTH_LONG).show()
     }

     override fun onProviderEnabled(provider: String?) {
         Toast.makeText(requireContext(), provider, Toast.LENGTH_LONG).show()
     }

     override fun onProviderDisabled(provider: String?) {
         Toast.makeText(requireContext(), provider, Toast.LENGTH_LONG).show()
     }*/
}