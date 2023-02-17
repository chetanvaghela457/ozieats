package com.admin.ozieats_app.ui.home.fragments.favourite

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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.admin.ozieats_app.R
import com.admin.ozieats_app.data.LocationRepository
import com.admin.ozieats_app.databinding.FragmentFavouriteBinding
import com.admin.ozieats_app.model.AddressModel
import com.admin.ozieats_app.ui.home.HomeActivity
import com.admin.ozieats_app.ui.home.cart.CartActivity
import com.admin.ozieats_app.ui.home.fragments.home.AutoCompleteAdapter
import com.admin.ozieats_app.ui.home.fragments.home.HomeFragment
import com.admin.ozieats_app.ui.home.fragments.home.HomeViewModel
import com.admin.ozieats_app.ui.home.fragments.home.RecentLocationsAdapter
import com.admin.ozieats_app.ui.home.fragments.searchproducts.SearchFragment
import com.admin.ozieats_app.ui.location.LocationModel
import com.admin.ozieats_app.ui.location.LocationViewModel
import com.admin.ozieats_app.utils.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.google.gson.Gson
import java.util.*


class FavouriteFragment : Fragment(), LocationRepository.CartItemTotalGet {

    lateinit var binding: FragmentFavouriteBinding
    lateinit var favouriteViewModel: FavouriteViewModel
    private lateinit var mActivity: AppCompatActivity
    private lateinit var bottomSheetLocation: BottomSheetBehavior<NestedScrollView>
    var locationModel: LocationModel? = null
    private lateinit var locationViewModel: LocationViewModel
    private lateinit var homeViewModel: HomeViewModel
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
                R.layout.fragment_favourite,
                container,
                false
            )

        mActivity = activity as HomeActivity

        favouriteViewModel = ViewModelProvider(
            this,
            FavouriteViewModel.FavouriteModelFactory(
                requireContext()
            )
        ).get(FavouriteViewModel::class.java)

        binding.favoriteListener = favouriteViewModel

        homeViewModel = ViewModelProvider(
            this,
            HomeViewModel.HomeViewModelFactory(
                requireContext(), this
            )
        ).get(HomeViewModel::class.java)

        locationViewModel = ViewModelProvider(
            this,
            LocationViewModel.LocationModelFactory(requireContext(), viewLifecycleOwner)
        ).get(LocationViewModel::class.java)

        /*locationModel = getLocationFromPreference(requireContext())
        if (locationModel != null) {
            binding.homeToolbar.selectedLocation.text =
                getCompleteAddressString(requireContext(), locationModel!!.lat, locationModel!!.lng)
        }*/

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

        /*if (!selectedLocation.toString().equals("")) {
            binding.homeToolbar.selectedLocation.text = selectedLocation.addressFull
        } else {*/
        if (locationModel != null) {
            binding.homeToolbar.selectedLocation.text =
                getCompleteAddressString(
                    requireContext(),
                    locationModel!!.lat,
                    locationModel!!.lng
                )
        }
