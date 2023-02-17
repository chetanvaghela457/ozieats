package com.admin.ozieats_app.ui.home.restaurantDetails

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.admin.ozieats_app.R
import com.admin.ozieats_app.data.LocationRepository
import com.admin.ozieats_app.databinding.ActivityRestaurantDetailsBinding
import com.admin.ozieats_app.model.RestaurantModel
import com.admin.ozieats_app.ui.home.cart.CartActivity
import com.admin.ozieats_app.ui.home.fragments.home.HomeViewModel
import com.admin.ozieats_app.ui.home.item.NetworkConnectionListener
import com.admin.ozieats_app.utils.*
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_restaurant_details.*
import kotlinx.android.synthetic.main.no_internet_layout.view.*

class RestaurantDetailsActivity : BaseActivity(), NetworkConnectionListener,
    LocationRepository.CartItemTotalGet,LocationCallback {

    lateinit var binding: ActivityRestaurantDetailsBinding
    lateinit var restaurantDetailsViewModel: RestaurantDetailsViewModel
    private var isReviewUpdate = false
    lateinit var onReviewGet: LocationRepository.CartItemTotalGet
    var isConnected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_restaurant_details)

        onReviewGet = this

        /*val toolbarViewModel = ViewModelProvider(
            this,
            ToolbarViewModel.ToolbarViewModelFactory(this)
        ).get(ToolbarViewModel::class.java)

        toolbarSetting(
            toolbarViewModel,
            ToolbarModel(getString(R.string.restaurant_detail), false, true),
            this,
            binding.toolbar
        )

        imgBack.setOnClickListener {
            onBackPressed()
        }
        imgShoppingCart.gone()
        txtToolbarTitle.text = "RestaurantDetails"*/

        var homeViewModel = ViewModelProvider(
            this,
            HomeViewModel.HomeViewModelFactory(this, this)
        ).get(HomeViewModel::class.java)

        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.txtToolbarTitle.text = getString(R.string.restaurant_detail)

        restaurantDetailsViewModel =
            ViewModelProvider(
                this,
                RestaurantDetailsViewModel.RestaurantDetailsModelFactory(this)
            ).get(
                RestaurantDetailsViewModel::class.java
            )

        binding.restaurantViewModel = restaurantDetailsViewModel

        val restaurantName = intent.getStringExtra(SendIntents.RESTAURANT_NAME)
        val restaurantImage = intent.getStringExtra(SendIntents.RESTAURANT_IMAGE)
        val restaurantDiscount = intent.getStringExtra(SendIntents.RESTAURANT_DISCOUNT)
        val restaurantAddress = intent.getStringExtra(SendIntents.RESTAURANT_ADDRESS)
        val restaurantCharges = intent.getStringExtra(SendIntents.RESTAURANT_DELIVERY_CHARGE)
        var favourtie = intent.getIntExtra(SendIntents.FAVOURITE, 0)
        val rating = intent.getFloatExtra(SendIntents.RATING, 0f)
        val total_rating = intent.getIntExtra(SendIntents.TOTAL_RATING, 0)
        val restaurant_id = intent.getIntExtra(SendIntents.RESTAURANT_ID, 0)
        val background_image = intent.getStringExtra(SendIntents.BACKGROUND_IMAGE)

        val restaurantModel = RestaurantModel()
        restaurantModel.restaurant_name = restaurantName
        restaurantModel.restaurant_discount = restaurantDiscount
        restaurantModel.delivery_address = restaurantAddress
        restaurantModel.delivery_charges = restaurantCharges
        restaurantModel.totalRating = total_rating
        restaurantModel.totalUserRating = rating
        restaurantModel.favourites = favourtie
        restaurantModel.background_image=background_image

        if (restaurantCharges.isEmpty())
        {
            binding.restaurantDetails.textViewDeliveryCharges.gone()
        }

        if (restaurantAddress.isEmpty())
        {
            binding.restaurantDetails.textViewAddress.gone()
        }

        binding.restaurantDetails.restaurantModel = restaurantModel

        Glide.with(this).asBitmap().load(restaurantImage)
            .into(binding.restaurantDetails.restaurantLogo)

        Glide.with(this).asBitmap().load(background_image)
            .into(binding.restaurantDetails.imageViewMainBacgroundImage)

        binding.imgShoppingCartItemTotal.text =
            SharedPrefsManager.newInstance(this).getInt(Preference.CART_COUNT, 0)
                .toString()

        binding.imgShoppingCart.setOnClickListener {

            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
            finish()

        }

        binding.restaurantDetails.mainRatingbar.rating = rating

        if (favourtie == 1) {
            binding.restaurantDetails.favouriteAddOrRemove.setBackgroundResource(R.drawable.heart_active)
        } else {
            binding.restaurantDetails.favouriteAddOrRemove.setBackgroundResource(R.drawable.heart_inactive)
        }

        binding.restaurantDetails.favouriteAddOrRemove.setOnClickListener {
            val loader = Loader(this, android.R.style.Theme_Translucent_NoTitleBar)
            loader.show()
            loader.setCancelable(false)
            loader.setCanceledOnTouchOutside(false)
            homeViewModel.addOrRemoveToFavourite(restaurant_id).observeForever {

                loader.cancel()
                if (it.status == Result.Status.SUCCESS) {

                    if (favourtie == 1) {
                        favourtie = 0
                    } else {
                        favourtie = 1
                    }
                    var restaurantList = ArrayList<RestaurantModel>()

                    if (favourtie == 1) {


                        binding.restaurantDetails.favouriteAddOrRemove.setBackgroundResource(R.drawable.heart_active)
                        for (i in getTotalRestaurantPreference(this)) {
                            if (i.restaurant_id == restaurant_id) {
                                i.favourites = 1
                            }
                            restaurantList.add(i)

                            SharedPrefsManager.newInstance(this).putString(
                                Preference.RESTAURANT_DATA,
                                Gson().toJson(restaurantList)
                            )
                        }

                    } else {
                        binding.restaurantDetails.favouriteAddOrRemove.setBackgroundResource(R.drawable.heart_inactive)

                        for (i in getTotalRestaurantPreference(this)) {
                            if (i.restaurant_id == restaurant_id) {
                                i.favourites = 0
                            }
                            restaurantList.add(i)

                            SharedPrefsManager.newInstance(this).putString(
                                Preference.RESTAURANT_DATA,
                                Gson().toJson(restaurantList)
                            )
                        }
                    }
                } else {
                    showAlert(this, it.message.toString())
                }

            }
        }



        tabRestaurantDetail.addTab(tabRestaurantDetail.newTab().setText("Menu"))
        tabRestaurantDetail.addTab(tabRestaurantDetail.newTab().setText("Review"))
        tabRestaurantDetail.addTab(tabRestaurantDetail.newTab().setText("Info"))

        tabRestaurantDetail.tabGravity = TabLayout.GRAVITY_FILL

        val adapter =
            RestaurantDetailsPagerAdapter(
                supportFragmentManager,
                tabRestaurantDetail.tabCount, this
            )
        viewPagerTab.adapter = adapter
        viewPagerTab.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(
                tabRestaurantDetail
            )
        )
        tabRestaurantDetail.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPagerTab.currentItem = tab.position
                (viewPagerTab.adapter as RestaurantDetailsPagerAdapter).notifyDataSetChanged()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
    }

    fun updateRestaurantReview(totalReview: Int, averageReview: Float) {
        binding.restaurantDetails.mainRatingbar.rating = averageReview
        binding.restaurantDetails.tvTotalReview.text = totalReview.toString()

        isReviewUpdate = true
    }

    override fun onBackPressed() {
        if (isReviewUpdate) {
            val intent = Intent()
            intent.putExtra("isReviewUpdate", "Yes")
            setResult(Activity.RESULT_OK, intent)
        } else {
            val intent = Intent()
            intent.putExtra("isReviewUpdate", "No")
            setResult(Activity.RESULT_OK, intent)
        }

        finish()
    }

    override fun onResume() {
        super.onResume()

        binding.imgShoppingCartItemTotal.text =
            SharedPrefsManager.newInstance(this).getInt(Preference.CART_COUNT, 0)
                .toString()

        if (isConnected)
        {
            if (!constraintLayoutMain.isVisible())
            {
                binding.noInternetLayout.gone()
                constraintLayoutMain.visible()
            }
            binding.noInternetLayout.retryButton.setOnClickListener {
                binding.noInternetLayout.gone()
                constraintLayoutMain.visible()
                val intent = Intent(this, RestaurantDetailsActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun getItemTotal(itemTotal: Int) {
        binding.imgShoppingCartItemTotal.text = itemTotal.toString()
    }

    override fun changeNetwork(connected: Boolean) {

        isConnected=connected

        if (connected) {
            if (!constraintLayoutMain.isVisible())
            {
                /*binding.noInternetLayout.gone()
                constraintLayoutMain.visible()*/
                binding.noInternetLayout.retryButton.setOnClickListener {
                    binding.noInternetLayout.gone()
                    constraintLayoutMain.visible()
                    val intent = Intent(this, RestaurantDetailsActivity::class.java)
                    startActivity(intent)
                }
            }

        } else {
            binding.noInternetLayout.visible()
            constraintLayoutMain.gone()
        }
    }

    override fun updateUi(location: Location, context: Context) {
        SharedPrefsManager.newInstance(context).putFloat(Preference.PREF_LAT, location.latitude.toFloat())
        SharedPrefsManager.newInstance(context).putFloat(Preference.PREF_LNG, location.longitude.toFloat())
    }
}