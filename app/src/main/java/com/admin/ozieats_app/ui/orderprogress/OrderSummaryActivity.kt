package com.admin.ozieats_app.ui.orderprogress

import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.admin.ozieats_app.R
import com.admin.ozieats_app.databinding.ActivityOrderSummaryBinding
import com.admin.ozieats_app.model.MyOrdersModel
import com.admin.ozieats_app.model.ToolbarModel
import com.admin.ozieats_app.model.ToolbarViewModel
import com.admin.ozieats_app.ui.home.fragments.myorders.OrderItemAdapter
import com.admin.ozieats_app.ui.home.item.NetworkConnectionListener
import com.admin.ozieats_app.utils.*
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_order_summary.*
import kotlinx.android.synthetic.main.activity_order_summary.constraintLayoutMain
import kotlinx.android.synthetic.main.activity_order_summary.noInternetLayout
import kotlinx.android.synthetic.main.activity_restaurant_details.*
import kotlinx.android.synthetic.main.no_internet_layout.view.*

class OrderSummaryActivity : BaseActivity(), NetworkConnectionListener,LocationCallback {

    private lateinit var mBinding: ActivityOrderSummaryBinding
    private lateinit var mOrderSummaryViewModel: OrderSummaryViewModel
    var isInternetConnectd = true
    var favourite: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_order_summary)
        mOrderSummaryViewModel = ViewModelProvider(
            this,
            OrderSummaryViewModel.OrderSummaryModelFactory(this)
        )
            .get(OrderSummaryViewModel::class.java)

        val toolbarViewModel = ViewModelProvider(
            this,
            ToolbarViewModel.ToolbarViewModelFactory(this)
        ).get(ToolbarViewModel::class.java)

        toolbarSetting(
            toolbarViewModel,
            ToolbarModel(getString(R.string.order_summary), false, true),
            this,
            mBinding.toolbar
        )

        val position = intent.getIntExtra("Position", 0)
        var isFav = intent.getIntExtra("IsFav", 0)

        mBinding.myOrderModel = getMyOrderPreference(this)

        val favouriteButtonVisibility =
            SharedPrefsManager.newInstance(this).getBoolean(Preference.ISFAVOURITE, false)

        if (favouriteButtonVisibility) {
            mBinding.favouriteIcon.gone()
        } else {
            if (mBinding.myOrderModel!!.status > 2) {
                mBinding.favouriteIcon.visible()
            } else {
                mBinding.favouriteIcon.gone()
            }

        }

        favourite = getMyOrderPreference(this).favourite

        if (favourite == 1) {
            mBinding.favouriteIcon.setBackgroundResource(R.drawable.heart_active)
        } else {
            mBinding.favouriteIcon.setBackgroundResource(R.drawable.grey_heart)
        }

        mBinding.restaurantTax.text = "$" + getMyOrderPreference(this).tax.toString()
        mBinding.totalPrice.text =
            "$" + (getMyOrderPreference(this).totalPrice - getMyOrderPreference(this).tax).toString()
        mBinding.totalSub.text = "$" + getMyOrderPreference(this).totalPrice.toString()

        Glide.with(this).asBitmap().load(getMyOrderPreference(this).restaurantImage)
            .into(mBinding.imageViewRestaurantImage)
        mBinding.orderSummaryListener = mOrderSummaryViewModel

        mBinding.favouriteIcon.setOnClickListener {

            if (isFav == 0) {
                isFav = 1
            } else {
                isFav = 0
            }


            mOrderSummaryViewModel.addToFavourite(
                getUserFromPreference(this).id,
                getMyOrderPreference(this).request_id
            ).observeForever {

                if (it.status == Result.Status.SUCCESS) {

                    val myOrders = getAllOrdersPreference(this)

                    val myOrderModel = MyOrdersModel()

                    myOrderModel.status = myOrders[position].status
                    myOrderModel.request_id = myOrders[position].request_id
                    myOrderModel.restaurant_name = myOrders[position].restaurant_name
                    myOrderModel.orderDateAndTime = myOrders[position].orderDateAndTime
                    myOrderModel.restaurantImage = myOrders[position].restaurantImage
                    myOrderModel.restaurant_address = myOrders[position].restaurant_address
                    myOrderModel.orderId = myOrders[position].orderId
                    myOrderModel.totalPrice = myOrders[position].totalPrice
                    myOrderModel.tax = myOrders[position].tax
                    myOrderModel.lat = myOrders[position].lat
                    myOrderModel.lng = myOrders[position].lng
                    myOrderModel.orderStatus = myOrders[position].orderStatus
                    myOrderModel.favourite = isFav

                    if (isFav == 0) {
                        mBinding.favouriteIcon.setBackgroundResource(R.drawable.grey_heart)

                    } else {
                        mBinding.favouriteIcon.setBackgroundResource(R.drawable.heart_active)
                    }
                    myOrderModel.orderItemModel = myOrders[position].orderItemModel

                    myOrders.set(position, myOrderModel)

                    SharedPrefsManager.newInstance(this)
                        .putString(Preference.ALLORDERS, Gson().toJson(myOrders))


                    /*if(mBinding.myOrderModel!=null)
                    {


                        SharedPrefsManager.newInstance(this).putString(Preference.MY_ORDER,Gson().toJson(myOrderModel))
                    }*/
                } else {
                    showAlert(this, it.message.toString())
                }

            }
        }

        fetchData()

    }

    fun fetchData() {
        val adapter = OrderItemAdapter(this, getMyOrderPreference(this).orderItemModel!!)
        myorderItemsRecycler.adapter = adapter
    }

    override fun changeNetwork(connected: Boolean) {
        isInternetConnectd = connected
        if (connected) {
            if (!constraintLayoutMain.isVisible()) {
                noInternetLayout.retryButton.setOnClickListener {

                    noInternetLayout.gone()
                    constraintLayoutMain.visible()
                    val intent = Intent(this, OrderSummaryActivity::class.java)
                    startActivity(intent)
                }
            }
        } else {
            noInternetLayout.visible()
            constraintLayoutMain.gone()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isInternetConnectd) {
            if (!constraintLayoutMain.isVisible()) {
                noInternetLayout.gone()
                constraintLayoutMain.visible()
            }
        }
    }

    override fun updateUi(location: Location, context: Context) {
        SharedPrefsManager.newInstance(context).putFloat(Preference.PREF_LAT, location.latitude.toFloat())
        SharedPrefsManager.newInstance(context).putFloat(Preference.PREF_LNG, location.longitude.toFloat())
    }
}