//        }

        binding.useCurrentLocationTextView.setOnClickListener {

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

        binding.homeToolbar.selectedLocation.setOnClickListener {

            hideSoftKeyboard(mActivity)
            if (bottomSheetLocation.state == BottomSheetBehavior.STATE_EXPANDED) {
                Handler().postDelayed(Runnable {
                    bottomSheetLocation.state = BottomSheetBehavior.STATE_HIDDEN
                },200)
            } else {
                bottomSheetLocation.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }


        if (recentLocationList.size > 0) {
            addAdapter(recentLocationList)
        } else {
            binding.recentAddressRecycler.gone()
        }

        binding.closeLocationDialog.setOnClickListener {

            if (bottomSheetLocation.state == BottomSheetBehavior.STATE_EXPANDED) {
                hideSoftKeyboard(mActivity)
                Handler().postDelayed(Runnable {
                    bottomSheetLocation.state = BottomSheetBehavior.STATE_HIDDEN
                },200)
            }
        }

        bottomSheetLocation.isHideable = true

        binding.homeToolbar.editTextSearch.gone()
        binding.homeToolbar.tvSearchRestaurant.visible()
        binding.homeToolbar.filterButton.gone()

        binding.homeToolbar.tvSearchRestaurant.setOnClickListener {
            replaceFragment(mActivity, SearchFragment())
        }

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
            startActivityForResult(intent, Permissions.MICORPHONE)
        }

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Places"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Orders"))

        binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        val adapter =
            PagerAdapter(
                requireActivity().supportFragmentManager,
                binding.tabLayout.tabCount
            )
        binding.viewPager.adapter = adapter
        binding.viewPager.addOnPageChangeListener(TabLayoutOnPageChangeListener(binding.tabLayout))
        binding.tabLayout.setOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        return binding.root
    }

    companion object {
        fun newInstance(): FavouriteFragment = FavouriteFragment()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Permissions.MICORPHONE && resultCode == Activity.RESULT_OK) {
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

        binding.homeToolbar.selectedLocation.text = addressModel.addressFull

        SharedPrefsManager.newInstance(mActivity)
            .putString(Preference.SELECTED_LOCATION, Gson().toJson(addressModel))

        addLocationData(addressModel.lat, addressModel.lng)
    }

    fun addLocationData(lat: Double, lng: Double) {
        val locations = LocationModel()
        locations.radius = locationModel!!.radius
        locations.user_id = getUserFromPreference(requireContext()).id
        locations.lat = lat
        locations.lng = lng

        bottomSheetLocation.state = BottomSheetBehavior.STATE_HIDDEN

        val loader = Loader(context, android.R.style.Theme_Translucent_NoTitleBar)
        loader.show()
        loader.setCancelable(false)
        loader.setCanceledOnTouchOutside(false)
        locationViewModel.locationSelect(locations).observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            loader.cancel()
            SharedPrefsManager.newInstance(requireContext())
                .putString(Preference.PREF_LOCATION, Gson().toJson(locations))

            if (it.data != null) {
                if (it.data.size > 0) {
                    SharedPrefsManager.newInstance(requireContext())
                        .putString(Preference.All_RESTAURANTS, Gson().toJson(it.data))

                    SharedPrefsManager.newInstance(requireContext())
                        .putString(Preference.RESTAURANT_DATA, Gson().toJson(it.data))
                    replaceFragment(mActivity, FavouriteFragment())

                    SharedPrefsManager.newInstance(requireContext())
                        .putBoolean(Preference.HOME_DATA, true)
                }
            } else {
                SharedPrefsManager.newInstance(requireContext())
                    .putString(Preference.All_RESTAURANTS, "")

                SharedPrefsManager.newInstance(requireContext())
                    .putString(Preference.RESTAURANT_DATA, "")

                SharedPrefsManager.newInstance(requireContext())
                    .putBoolean(Preference.HOME_DATA, true)
                replaceFragment(mActivity, FavouriteFragment())
            }
        })


    }

    fun performRecentLocation(addressModel: AddressModel) {

        if (recentLocationList.size == 5) {
//            for (recentList in recentLocationList) {
            recentLocationList.removeAt(0)
//                if (!recentList.addressSingleLine.contains(addressModel.addressSingleLine)) {

            binding.homeToolbar.selectedLocation.text = addressModel.addressSingleLine


//            SharedPrefsManager.newInstance(mActivity)
//                .putString(Preference.SELECTED_LOCATION, Gson().toJson(addressModel))
            recentLocationList.add(addressModel)

            if (recentLocationList.size>0)
            {
                addAdapter(recentLocationList)
            }
            SharedPrefsManager.newInstance(requireContext())
                .putString(
                    Preference.SEARCH_LOCATION_LIST,
                    Gson().toJson(recentLocationList)
                )
            addLocationData(addressModel.lat, addressModel.lng)
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

            if (recentLocationList.size>0)
            {
                addAdapter(recentLocationList)
            }
            /*SharedPrefsManager.newInstance(mActivity)
                .putString(Preference.SELECTED_LOCATION, Gson().toJson(addressModel))*/
            SharedPrefsManager.newInstance(requireContext())
                .putString(
                    Preference.SEARCH_LOCATION_LIST,
                    Gson().toJson(recentLocationList)
                )
            addLocationData(addressModel.lat, addressModel.lng)
        }
    }


    private fun addAdapter(recentLocations: ArrayList<AddressModel>)
    {
        val searchAdapter =
            RecentLocationsAdapter(requireContext(), recentLocations, onAddressClick = {
                onAddressClick(it)
            })
        binding.recentAddressRecycler.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, true)
        binding.recentAddressRecycler.adapter = searchAdapter
        runAnimationAgain(requireContext(),binding.recentAddressRecycler)
        searchAdapter.notifyDataSetChanged()
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

                            Log.e(
                                "ghksfjhfksghjfsgh",
                                task.place.latLng!!.latitude.toString() + "----" + task.place.latLng.toString()
                            )
                        }
                    }.addOnFailureListener { e ->
                        e.printStackTrace()
                        binding.homeToolbar.selectedLocation.text = e.message
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    override fun onResume() {
        super.onResume()
        binding.homeToolbar.imgShoppingCartItemTotal.text =
            SharedPrefsManager.newInstance(requireContext()).getInt(Preference.CART_COUNT, 0)
                .toString()
    }

    override fun getItemTotal(itemTotal: Int) {
        binding.homeToolbar.imgShoppingCartItemTotal.text = itemTotal.toString()
    }
}