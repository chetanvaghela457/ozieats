package com.admin.ozieats_app.ui.location

import android.Manifest
import android.app.Activity
import android.content.Context
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.admin.ozieats_app.data.BannerRepository
import com.admin.ozieats_app.data.LocationRepository
import com.admin.ozieats_app.model.AdvertisementModel
import com.admin.ozieats_app.model.RestaurantModel
import com.admin.ozieats_app.utils.Loader
import com.admin.ozieats_app.utils.Permissions
import com.admin.ozieats_app.utils.Result
import com.admin.ozieats_app.utils.getJsonDataFromAsset
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import pub.devrel.easypermissions.EasyPermissions

class LocationViewModel(
    private var context: Context,
    private val viewLifecycleOwner: LifecycleOwner
) : ViewModel() {

    private var ranges = MutableLiveData<ArrayList<RangeModel>>()
    private var restaurantsList = MutableLiveData<Result<ArrayList<RestaurantModel>>>()
    var locationRepository: LocationRepository = LocationRepository(context)
    var advertisements = MutableLiveData<Result<ArrayList<AdvertisementModel>>>()
    val progress = ObservableField<Boolean>()
    var bannerRepository = BannerRepository(context)

    val permissions =
        arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

    fun onLocationAllowButtonClick(view: View) {

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

    fun getAllRangeList(): LiveData<ArrayList<RangeModel>> {
        val jsonFileString = getJsonDataFromAsset(context, "search_range.json")
        val type = object : TypeToken<ArrayList<RangeModel>>() {}.type
        ranges.postValue(Gson().fromJson(jsonFileString, type))
        return ranges
    }

    /*fun getAllAdvertisementList(): MutableLiveData<Result<ArrayList<AdvertisementModel>>> {

        //advertisements.postValue(getAdvertismentPreference(context))

        progress.set(true)
        bannerRepository.getBannerRepository(getLocationFromPreference(context)).observeForever {

            progress.set(false)
            advertisements.postValue(it)

        }
        return advertisements
    }*/


    class LocationModelFactory(
        private val context: Context,
        private val viewLifecycleOwner: LifecycleOwner
    ) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return LocationViewModel(context, viewLifecycleOwner) as T
        }
    }

    fun locationSelect(locationModel: LocationModel): MutableLiveData<Result<ArrayList<RestaurantModel>>> {

        val loader = Loader(context, android.R.style.Theme_Translucent_NoTitleBar)
        loader.show()
        loader.setCancelable(false)
        loader.setCanceledOnTouchOutside(false)
        locationRepository.getNearByRestaurant(locationModel).observeForever {
            loader.cancel()

            restaurantsList.postValue(it)

        }
        return restaurantsList
    }


}