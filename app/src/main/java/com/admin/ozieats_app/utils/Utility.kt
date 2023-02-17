package com.admin.ozieats_app.utils

import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.admin.ozieats_app.R
import com.admin.ozieats_app.model.*
import com.admin.ozieats_app.ui.auth.SocialLoginActivity
import com.admin.ozieats_app.ui.location.LocationModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList


fun getJsonDataFromAsset(context: Context, fileName: String): String? {
    val jsonString: String
    try {
        jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
    } catch (ioException: IOException) {
        ioException.printStackTrace()
        return null
    }
    return jsonString
}

fun View.snack(message: String, duration: Int = Snackbar.LENGTH_LONG) {
    Snackbar.make(this, message, duration).show()
}

fun getDeviceToken(context: Context): String {

    val toekn = context.getSharedPreferences("toekn_pref", MODE_PRIVATE)
        .getString(Preference.PREF_TOKEN, "")
//    val android_id: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    return toekn.toString()
}

fun getCompleteAddressString(
    context: Context,
    LATITUDE: Double,
    LONGITUDE: Double
): String? {
    var strAdd = ""
    val geocoder = Geocoder(context, Locale.getDefault())
    try {
        val addresses: List<Address>? =
            geocoder.getFromLocation(LATITUDE, LONGITUDE, 1)
        if (addresses != null) {
            val returnedAddress: Address = addresses[0]
            val strReturnedAddress = StringBuilder("")
            for (i in 0..returnedAddress.maxAddressLineIndex) {
                strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n")
            }
            val address = addresses[0].getAddressLine(0)
            val city = addresses[0].locality
            val state = addresses[0].adminArea
            val zip = addresses[0].postalCode
            val country = addresses[0].countryName

            strAdd = address
        } else {
            Toast.makeText(context, "No Address returned!", Toast.LENGTH_LONG).show()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return strAdd
}

fun getUserFromPreference(context: Context): User {
    var user = User()
    val json = SharedPrefsManager.newInstance(context).getString(Preference.PREF_USER)
    if (json != null) {
        val type = object : TypeToken<User>() {}.type
        user = Gson().fromJson(json, type)
    }
    return user
}
/*
fun getLocationFromPreference(context: Context): LocationModel {

    var user = LocationModel()
    val json = SharedPrefsManager.newInstance(context).getString(Preference.PREF_LOCATION)
    if (json != null && json.isNotEmpty()) {

        println("JsonString ==> $json")

        val type = object : TypeToken<LocationModel>() {}.type

        println("dlkjdlgksd" + Gson().fromJson(json, type))

        user = Gson().fromJson(json, type)
    }
    return user
}*/

fun getMyOrderPreference(context: Context): MyOrdersModel {
    var user = MyOrdersModel()
    Log.e("QRCODE", "onCreate: ===> ")
    val json =
        context.getSharedPreferences("sPrefs", MODE_PRIVATE).getString(Preference.MY_ORDER, "")
    Log.e("QRCODE", "onCreate: " + json.toString())
    if (json != null && json.isNotEmpty()) {
        val type = object : TypeToken<MyOrdersModel>() {}.type
        user = Gson().fromJson(json, type)
    }
    return user
}

fun getOrderTimePreference(context: Context): ArrayList<OrderTime> {
    var user = ArrayList<OrderTime>()
    val json = SharedPrefsManager.newInstance(context).getString(Preference.ORDERTIMING)
    if (json != null && json.isNotEmpty()) {
        val type = object : TypeToken<ArrayList<OrderTime>>() {}.type
        user = Gson().fromJson(json, type)
    }
    return user
}

fun addOrderTimeArrayToPreference(context: Context, timingArray: ArrayList<OrderTime>) {
    SharedPrefsManager.newInstance(context)
        .putString(Preference.ORDERTIMING, Gson().toJson(timingArray))
}

fun addLatLngToPreference(context: Context, latLng: LatLng) {
    SharedPrefsManager.newInstance(context).putFloat(Preference.PREF_LAT, latLng.latitude.toFloat())
    SharedPrefsManager.newInstance(context)
        .putFloat(Preference.PREF_LNG, latLng.longitude.toFloat())
}

fun getAllOrdersPreference(context: Context): ArrayList<MyOrdersModel> {
    var user = ArrayList<MyOrdersModel>()
    val json = SharedPrefsManager.newInstance(context).getString(Preference.ALLORDERS)
    if (json != null && json.isNotEmpty()) {
        val type = object : TypeToken<ArrayList<MyOrdersModel>>() {}.type
        user = Gson().fromJson(json, type)
    }
    return user
}

fun getReviewFromPreference(context: Context): ArrayList<ReviewModel> {
    var user = ArrayList<ReviewModel>()
    val json = SharedPrefsManager.newInstance(context).getString(Preference.REVIEWS)
    if (json != null && json.isNotEmpty()) {
        val type = object : TypeToken<ArrayList<ReviewModel>>() {}.type
        user = Gson().fromJson(json, type)
    }
    return user
}

fun getAdvertismentItemFromPreference(context: Context): AdvertisementModel {
    var user = AdvertisementModel()
    val json = SharedPrefsManager.newInstance(context).getString(Preference.ADVERTISMENT_ITEM)
    if (json != null && json.isNotEmpty()) {
        val type = object : TypeToken<AdvertisementModel>() {}.type
        user = Gson().fromJson(json, type)
    }
    return user
}

fun getMenuItemChildFromPreference(context: Context): MenuItemChildModel {
    var user = MenuItemChildModel()
    val json = SharedPrefsManager.newInstance(context).getString(Preference.CHILD_DATA)
    if (json != null && json.isNotEmpty()) {
        val type = object : TypeToken<MenuItemChildModel>() {}.type
        user = Gson().fromJson(json, type)
    }
    return user
}

fun getAddOnsFromPreference(context: Context): ArrayList<AddOnsHeaderModel> {
    var user = ArrayList<AddOnsHeaderModel>()
    val json = SharedPrefsManager.newInstance(context).getString(Preference.ADD_ONS)
    if (json != null) {
        val type = object : TypeToken<ArrayList<AddOnsHeaderModel>>() {}.type
        user = Gson().fromJson(json, type)
    }
    return user
}


/*fun getAllRestaurantPreference(context: Context): ArrayList<RestaurantModel> {
    var user = ArrayList<RestaurantModel>()
    val json = SharedPrefsManager.newInstance(context).getString(Preference.All_RESTAURANTS)
    if (json != null && json.isNotEmpty()) {
        val type = object : TypeToken<ArrayList<RestaurantModel>>() {}.type
        user = Gson().fromJson(json, type)
    }
    return user
}*/

fun getAllCategoriesPreference(context: Context): ArrayList<CategoriesModel> {
    var user = ArrayList<CategoriesModel>()
    val json = SharedPrefsManager.newInstance(context).getString(Preference.SEARCH_DATA)
    if (json != null && json.isNotEmpty()) {
        val type = object : TypeToken<ArrayList<CategoriesModel>>() {}.type
        user = Gson().fromJson(json, type)
    }
    return user
}

fun getTotalRestaurantPreference(context: Context): ArrayList<RestaurantModel> {
    var user = ArrayList<RestaurantModel>()
    val json = SharedPrefsManager.newInstance(context).getString(Preference.RESTAURANT_DATA)
    if (json != null && json.isNotEmpty()) {
        val type = object : TypeToken<ArrayList<RestaurantModel>>() {}.type
        user = Gson().fromJson(json, type)

    }
    return user
}

fun getFavouriteRestaurantPreference(context: Context): ArrayList<RestaurantModel> {
    var user = ArrayList<RestaurantModel>()
    val json =
        SharedPrefsManager.newInstance(context).getString(Preference.FAVOURITE_RESTAURANT_DATA)
    if (json != null && json.isNotEmpty()) {
        val type = object : TypeToken<ArrayList<RestaurantModel>>() {}.type
        user = Gson().fromJson(json, type)

    }
    return user
}

fun getAdvertismentPreference(context: Context): ArrayList<AdvertisementModel> {
    var user = ArrayList<AdvertisementModel>()
    val json = SharedPrefsManager.newInstance(context).getString(Preference.ADVERTISMENT_DATA)
    if (json != null && json.isNotEmpty()) {
        val type = object : TypeToken<ArrayList<AdvertisementModel>>() {}.type
        user = Gson().fromJson(json, type)
    }
    return user
}

fun getRestaurantFromPreference(context: Context): RestaurantModel {
    var user = RestaurantModel()
    val json = SharedPrefsManager.newInstance(context).getString(Preference.RESTAURANTS)
    if (json != null) {
        val type = object : TypeToken<RestaurantModel>() {}.type
        user = Gson().fromJson(json, type)
    }
    return user
}

fun getLocationFromPreference(context: Context): LocationModel {
    var user = LocationModel()
    val json = SharedPrefsManager.newInstance(context).getString(Preference.PREF_LOCATION)
    if (json != null && json.isNotEmpty()) {
        val type = object : TypeToken<LocationModel>() {}.type
        user = Gson().fromJson(json, type)
    }
    return user
}

fun getCheckReviewFromPreference(context: Context): CheckForReviewModel {
    var user = CheckForReviewModel()
    val json = SharedPrefsManager.newInstance(context).getString(Preference.CHECKIFORDERED)
    if (json != null && json.isNotEmpty()) {
        val type = object : TypeToken<CheckForReviewModel>() {}.type
        user = Gson().fromJson(json, type)
    }
    return user
}

fun getRecentSearchList(context: Context): ArrayList<String> {
    var user = ArrayList<String>()
    val json = SharedPrefsManager.newInstance(context).getString(Preference.SEARCH_LIST)

    Log.e("JSON_DATA", "getRecentSearchList: " + json)

    if (json != null && json.isNotEmpty()) {
        val type = object : TypeToken<ArrayList<String>>() {}.type
        user = Gson().fromJson(json, type)
    }
    return user
}

fun getRecentLocationList(context: Context): ArrayList<AddressModel> {
    var user = ArrayList<AddressModel>()
    val json = SharedPrefsManager.newInstance(context).getString(Preference.SEARCH_LOCATION_LIST)

    Log.e("JSON_DATA", "getRecentSearchList: " + json)

    if (json != null && json.isNotEmpty()) {
        val type = object : TypeToken<ArrayList<AddressModel>>() {}.type
        user = Gson().fromJson(json, type)
    }
    return user
}

fun getSelectedLocation(context: Context): AddressModel {
    var user = AddressModel()
    val json = SharedPrefsManager.newInstance(context).getString(Preference.SELECTED_LOCATION)

    Log.e("JSON_DATA", "getRecentSearchList: " + json)

    if (json != null && json.isNotEmpty()) {
        val type = object : TypeToken<AddressModel>() {}.type
        user = Gson().fromJson(json, type)
    }
    return user
}

fun logout(context: Context?) {
    if (context != null) {
        SharedPrefsManager.newInstance(context).putString(Preference.PREF_TOKEN, "")
        SharedPrefsManager.newInstance(context).putString(Preference.PREF_USER, "")
        SharedPrefsManager.newInstance(context).putString(Preference.PREF_SUPPLIER, "")
        SharedPrefsManager.newInstance(context).putString(Preference.All_RESTAURANTS, "")
        SharedPrefsManager.newInstance(context).putString(Preference.ADVERTISMENT_DATA, "")
        SharedPrefsManager.newInstance(context).putString(Preference.RESTAURANT_DATA, "")
        SharedPrefsManager.newInstance(context).putString(Preference.RESTAURANTS, "")
        SharedPrefsManager.newInstance(context).putBoolean(Preference.IS_LOGGEDIN, false)
        context.startActivity(Intent(context, SocialLoginActivity::class.java))
        (context as Activity).finish()
    }

}

fun isValidPassword(password: String): Boolean {
    val regExpn = "^[a-z0-9_$@.!%*?&]{6,24}$"
    val inputStr: CharSequence = password
    val pattern =
        Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE)
    val matcher = pattern.matcher(inputStr)
    return matcher.matches()
}


fun replaceFragment(mActivity: AppCompatActivity, fragment: Fragment) {
    val transaction = mActivity.supportFragmentManager.beginTransaction()
    transaction.replace(R.id.container, fragment)
//    transaction.addToBackStack(null)
    transaction.commit()
}

fun hideSoftKeyboard(activity: AppCompatActivity) {
    val inputMethodManager: InputMethodManager =
        activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    if(inputMethodManager.isAcceptingText)
    {
        inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
    }
}

fun showSoftKeyboard(activity: AppCompatActivity, view: View) {
    val inputMethodManager =
        activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    view.requestFocus()
    inputMethodManager.showSoftInput(view, 0)
}

