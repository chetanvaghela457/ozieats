package com.admin.ozieats_app.ui.home.fragments.home

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.speech.RecognizerIntent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.admin.ozieats_app.R
import com.admin.ozieats_app.data.LocationRepository
import com.admin.ozieats_app.databinding.FragmentHomeBinding
import com.admin.ozieats_app.model.AddressModel
import com.admin.ozieats_app.model.RestaurantModel
import com.admin.ozieats_app.ui.home.HomeActivity
import com.admin.ozieats_app.ui.home.cart.CartActivity
import com.admin.ozieats_app.ui.home.fragments.favourite.RestaurantAdapter
import com.admin.ozieats_app.ui.home.fragments.searchproducts.SearchFragment
import com.admin.ozieats_app.ui.home.restaurantDetails.RestaurantDetailsActivity
import com.admin.ozieats_app.ui.location.LocationModel
import com.admin.ozieats_app.ui.location.LocationViewModel
import com.admin.ozieats_app.utils.*
import com.admin.ozieats_app.utils.Permissions.Companion.MICORPHONE
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_home.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.util.*


class HomeFragment : Fragment(), LocationRepository.CartItemTotalGet {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private var adapter: RestaurantAdapter? = null
    private lateinit var mActivity: AppCompatActivity
    private lateinit var locationViewModel: LocationViewModel
    private lateinit var mHomeRangeAdapter: HomeRangeAdapter
    private lateinit var bottomSheet: BottomSheetBehavior<NestedScrollView>
    private lateinit var bottomSheetLocation: BottomSheetBehavior<NestedScrollView>
    lateinit var onReviewGet: LocationRepository.CartItemTotalGet
    var locationModel: LocationModel? = null
    var placesClient: PlacesClient? = null
    var autoCompleteTextView: AutoCompleteTextView? = null
    var autoAdapter: AutoCompleteAdapter? = null
    var new_range = 0
    private var recentLocationList: ArrayList<AddressModel> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_home,
                container,
                false
            )

        onReviewGet = this

        mActivity = activity as HomeActivity


        homeViewModel = ViewModelProvider(
            this,
            HomeViewModel.HomeViewModelFactory(
                requireContext(), this
            )
        ).get(HomeViewModel::class.java)

        binding.homeViewModel = homeViewModel

        //checkConnection()

        locationViewModel = ViewModelProvider(
            this,
            LocationViewModel.LocationModelFactory(requireContext(), viewLifecycleOwner)
        ).get(LocationViewModel::class.java)



        bottomSheet = BottomSheetBehavior.from(binding.scrollViewSearchRangeHome)
        bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN

        bottomSheetLocation = BottomSheetBehavior.from(binding.scrollViewLocationAddress)
        bottomSheetLocation.state = BottomSheetBehavior.STATE_HIDDEN

        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), getString(R.string.google_direction_api_key))
        }

        placesClient = Places.createClient(requireActivity())

        initAutoCompleteTextView()

        locationModel = getLocationFromPreference(requireContext())

        val selectedLocation = getSelectedLocation(requireContext())

        recentLocationList.clear()
        recentLocationList.addAll(getRecentLocationList(requireContext()))

        if (locationModel != null) {
            binding.homeToolbar.selectedLocation.text =
                getCompleteAddressString(
                    requireContext(),
                    locationModel!!.lat,
                    locationModel!!.lng
                )

            Log.e("LOCATION", "onCreateView: "+getCompleteAddressString(
                requireContext(),
                locationModel!!.lat,
                locationModel!!.lng
            ) )
        }

        binding.useCurrentLocationTextView.setOnClickListener {

            if (bottomSheet.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
            }
            bottomSheetLocation.state = BottomSheetBehavior.STATE_HIDDEN
            if ((ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                        ) && (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED)
            ) {

                val locationManager =
                    requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

                val locationProvider = LocationManager.NETWORK_PROVIDER
                val lastlocation = locationManager.getLastKnownLocation(locationProvider)

                if (lastlocation != null) {
                    val userLocation = LatLng(lastlocation.latitude, lastlocation.longitude)

                    SharedPrefsManager.newInstance(requireContext())
                        .putFloat(Preference.PREF_LAT, lastlocation.latitude.toFloat())
                    SharedPrefsManager.newInstance(requireContext())
                        .putFloat(Preference.PREF_LNG, lastlocation.longitude.toFloat())

                    addLocationData(lastlocation.latitude, lastlocation.longitude)

//                    SharedPrefsManager.newInstance(requireContext())
//                        .putString(Preference.SELECTED_LOCATION, "")

                    binding.homeToolbar.selectedLocation.text =
                        getCompleteAddressString(
                            requireContext(),
                            userLocation.latitude,
                            userLocation.longitude
                        )
                }
            }
        }

        fetchData(getLocationFromPreference(mActivity))
        fetchLocations()

        binding.mSwipeRefreshLayout.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                changeLocationData(getLocationFromPreference(mActivity))
                if(binding.mSwipeRefreshLayout.isRefreshing) {
                    binding.mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        })

        binding.homeToolbar.selectedLocation.setOnClickListener {

            if (bottomSheet.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
            }
            if (bottomSheetLocation.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetLocation.state = BottomSheetBehavior.STATE_HIDDEN
            } else {
                bottomSheetLocation.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }


        if (recentLocationList.size > 0) {
            addAdapter(recentLocationList)
        } else {
            //binding.recentAddressRecycler.gone()
            Log.e("NOITEMFOUND", "onCreateView: ")
        }

        binding.homeToolbar.imgShoppingCartItemTotal.text =
            SharedPrefsManager.newInstance(requireContext()).getInt(Preference.CART_COUNT, 0)
                .toString()

        binding.homeToolbar.imageViewCartButton.setOnClickListener {

            val intent = Intent(requireContext(), CartActivity::class.java)
            startActivity(intent)
        }

        binding.homeToolbar.imageViewMicrophone.setOnClickListener {
            binding.homeToolbar.editTextSearch.setText("")
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            intent.putExtra(
                RecognizerIntent.EXTRA_CALLING_PACKAGE,
                requireActivity().packageName
            )
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speak))
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH)
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            startActivityForResult(intent, MICORPHONE)
        }

        binding.closeFilter.setOnClickListener {
            if (bottomSheetLocation.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetLocation.state = BottomSheetBehavior.STATE_HIDDEN
            }
            if (bottomSheet.state == BottomSheetBehavior.STATE_EXPANDED) {
                hideSoftKeyboard(mActivity)
                Handler().postDelayed(Runnable {
                bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
            },200)
            }
        }

        binding.closeLocationDialog.setOnClickListener {

            if (bottomSheet.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
            }
            if (bottomSheetLocation.state == BottomSheetBehavior.STATE_EXPANDED) {
                hideSoftKeyboard(mActivity)
                Handler().postDelayed(Runnable {
                    bottomSheetLocation.state = BottomSheetBehavior.STATE_HIDDEN
                },200)
            }
        }

        binding.homeToolbar.filterButton.setOnClickListener {

            onFilterButtonClick()
        }

        binding.homeToolbar.editTextSearch.gone()
        binding.homeToolbar.tvSearchRestaurant.visible()

        binding.homeToolbar.tvSearchRestaurant.setOnClickListener {
            replaceFragment(mActivity, SearchFragment())
        }

        bottomSheet.isHideable = true
        bottomSheetLocation.isHideable = true
        return binding.root
    }

    private fun initAutoCompleteTextView() {
        binding.addLocationEditText.threshold = 1

        binding.addLocationEditText.onItemClickListener = autocompleteClickListener
        autoAdapter = AutoCompleteAdapter(requireContext(), placesClient!!)
        binding.addLocationEditText.setAdapter(autoAdapter)


        binding.addLocationEditText.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->

            if (actionId == EditorInfo.IME_ACTION_DONE) {
                autocompleteClickListener
                hideSoftKeyboard(mActivity)

                binding.addLocationEditText.setText("")
                Log.e("CHECK_SRERACH_TEXT", "onCreateView: " + v.text)

                Handler().postDelayed(Runnable {
                    bottomSheetLocation.state = BottomSheetBehavior.STATE_HIDDEN
                },200)

                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun onAddressClick(addressModel: AddressModel) {

        bottomSheetLocation.state = BottomSheetBehavior.STATE_HIDDEN
        if (bottomSheet.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.homeToolbar.selectedLocation.text = addressModel.addressFull

        /*SharedPrefsManager.newInstance(mActivity)
            .putString(Preference.SELECTED_LOCATION, Gson().toJson(addressModel))*/

        addLocationData(addressModel.lat, addressModel.lng)
    }

    fun addLocationData(lat: Double, lng: Double) {
        val locations = LocationModel()
        locations.radius = locationModel!!.radius
        locations.user_id = getUserFromPreference(requireContext()).id
        locations.lat = lat
        locations.lng = lng

        bottomSheetLocation.state = BottomSheetBehavior.STATE_HIDDEN

        changeLocationData(locations)

    }

    fun performRecentLocation(addressModel: AddressModel) {

        if (recentLocationList.size == 5) {
//            for (recentList in recentLocationList) {
            recentLocationList.removeAt(0)
//                if (!recentList.addressSingleLine.contains(addressModel.addressSingleLine)) {

            binding.homeToolbar.selectedLocation.text = addressModel.addressSingleLine

            recentLocationList.add(addressModel)

            if (recentLocationList.size > 0) {
                addAdapter(recentLocationList)

                SharedPrefsManager.newInstance(requireContext())
                    .putString(
                        Preference.SEARCH_LOCATION_LIST,
                        Gson().toJson(recentLocationList)
                    )
                addLocationData(addressModel.lat, addressModel.lng)
            }

//                }
//            }
        } else {

//            for (recentList in recentLocationList) {
//                if (!recentList.addressSingleLine.contains(addressModel.addressSingleLine)) {
            binding.homeToolbar.selectedLocation.text = addressModel.addressSingleLine
            Log.e(
                "ghhghg",
                "performRecentLocation: " + addressModel.addressFull + "---" + addressModel.addressSingleLine
            )
            recentLocationList.add(addressModel)

            if (recentLocationList.size > 0) {
                addAdapter(recentLocationList)
                SharedPrefsManager.newInstance(requireContext())
                    .putString(
                        Preference.SEARCH_LOCATION_LIST,
                        Gson().toJson(recentLocationList)
                    )
                addLocationData(addressModel.lat, addressModel.lng)
            }
            /*SharedPrefsManager.newInstance(mActivity)
                .putString(Preference.SELECTED_LOCATION, Gson().toJson(addressModel))*/

        }
    }


    private fun addAdapter(recentLocations: ArrayList<AddressModel>) {
        val searchAdapter =
            RecentLocationsAdapter(requireContext(), recentLocations, onAddressClick = {
                onAddressClick(it)
            })
        binding.recentAddressRecycler.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, true)
        binding.recentAddressRecycler.adapter = searchAdapter
    }

    private val autocompleteClickListener: AdapterView.OnItemClickListener =
        AdapterView.OnItemClickListener { adapterView, view, i, l ->
            try {
                val item: AutocompletePrediction = autoAdapter?.getItem(i)!!
                var placeID: String? = null
                if (item != null) {
                    placeID = item.placeId
                }

                val placeFields: List<Place.Field> = Arrays.asList(
                    Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS
                    , Place.Field.LAT_LNG
                )
                var request: FetchPlaceRequest? = null
                if (placeID != null) {
                    request = FetchPlaceRequest.builder(placeID, placeFields)
                        .build()
                }
                if (request != null) {
                    placesClient!!.fetchPlace(request).addOnSuccessListener { task ->
                        if (task != null) {

                            val addressModel = AddressModel()
                            addressModel.addressFull = task.place.address.toString()
                            addressModel.addressSingleLine = task.place.name.toString()
                            addressModel.lat = task.place.latLng!!.latitude
                            addressModel.lng = task.place.latLng!!.longitude
                            performRecentLocation(addressModel)
                        }
                    }.addOnFailureListener { e ->
                        e.printStackTrace()
                        //binding.homeToolbar.selectedLocation.text = e.message
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    @AfterPermissionGranted(Permissions.LOCATION_PERMISSION)
    fun onFilterButtonClick() {
        if (EasyPermissions.hasPermissions(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) && EasyPermissions.hasPermissions(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            if (bottomSheet.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
            } else {
                bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
            }

            if (bottomSheetLocation.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetLocation.state = BottomSheetBehavior.STATE_HIDDEN
            }

        } else {
            EasyPermissions.requestPermissions(
                context as Activity,
                "LocationPermission",
                Permissions.LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            EasyPermissions.requestPermissions(
                context as Activity,
                "LocationPermission",
                Permissions.LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }

    }

    companion object {
        fun newInstance(): HomeFragment = HomeFragment()
    }

    override fun onResume() {
        super.onResume()

        if (adapter == null) {
            val restaurantData = getTotalRestaurantPreference(requireContext())
            if (restaurantData != null && restaurantData.size > 0) {
                binding.mainHomeItems.visible()
                binding.emptyLayout.gone()
                binding.placesNearYou.visible()
                adapter = RestaurantAdapter(
                    requireContext(),
                    restaurantData,
                    homeViewModel,
                    onRestaurantClick = {
                        openActivity(it)
                    }, onFavouriteButtonClick = { it, isFavourite ->
                        onFavouriteButtonClick(it, isFavourite)
                    })
                restaurantRecycler.adapter = adapter
                restaurantRecycler.setHasFixedSize(true)
                restaurantRecycler.isNestedScrollingEnabled = false
            } else {
                binding.placesNearYou.gone()
                binding.mainHomeItems.gone()
                binding.emptyLayout.visible()
            }

        } else {
            val restaurantData = getTotalRestaurantPreference(requireContext())
            if (restaurantData != null && restaurantData.size > 0) {
                binding.mainHomeItems.visible()
                binding.emptyLayout.gone()
                binding.placesNearYou.visible()
                adapter!!.setData(getTotalRestaurantPreference(requireContext()))
            } else {
                binding.placesNearYou.gone()
                binding.mainHomeItems.gone()
                binding.emptyLayout.visible()
            }
        }

        binding.homeToolbar.imgShoppingCartItemTotal.text =
            SharedPrefsManager.newInstance(requireContext()).getInt(Preference.CART_COUNT, 0)
                .toString()

    }

    fun changeLocationData(locations: LocationModel)
    {
        homeViewModel.geLocationChange(locations).observe(viewLifecycleOwner,
            Observer {

                if (it != null) {
                    if (it.size > 0) {

                        SharedPrefsManager.newInstance(requireContext()).putString(
                            Preference.RESTAURANT_DATA,
                            Gson().toJson(it)
                        )
                        binding.mainHomeItems.visible()
                        binding.emptyLayout.gone()
                        binding.placesNearYou.visible()
                        binding.placesNearString.text = "(" + locationModel?.radius + "km)"
                        adapter = RestaurantAdapter(
                            requireContext(),
                            it,
                            homeViewModel,
                            onRestaurantClick = {
                                openActivity(it)
                            }, onFavouriteButtonClick = { it, isFavourite ->
                                onFavouriteButtonClick(it, isFavourite)
                            })
                        restaurantRecycler.adapter = adapter
                        restaurantRecycler.setHasFixedSize(true)
                        restaurantRecycler.isNestedScrollingEnabled = false
                    }
                } else {
                    SharedPrefsManager.newInstance(requireContext()).putString(
                        Preference.RESTAURANT_DATA,
                        ""
                    )
                    binding.placesNearYou.gone()
                    binding.mainHomeItems.gone()
                    binding.emptyLayout.visible()
                }

            })

        if (getAdvertismentPreference(requireContext()).size > 0) {
            homeViewModel.dataNotFound.set(true)
            val adapter =
                AdvertisementAdapter(requireContext(), getAdvertismentPreference(requireContext()))
            binding.advertisementRecycler.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            binding.advertisementRecycler.adapter = adapter
        } else {
            homeViewModel.dataNotFound.set(false)
        }

        SharedPrefsManager.newInstance(requireContext())
            .putString(Preference.PREF_LOCATION, Gson().toJson(locations))
    }

    private fun fetchData(locationModel: LocationModel) {
        homeViewModel.getAllRestaurantList(locationModel).observe(viewLifecycleOwner,
            Observer {

                if (it != null) {
                    if (it.size > 0) {

                        SharedPrefsManager.newInstance(requireContext()).putString(
                            Preference.RESTAURANT_DATA,
                            Gson().toJson(it)
                        )
                        SharedPrefsManager.newInstance(requireContext())
                            .putBoolean(Preference.HOME_DATA, false)
                        binding.mainHomeItems.visible()
                        binding.emptyLayout.gone()
                        binding.placesNearYou.visible()
                        binding.placesNearString.text = "(" + locationModel?.radius + "km)"
                        adapter = RestaurantAdapter(
                            requireContext(),
                            it,
                            homeViewModel,
                            onRestaurantClick = {
                                openActivity(it)
                            }, onFavouriteButtonClick = { it, isFavourite ->
                                onFavouriteButtonClick(it, isFavourite)
                            })
                        restaurantRecycler.adapter = adapter
                        restaurantRecycler.setHasFixedSize(true)
                        restaurantRecycler.isNestedScrollingEnabled = false
                    }
                } else {
                    SharedPrefsManager.newInstance(requireContext()).putString(
                        Preference.RESTAURANT_DATA,
                        ""
                    )
                    binding.placesNearYou.gone()
                    binding.mainHomeItems.gone()
                    binding.emptyLayout.visible()
                }

            })

        if (getAdvertismentPreference(requireContext()).size > 0) {
            homeViewModel.dataNotFound.set(true)
            val adapter =
                AdvertisementAdapter(requireContext(), getAdvertismentPreference(requireContext()))
            binding.advertisementRecycler.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            binding.advertisementRecycler.adapter = adapter
        } else {
            homeViewModel.dataNotFound.set(false)
        }
    }

    private fun onFavouriteButtonClick(
        restaurants: RestaurantModel,
        isFavourite: Int
    ) {

        val loader = Loader(context, android.R.style.Theme_Translucent_NoTitleBar)
        loader.show()
        loader.setCancelable(false)
        loader.setCanceledOnTouchOutside(false)
        homeViewModel.addOrRemoveToFavourite(restaurants.restaurant_id)
            .observeForever {
                loader.dismiss()

                val restaurantList = ArrayList<RestaurantModel>()

                for (i in getTotalRestaurantPreference(mActivity)) {
                    if (i.restaurant_id == restaurants.restaurant_id) {
                        i.favourites = isFavourite
                    }
                    restaurantList.add(i)
                }
                SharedPrefsManager.newInstance(mActivity).putString(
                    Preference.RESTAURANT_DATA,
                    Gson().toJson(restaurantList)
                )
            }
    }

    private fun openActivity(restaurentModel: RestaurantModel) {

        if (bottomSheet.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        }
        if (bottomSheetLocation.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetLocation.state = BottomSheetBehavior.STATE_HIDDEN
        }

        val intent = Intent(context, RestaurantDetailsActivity::class.java)
        intent.putExtra(SendIntents.RESTAURANT_NAME, restaurentModel.restaurant_name)
        intent.putExtra(SendIntents.RESTAURANT_ID, restaurentModel.restaurant_id)
        intent.putExtra(SendIntents.RESTAURANT_ADDRESS, restaurentModel.delivery_address)
        intent.putExtra(SendIntents.RESTAURANT_IMAGE, restaurentModel.restaurant_image)
        intent.putExtra(SendIntents.RESTAURANT_DISCOUNT, restaurentModel.restaurant_discount)
        intent.putExtra(SendIntents.RESTAURANT_DELIVERY_CHARGE, restaurentModel.delivery_charges)
        intent.putExtra(SendIntents.FAVOURITE, restaurentModel.favourites)
        intent.putExtra(SendIntents.TOTAL_RATING, restaurentModel.totalRating)
        intent.putExtra(SendIntents.RATING, restaurentModel.totalUserRating)
        intent.putExtra(SendIntents.BACKGROUND_IMAGE,restaurentModel.background_image)
        startActivityForResult(intent, 5001)

    }

    private fun fetchLocations() {

        locationViewModel.getAllRangeList().observe(viewLifecycleOwner, Observer {

            mHomeRangeAdapter = HomeRangeAdapter(requireContext(),
                it,
                locationViewModel,
                viewLifecycleOwner,
                homeViewModel,
                locationModel!!,
                onRangeClick = {
                    onRangeClick(it)
                })

            recyclerViewRanges.setHasFixedSize(true)
            recyclerViewRanges.itemAnimator = null
            recyclerViewRanges.isNestedScrollingEnabled = false
            recyclerViewRanges.adapter = mHomeRangeAdapter
        })
    }

    private fun onRangeClick(range: Int) {

        bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN

        val locations = getLocationFromPreference(requireContext())

        new_range = range

        val locationModels = LocationModel()
        locationModels.lat = locations.lat
        locationModels.lng = locations.lng
        locationModels.radius = range
        locationModels.user_id = locations.user_id

        val loader = Loader(context, android.R.style.Theme_Translucent_NoTitleBar)
        loader.show()
        loader.setCancelable(false)
        loader.setCanceledOnTouchOutside(false)
        locationViewModel.locationSelect(locationModels).observe(viewLifecycleOwner, Observer {

            loader.cancel()
            SharedPrefsManager.newInstance(requireContext())
                .putString(Preference.PREF_LOCATION, Gson().toJson(locationModels))

            if (it.data != null) {
                if (it.data.size > 0) {
                    binding.mainHomeItems.visible()
                    binding.emptyLayout.gone()
                    binding.placesNearYou.visible()

                    SharedPrefsManager.newInstance(requireContext())
                        .putString(Preference.RESTAURANT_DATA, Gson().toJson(it.data))

                    if (adapter == null) {
                        adapter = RestaurantAdapter(
                            requireContext(),
                            it.data,
                            homeViewModel,
                            onRestaurantClick = {
                                openActivity(it)
                            }, onFavouriteButtonClick = { it, isFavourite ->
                                onFavouriteButtonClick(it, isFavourite)
                            })
                        restaurantRecycler.adapter = adapter
                        restaurantRecycler.setHasFixedSize(true)
                        restaurantRecycler.isNestedScrollingEnabled = false
                    } else {
                        adapter!!.setData(it.data)
                    }
                }
            } else {

                SharedPrefsManager.newInstance(requireContext())
                    .putString(Preference.RESTAURANT_DATA, "")
                binding.mainHomeItems.gone()
                binding.emptyLayout.visible()
                binding.placesNearYou.gone()
            }
            locationModel = getLocationFromPreference(requireContext())

            fetchLocations()

            binding.placesNearString.text = "(" + range + "km)"

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == MICORPHONE && resultCode == Activity.RESULT_OK) {
            /* matches will contain possible words for voice */
            val matches =
                data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val topResult: String
            /* Choose the first word from the result and append to the edit TextBox */
            topResult = matches[0]
            binding.homeToolbar.editTextSearch.append(topResult)
            GlobalVariables.search_data = topResult
            replaceFragment(mActivity, SearchFragment())

        }

    }

    override fun getItemTotal(itemTotal: Int) {
        binding.homeToolbar.imgShoppingCartItemTotal.text = itemTotal.toString()
    }

    /*override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (!isConnected) {
            binding.noInternetLayout.visible()
            binding.mainCoordinatorLayout.gone()
        } else {

            binding.noInternetLayout.gone()
            binding.mainCoordinatorLayout.visible()
        }
    }*/

}