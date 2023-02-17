package com.admin.ozieats_app.utils

//const val BASE_URL = "http://15.207.32.168/api/"
//const val BASE_URL = "https://opensourceinfotech.in/ozi_eats/api/"
//const val BASE_URL = "https://restaurants.ozieats.com.au/api/"
//const val BASE_URL = "http://www.ozieats.com.au/ozi_eats/api/"
//const val BASE_URL = "http://192.168.1.111/mohit_ozieats/api/"

class Permissions {
    companion object {
        const val LOCATION_PERMISSION = 100
        const val READ_STORAGE = 101
        const val MICORPHONE = 102
        const val CAMERA = 103
        const val IMAGE_PICK_CODE = 1000
        const val IMAGE_CAMERA_CODE = 1001
    }
}

class SendIntents {
    companion object {
        const val BACKGROUND_IMAGE= "background_image"
        const val ITEMDETAILS = "itemdetails"
        const val CHECK_PATH = "check_path"
        const val RESTAURANT_NAME = "restaurant_name"
        const val RESTAURANT_ID = "restaurant_id"
        const val RESTAURANT_IMAGE = "restaurant_image"
        const val RESTAURANT_ADDRESS = "restaurant_address"
        const val RESTAURANT_DELIVERY_CHARGE = "restaurant_delivery_charge"
        const val FAVOURITE = "favourite"
        const val TOTAL_RATING = "total_rating"
        const val RATING = "rating"
        const val RESTAURANT_DISCOUNT = "restaurant_discount"
    }
}

class Preference {
    companion object {

        const val ISFROMDIRECTORDER="isfromdirectorder"
        const val ALLORDERS="allorders"
        const val ORDERTIMING="ordertiming"
        const val CHECKIFORDERED="checkifordered"
        const val SEARCH_DATA="search_data"
        const val ISFAVOURITE: String="isfavourite"
        const val CURRENT_TIME: String="current_time"
        const val ORDER_REQUEST_ID: String="order_request_id"
        const val PHONE="phone"
        const val OTP: String="otp"
        const val ORDER_PROGRESS: String="order_progress"
        const val CHILD_DATA: String="child_data"
        const val SEARCH_LIST: String = "search_list"
        const val SEARCH_LOCATION_LIST: String = "search_location_list"
        const val SELECTED_LOCATION: String = "selected_location"
        const val PREF_LOCATION = "pref_location"
        const val PREF_TOKEN = "pref_token"
        const val PREF_USER = "pref_user"
        const val PREF_SUPPLIER = "pref_supplier"
        const val PREF_LAT = "pref_lat"
        const val PREF_LNG = "pref_lng"
        const val RESTAURANT_DATA = "restaurant_data"
        const val FAVOURITE_RESTAURANT_DATA = "favourite_restaurant_data"
        const val RESTAURANTS = "restaurants"
        const val FOODDATA = "food_data"
        const val IS_LOGGEDIN = "is_loggedIn"
        const val RESTAURANT_ID = "restaurant_id"
        const val All_RESTAURANTS = "all_restaurants"
        const val ADVERTISMENT_DATA = "advertisment_data"
        const val REVIEWS = "reviews"
        const val CART_COUNT = "cart_count"
        const val ADD_ONS = "add_ons"
        const val ADVERTISMENT_ITEM = "advertisment_item"
        const val MY_ORDER = "my_order"
        const val ORDER_ID = "order_id"
        const val ORDER_ID_STRING = "order_id_string"
        const val HOME_DATA="home_data"
    }
}

class Key {
    companion object {
        const val CHECK="check"
        const val OTP = "otp"
        const val SUCCESS = "success"
        const val STATUS = "status"
        const val TOKEN = "token"
        const val DATA = "data"
        const val RESTAURANTS = "restaurants"
        const val FOODLIST = "food_list"
        const val MESSAGE = "message"
        const val CART = "cart"
        const val RESTAURANT_DATA = "restaurant_data"
        const val FAVOURITES_LIST = "favourite_list"
        const val ADVERTISMENT = "advertisment"
        const val ORDERS = "past_orders"
        const val ORDERS_FAV = "past_orders"
        const val REVIEWS = "reviews"
        const val PROFILE_IMAGE = "image_path"
    }
}