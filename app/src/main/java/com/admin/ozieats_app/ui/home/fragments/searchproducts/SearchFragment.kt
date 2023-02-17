package com.admin.ozieats_app.ui.home.fragments.searchproducts

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.admin.ozieats_app.R
import com.admin.ozieats_app.data.LocationRepository
import com.admin.ozieats_app.databinding.FragmentSearchBinding
import com.admin.ozieats_app.model.AddressModel
import com.admin.ozieats_app.model.CategoriesModel
import com.admin.ozieats_app.model.RestaurantModel
import com.admin.ozieats_app.ui.home.HomeActivity
import com.admin.ozieats_app.ui.home.cart.CartActivity
import com.admin.ozieats_app.ui.home.fragments.favourite.RestaurantAdapter
import com.admin.ozieats_app.ui.home.fragments.home.*
import com.admin.ozieats_app.ui.home.restaurantDetails.RestaurantDetailsActivity
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
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*
import kotlin.collections.ArrayList


class SearchFragment : Fragment(), LocationRepository.CartItemTotalGet {

    private lateinit var mBinding: FragmentSearchBinding
    private lateinit var mSearchViewModel: SearchViewModel
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var mActivity: AppCompatActivity
    private lateinit var mMutableSearchResult: TextView
    private lateinit var mFixedSearchResult: TextView
    private lateinit var mHomeRangeAdapter: HomeRangeAdapter
    private lateinit var bottomSheet: BottomSheetBehavior<NestedScrollView>
    private lateinit var bottomSheetLocation: BottomSheetBehavior<NestedScrollView>
    private var recentSearchList: ArrayList<String> = ArrayList()
    private lateinit var locationViewModel: LocationViewModel
    lateinit var onReviewGet: LocationRepository.CartItemTotalGet
    var locationModel: LocationModel? = null
    lateinit var adapter: RestaurantAdapter
    var placesClient: PlacesClient? = null
    var autoCompleteTextView: AutoCompleteTextView? = null
    var autoAdapter: AutoCompleteAdapter? = null
    var new_range = 0
    private var recentLocationList: java.util.ArrayList<AddressModel> = java.util.ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Bind Layout
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_search,
            container,
            false
        )
        onReviewGet = this

        mActivity = activity as HomeActivity

        //Add View model factory
        mSearchViewModel = ViewModelProvider(
            this,
            SearchViewModel.SearchFactoryModel(requireContext())
        ).get(SearchViewModel::class.java)

        homeViewModel = ViewModelProvider(
            this, HomeViewModel.HomeViewModelFactory(requireContext(), this)
        ).get(HomeViewModel::class.java)

        locationViewModel = ViewModelProvider(
            this,
            LocationViewModel.LocationModelFactory(requireContext(), viewLifecycleOwner)
        ).get(LocationViewModel::class.java)

        //Add Listener
        mBinding.searchListener = mSearchViewModel

        bottomSheet = BottomSheetBehavior.from(mBinding.scrollViewSearchRangeHome)
        bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN

        bottomSheetLocation = BottomSheetBehavior.from(mBinding.scrollViewLocationAddress)
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
            mBinding.searchToolbar.selectedLocation.text =
                getCompleteAddressString(
                    requireContext(),
                    locationModel!!.lat,
                    locationModel!!.lng
                )
        }
