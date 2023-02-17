package com.admin.ozieats_app.api

import com.admin.ozieats_app.model.*
import com.admin.ozieats_app.ui.location.LocationModel
import com.admin.ozieats_app.ui.profile.ChangePasswordModel
import com.google.gson.JsonElement
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiClient {
    @POST("register")
    fun registerUser(@Body user: User): Observable<JsonElement>

    @POST("login")
    fun loginUser(@Body user: User): Observable<JsonElement>

    @POST("change_password")
    fun changePassword(@Body changePasswordModel: ChangePasswordModel): Observable<JsonElement>

    @POST("forgot_password")
    fun forgetPassword(@Body user: User): Observable<JsonElement>

    @POST("update_password")
    fun updatePassword(@Body user: User): Observable<JsonElement>

    @POST("change_profile_image")
    fun changeProfileImage(@Body profileImageModel: ProfileImageModel): Observable<JsonElement>

    @POST("get_nearby_restaurant")
    fun getNearByRestaurants(@Body locationModel: LocationModel): Observable<JsonElement>

    @POST("get_food_list")
    fun getMenuItem(@Body allIdsModel: AllIdsModel): Observable<JsonElement>

    @POST("add_to_cart")
    fun addToCart(@Body addToCartModel: AddToCartModel): Observable<JsonElement>

    @POST("reduce_from_cart")
    fun reduceFromCart(@Body cartButtons: CartButtons): Observable<JsonElement>

    @POST("increment_from_cart")
    fun incrementToCart(@Body cartButtons: CartButtons): Observable<JsonElement>

    /*@POST("check_cart")
    fun checkCart(@Query("user_id") user_id: Int): Observable<JsonElement>*/
    @POST("check_cart")
    fun checkCart(@Body user_id:UserIdModel): Observable<JsonElement>

    @POST("check_review")
    fun checkReview(@Body allIdsModel: AllIdsModel): Observable<JsonElement>

    @POST("update_order_status")
    fun updateOrderStatus(
        @Body updateStatusModel: UpdateStatusModel
    ): Observable<JsonElement>

    @POST("push_notification")
    fun pushNotification(
        @Body pushNotificationModel: PushNotificationModel
    ): Observable<JsonElement>

    @POST("add_car_number")
    fun addCarNumber(
        @Body carNumberModel: CarNumberModel
    ): Observable<JsonElement>

    @POST("update_favourite")
    fun addToFavourite(
        @Body allIdsModel: AllIdsModel
    ): Observable<JsonElement>

    @POST("order_favourite")
    fun addToFavouriteOrders(
        @Body favouriteOrderModel: FavouriteOrderModel
    ): Observable<JsonElement>

    @POST("getRestaurantAdvertisment")
    fun getBannersList(@Body locationModel: LocationModel): Observable<JsonElement>

    //categotiesList Api
    @POST("get_filter_list")
    fun categoriesList(@Body categoryListModel: CategoryListModel): Observable<JsonElement>

    @POST("restaurant_ratings")
    fun addRestaurantReview(@Body reviewModel: ReviewModel): Observable<JsonElement>

    @POST("getRestaurantRating")
    fun getRestaurantReview(@Body allIdsModel: AllIdsModel): Observable<JsonElement>

    @POST("get_favourite_list")
    fun getFavouriteRestaurant(@Body userIdModel: UserIdModel): Observable<JsonElement>

    @POST("get_favourite_orders")
    fun getFavouriteOrders(@Body userIdModel: UserIdModel): Observable<JsonElement>

    @POST("order_history")
    fun orderHistory(@Body userIdModel: UserIdModel): Observable<JsonElement>

    @POST("paynow")
    fun orderPlace(@Body placeOrderModel: PlaceOrderModel): Observable<JsonElement>

}