//        }

        mBinding.useCurrentLocationTextView.setOnClickListener {

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

                    mBinding.searchToolbar.selectedLocation.text =
                        getCompleteAddressString(
                            requireContext(),
                            userLocation.latitude,
                            userLocation.longitude
                        )
                }
            }
        }

        mBinding.searchToolbar.selectedLocation.setOnClickListener {

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
            mBinding.recentAddressRecycler.gone()
        }

        initViews()
        getRestaurantData()
        fetchLocations()

        /*mBinding.searchToolbar.selectedLocation.text =
            getCompleteAddressString(requireContext(), locationModel!!.lat, locationModel!!.lng)*/

        mBinding.searchToolbar.imgShoppingCartItemTotal.text =
            SharedPrefsManager.newInstance(requireContext()).getInt(Preference.CART_COUNT, 0)
                .toString()

        mBinding.searchToolbar.imageViewCartButton.setOnClickListener {

            val intent = Intent(requireContext(), CartActivity::class.java)
            startActivity(intent)

        }

        val searchList = getRecentSearchList(requireContext())

        if (searchList.size > 0) {
            val searchAdapter = RecentSearchAdapter(requireContext(), searchList, onSearchClick = {
                onSearchItemClick(it)
            })
            mBinding.recentSearchesRecycler.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            mBinding.recentSearchesRecycler.adapter = searchAdapter
        } else {
            mBinding.recentSearchesRecycler.gone()
        }

        mBinding.searchToolbar.imageViewMicrophone.setOnClickListener {
            mBinding.searchToolbar.editTextSearch.setText("")
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

        mBinding.searchToolbar.filterButton.setOnClickListener {

            if (bottomSheetLocation.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetLocation.state = BottomSheetBehavior.STATE_HIDDEN
            }
            if (bottomSheet.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
            } else {
                bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        mBinding.closeLocationDialog.setOnClickListener {

            hideSoftKeyboard(mActivity)
            if (bottomSheet.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
            }
            if (bottomSheetLocation.state == BottomSheetBehavior.STATE_EXPANDED) {
                Handler().postDelayed(Runnable {
                    bottomSheetLocation.state = BottomSheetBehavior.STATE_HIDDEN
                },200)
            }
        }

        mBinding.closeFilter.setOnClickListener {

            hideSoftKeyboard(mActivity)
            if (bottomSheetLocation.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetLocation.state = BottomSheetBehavior.STATE_HIDDEN
            }

            if (bottomSheet.state == BottomSheetBehavior.STATE_EXPANDED) {
                Handler().postDelayed(Runnable {
                    bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
                },200)
            }
        }

        recentSearchList.clear()
        recentSearchList.addAll(getRecentSearchList(requireContext()))

        bottomSheet.isHideable = true
        bottomSheetLocation.isHideable = true

        mBinding.searchToolbar.editTextSearch.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(v.text)
                Log.e("CHECK_SRERACH_TEXT", "onCreateView: " + v.text)
                return@OnEditorActionListener true
            }
            false
        })

        val selected_search_string = GlobalVariables.search_data
        if (selected_search_string != null) {
            mBinding.searchToolbar.editTextSearch.setText(selected_search_string)
            filter(selected_search_string, homeViewModel)
            if (!mMutableSearchResult.isVisible())
                mMutableSearchResult.visible()
            val searchTextInDoubleQuote = "\"" + selected_search_string + "\""
            //val next = searchTextInDoubleQuote
            val firstWord = resources.getString(R.string.search_for)
            mMutableSearchResult.text = firstWord + searchTextInDoubleQuote
        }

        mBinding.searchToolbar.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                // TODO: 14-Jun-20 find search result from api
                if (s.toString().isNotEmpty()) {
                    if (!mMutableSearchResult.isVisible())
                        mMutableSearchResult.visible()
                    mBinding.recentSearchesRecycler.visible()
                    val searchTextInDoubleQuote = "\"" + s.toString() + "\""
                    //val next = searchTextInDoubleQuote
                    val firstWord = resources.getString(R.string.search_for)
                    mMutableSearchResult.text = firstWord + searchTextInDoubleQuote
                    filter(s.toString(), homeViewModel)

                } else {
                    mMutableSearchResult.text = ""
                    GlobalVariables.search_data = null
                    mBinding.rvFoodList.gone()
                    mBinding.rvSearchList.gone()
                    mBinding.rvFoodList.visible()
                    mBinding.emptySearchLayout.gone()
                    mBinding.tvLabel.visible()
                    mBinding.recentSearchesRecycler.gone()
                    mMutableSearchResult.gone()
                }
            }

            override fun afterTextChanged(editable: Editable?) {

            }
        })

        return mBinding.root
    }

    private fun initAutoCompleteTextView() {
        mBinding.addLocationEditTextSearch.threshold = 1
        mBinding.addLocationEditTextSearch.onItemClickListener = autocompleteClickListener
        autoAdapter = AutoCompleteAdapter(mActivity, placesClient!!)
        mBinding.addLocationEditTextSearch.setAdapter(autoAdapter)

        mBinding.addLocationEditTextSearch.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->

            if (actionId == EditorInfo.IME_ACTION_DONE) {
                autocompleteClickListener
                hideSoftKeyboard(mActivity)
                mBinding.addLocationEditTextSearch.setText("")
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

        if (bottomSheet.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        }
        bottomSheetLocation.state = BottomSheetBehavior.STATE_HIDDEN

        mBinding.searchToolbar.selectedLocation.text = addressModel.addressFull

        SharedPrefsManager.newInstance(mActivity)
            .putString(Preference.SELECTED_LOCATION, Gson().toJson(addressModel))

        addLocationData(addressModel.lat, addressModel.lng)
    }

    private fun addLocationData(lat: Double, lng: Double) {
        val locations = LocationModel()
        locations.radius = locationModel!!.radius
        locations.user_id = getUserFromPreference(requireContext()).id
        locations.lat = lat
        locations.lng = lng

        if (bottomSheet.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        }
        bottomSheetLocation.state = BottomSheetBehavior.STATE_HIDDEN

        val loader = Loader(context, android.R.style.Theme_Translucent_NoTitleBar)
        loader.show()
        loader.setCancelable(false)
        loader.setCanceledOnTouchOutside(false)
        locationViewModel.locationSelect(locations).observe(viewLifecycleOwner, Observer {

            loader.cancel()
            SharedPrefsManager.newInstance(requireContext())
                .putString(Preference.PREF_LOCATION, Gson().toJson(locations))

            if (it.data != null) {
                if (it.data.size > 0) {
                    SharedPrefsManager.newInstance(requireContext())
                        .putString(Preference.All_RESTAURANTS, Gson().toJson(it.data))

                    SharedPrefsManager.newInstance(requireContext())
                        .putString(Preference.RESTAURANT_DATA, Gson().toJson(it.data))
                    replaceFragment(mActivity, HomeFragment())
                }
            } else {
                SharedPrefsManager.newInstance(requireContext())
                    .putString(Preference.All_RESTAURANTS, "")

                SharedPrefsManager.newInstance(requireContext())
                    .putString(Preference.RESTAURANT_DATA, "")
                replaceFragment(mActivity, HomeFragment())
            }
        })
    }

    fun performRecentLocation(addressModel: AddressModel) {

        if (recentLocationList.size == 5) {
//            for (recentList in recentLocationList) {
            recentLocationList.removeAt(0)
//                if (!recentList.addressSingleLine.contains(addressModel.addressSingleLine)) {

            mBinding.searchToolbar.selectedLocation.text = addressModel.addressSingleLine


//            SharedPrefsManager.newInstance(mActivity)
//                .putString(Preference.SELECTED_LOCATION, Gson().toJson(addressModel))
            recentLocationList.add(addressModel)

            if (recentLocationList.size > 0) {
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
            mBinding.searchToolbar.selectedLocation.text = addressModel.addressSingleLine
            Log.e(
                "ghhghg",
                "performRecentLocation: " + addressModel.addressFull + "---" + addressModel.addressSingleLine
            )
            recentLocationList.add(addressModel)

            if (recentLocationList.size > 0) {
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


    private fun addAdapter(recentLocations: java.util.ArrayList<AddressModel>) {
        val searchAdapter =
            RecentLocationsAdapter(requireContext(), recentLocations, onAddressClick = {
                onAddressClick(it)
            })
        mBinding.recentAddressRecycler.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, true)
        mBinding.recentAddressRecycler.adapter = searchAdapter
    }

    private val autocompleteClickListener: AdapterView.OnItemClickListener =
        AdapterView.OnItemClickListener { adapterView, view, i, l ->
            try {
                val item: AutocompletePrediction = autoAdapter?.getItem(i)!!
                var placeID: String? = null
                placeID = item.getPlaceId()

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
                        mBinding.searchToolbar.selectedLocation.text = e.message
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    private fun onSearchItemClick(searchedItem: String) {

        mBinding.searchToolbar.editTextSearch.append(searchedItem)
        GlobalVariables.search_data = searchedItem
        mBinding.searchToolbar.editTextSearch.setText(searchedItem)
        filter(searchedItem, homeViewModel)
        if (!mMutableSearchResult.isVisible())
            mMutableSearchResult.visible()
        val searchTextInDoubleQuote = "\"" + searchedItem + "\""
        //val next = searchTextInDoubleQuote
        val firstWord = resources.getString(R.string.search_for)
        mMutableSearchResult.text = firstWord + searchTextInDoubleQuote

    }

    private fun fetchLocations() {
        val locationViewModel = ViewModelProvider(
            this,
            LocationViewModel.LocationModelFactory(requireContext(), viewLifecycleOwner)
        ).get(LocationViewModel::class.java)


        locationViewModel.getAllRangeList().observe(viewLifecycleOwner, Observer {

            mHomeRangeAdapter = HomeRangeAdapter(requireContext(), it,
                locationViewModel, viewLifecycleOwner, homeViewModel,
                locationModel!!, onRangeClick = {
                    OnRangeClick(it)
                })

            recyclerViewRanges.setHasFixedSize(true)
            recyclerViewRanges.itemAnimator = null
            recyclerViewRanges.isNestedScrollingEnabled = false
            recyclerViewRanges.adapter = mHomeRangeAdapter
            runAnimationAgain(requireContext(),recyclerViewRanges)
            mHomeRangeAdapter.notifyDataSetChanged()
        })
    }

    private fun OnRangeClick(range: Int) {

        bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN

        val locations = getLocationFromPreference(requireContext())

        val locationModel = LocationModel()
        locationModel.lat = locations.lat
        locationModel.lng = locations.lng
        locationModel.radius = range
        locationModel.user_id = locations.user_id

        val loader = Loader(context, android.R.style.Theme_Translucent_NoTitleBar)
        loader.show()
        loader.setCancelable(false)
        loader.setCanceledOnTouchOutside(false)
        locationViewModel.locationSelect(locationModel).observe(viewLifecycleOwner, Observer {

            loader.cancel()
            SharedPrefsManager.newInstance(requireContext())
                .putString(Preference.PREF_LOCATION, Gson().toJson(locationModel))

            if (it.data != null) {
                if (it.data.size > 0) {
                    SharedPrefsManager.newInstance(requireContext())
                        .putString(Preference.All_RESTAURANTS, Gson().toJson(it.data))

                    SharedPrefsManager.newInstance(requireContext())
                        .putString(Preference.RESTAURANT_DATA, Gson().toJson(it.data))
                    replaceFragment(mActivity, HomeFragment())
                }
            } else {
                SharedPrefsManager.newInstance(requireContext())
                    .putString(Preference.All_RESTAURANTS, "")

                SharedPrefsManager.newInstance(requireContext())
                    .putString(Preference.RESTAURANT_DATA, "")
                replaceFragment(mActivity, HomeFragment())
            }

        })
    }

    private fun performSearch(text: CharSequence?) {

        if (recentSearchList.size == 10) {
            recentSearchList.removeAt(0)
            if (text.toString().isNotEmpty()) {
                if (!recentSearchList.contains(text.toString())) {
                    recentSearchList.add(text.toString())
                    SharedPrefsManager.newInstance(requireContext())
                        .putString(Preference.SEARCH_LIST, Gson().toJson(recentSearchList))
                }
            }
        } else {
            if (!recentSearchList.contains(text.toString())) {
                recentSearchList.add(text.toString())
                SharedPrefsManager.newInstance(requireContext())
                    .putString(Preference.SEARCH_LIST, Gson().toJson(recentSearchList))
            }
        }
    }

    private fun filter(
        text: String,
        homeViewModel: HomeViewModel
    ) {
        var restaurantList = ArrayList<RestaurantModel>()

        for (s in getTotalRestaurantPreference(requireContext())) {

            if (s.restaurant_name.toLowerCase().contains(text.toLowerCase())) {

                restaurantList.add(s)

            } else {
                for (itemName in s.food_list!!) {
                    var isMatch = false
                    for (item in itemName.menuItemChildModel!!) {

                        //Log.e("zgkdaghjad", "filter: "+text )

                        if (item.itemName.toLowerCase().contains(text.toLowerCase())) {

                            Log.e("zgkdaghjad===>", "filter: " + text)

                            restaurantList.add(s)
                            isMatch = true
                            break
                        }
                    }
                    if (isMatch) {
                        break
                    }
                }
            }
        }

        if (restaurantList.size > 0) {
            mBinding.rvFoodList.gone()
            mBinding.rvSearchList.visible()
            mBinding.emptySearchLayout.gone()
            mBinding.tvLabel.gone()
            adapter = RestaurantAdapter(
                requireContext(),
                restaurantList,
                homeViewModel,
                onRestaurantClick = {
                    performSearch(mBinding.searchToolbar.editTextSearch.text)
                    openActivity(it)

                }, onFavouriteButtonClick = { it, isFavourite ->
                    onFavouriteButtonClick(it, isFavourite)
                })
            mBinding.rvSearchList.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            mBinding.rvSearchList.adapter = adapter
            runAnimationAgain(requireContext(),mBinding.rvSearchList)
            adapter.notifyDataSetChanged()

        } else {
            Log.e(":gsdghskjg=====>", "EmptyValu")
            mBinding.rvFoodList.gone()
            mBinding.rvSearchList.gone()
            mBinding.emptySearchLayout.visible()
            mBinding.tvLabel.gone()
        }

    }

    companion object {
        fun newInstance(): SearchFragment = SearchFragment()
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
                loader.cancel()

                val restaurantList = java.util.ArrayList<RestaurantModel>()

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

                /* if (it.status == Result.Status.SUCCESS) {

                     if (restaurants.favourites == 1) {
                         restaurants.favourites = 0
                     } else {
                         restaurants.favourites = 1
                     }
                     val restaurantList = java.util.ArrayList<RestaurantModel>()

                     if (restaurants.favourites == 1) {

                         imageview.setBackgroundResource(R.drawable.heart_active)
                         adapter.notifyDataSetChanged()
                         for (i in getTotalRestaurantPreference(requireContext())) {
                             if (i.restaurant_id == restaurants.restaurant_id) {
                                 i.favourites = 1
                             }
                             restaurantList.add(i)

                             SharedPrefsManager.newInstance(requireContext()).putString(
                                 Preference.RESTAURANT_DATA,
                                 Gson().toJson(restaurantList)
                             )
                         }

                     } else {
                         imageview.setBackgroundResource(R.drawable.heart_inactive)

                         adapter.notifyDataSetChanged()
                         for (i in getTotalRestaurantPreference(requireContext())) {
                             if (i.restaurant_id == restaurants.restaurant_id) {
                                 i.favourites = 0
                             }


                             restaurantList.add(i)

                             SharedPrefsManager.newInstance(requireContext()).putString(
                                 Preference.RESTAURANT_DATA,
                                 Gson().toJson(restaurantList)
                             )
                         }
                     }
                 } else {
                     showAlert(requireContext(), it.message.toString())
                 }*/

            }

    }

    private fun initViews() {
        mMutableSearchResult = mBinding.tvMutableSearchResult
        mFixedSearchResult = mBinding.tvFixedSearchResult
        mBinding.rvFoodList.layoutManager = GridLayoutManager(mActivity, 2)
        mBinding.rvFoodList.addItemDecoration(ItemOffsetDecoration(mActivity, R.dimen._5sdp))
    }

    private fun getRestaurantData() {
        mSearchViewModel.getAllCategoriesItems().observe(viewLifecycleOwner,
            Observer {
                setAdapter(it)
            })
    }

    private fun setAdapter(mRestaurantList: ArrayList<CategoriesModel>?) {
        val adapter = FoodCategoriesAdapter(mActivity, mRestaurantList!!, onCategoryClick = {
            onCategoryClick(it)
        }, noDataFound = {
            mBinding.rvFoodList.gone()
            mBinding.rvSearchList.gone()
            mBinding.emptySearchLayout.visible()
            mBinding.tvLabel.gone()

        })
        mBinding.rvFoodList.adapter = adapter
        runAnimationAgain(requireContext(),mBinding.rvFoodList)
        adapter.notifyDataSetChanged()
    }

/*
    fun fromHtml(html: String?): Spanned? {
        return when {
            html == null -> {
                // return an empty spannable if the html is null
                SpannableString("")
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                // FROM_HTML_MODE_LEGACY is the behaviour that was used for versions below android N
                // we are using this flag to give a consistent behaviour
                Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
            }
            else -> {
                // TODO: 14-Jun-20 Html.fromHtml(""), This method is deprecated in Android N
                Html.fromHtml(html)
            }
        }
    }*/

    private fun onCategoryClick(restaurantArray: ArrayList<RestaurantModel>) {

        if (bottomSheet.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        }
        if (bottomSheetLocation.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetLocation.state = BottomSheetBehavior.STATE_HIDDEN
        }
        if (restaurantArray.size > 0) {
            mBinding.rvFoodList.gone()
            mBinding.rvSearchList.visible()
            mBinding.emptySearchLayout.gone()
            mBinding.tvLabel.gone()

            adapter = RestaurantAdapter(
                requireContext(),
                restaurantArray,
                homeViewModel,
                onRestaurantClick = {
                    openActivity(it)
                }, onFavouriteButtonClick = { it, isFavourite ->
                    onFavouriteButtonClick(it, isFavourite)
                }
            )
            mBinding.rvSearchList.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            mBinding.rvSearchList.adapter = adapter
            runAnimationAgain(requireContext(),mBinding.rvSearchList)
            adapter.notifyDataSetChanged()
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

    override fun onResume() {
        super.onResume()
        mBinding.searchToolbar.imgShoppingCartItemTotal.text =
            SharedPrefsManager.newInstance(requireContext()).getInt(Preference.CART_COUNT, 0)
                .toString()
        showSoftKeyboard(mActivity, mBinding.searchToolbar.editTextSearch)
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
            mBinding.searchToolbar.editTextSearch.append(topResult)
            GlobalVariables.search_data = topResult
            mBinding.searchToolbar.editTextSearch.setText(topResult)
            filter(topResult, homeViewModel)
            if (!mMutableSearchResult.isVisible())
                mMutableSearchResult.visible()
            val searchTextInDoubleQuote = "\"" + topResult + "\""
            //val next = searchTextInDoubleQuote
            val firstWord = resources.getString(R.string.search_for)
            mMutableSearchResult.text = firstWord + searchTextInDoubleQuote

        }

    }

    override fun getItemTotal(itemTotal: Int) {
        mBinding.searchToolbar.imgShoppingCartItemTotal.text = itemTotal.toString()
    }
}