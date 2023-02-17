package com.admin.ozieats_app.ui.orderprogress

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.admin.ozieats_app.R
import com.admin.ozieats_app.databinding.ActivityOrderProgressBinding
import com.admin.ozieats_app.model.ToolbarModel
import com.admin.ozieats_app.model.ToolbarViewModel
import com.admin.ozieats_app.ui.home.item.NetworkConnectionListener
import com.admin.ozieats_app.utils.*
import kotlinx.android.synthetic.main.activity_order_progress.*
import kotlinx.android.synthetic.main.activity_order_progress.constraintLayoutMain
import kotlinx.android.synthetic.main.activity_order_progress.noInternetLayout
import kotlinx.android.synthetic.main.activity_restaurant_details.*
import kotlinx.android.synthetic.main.no_internet_layout.view.*

class OrderProgressActivity : BaseActivity(), NetworkConnectionListener, LocationCallback {

    private lateinit var binding: ActivityOrderProgressBinding
    private lateinit var orderProgressViewModel: OrderProgressViewModel
    var isInternetConnectd = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_order_progress)

        orderProgressViewModel =
            ViewModelProvider(this, OrderProgressViewModel.OrderProgressModelFactory(this)).get(
                OrderProgressViewModel::class.java
            )

        val toolbarViewModel = ViewModelProvider(
            this,
            ToolbarViewModel.ToolbarViewModelFactory(this)
        ).get(ToolbarViewModel::class.java)

        toolbarSetting(
            toolbarViewModel,
            ToolbarModel(getString(R.string.track_order), false, true),
            this,
            binding.orderProgressToolbar
        )

        binding.orderProgressListener = orderProgressViewModel

        val progress = getMyOrderPreference(this).status

        if (progress > 0) {
            val exploreFragment = PickupProgressFragment.newInstance()
            orderProgressViewModel.openFragment(exploreFragment)

        } else {
            val exploreFragment = PickUpOrderFragment.newInstance()
            orderProgressViewModel.openFragment(exploreFragment)
        }
        /*val proressLive=SharedPrefsManager.newInstance(this).getBoolean(Preference.ORDER_PROGRESS,false)

        if (proressLive)
        {
            val exploreFragment = PickupProgressFragment.newInstance()
            orderProgressViewModel.openFragment(exploreFragment)
        }else
        {
            val exploreFragment = PickUpOrderFragment.newInstance()
            orderProgressViewModel.openFragment(exploreFragment)
        }*/


    }

    /*override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }*/

    override fun changeNetwork(connected: Boolean) {
        isInternetConnectd = connected
        if (connected) {
            if (!constraintLayoutMain.isVisible()) {
                noInternetLayout.retryButton.setOnClickListener {

                    noInternetLayout.gone()
                    constraintLayoutMain.visible()
                    val intent = Intent(this, OrderProgressActivity::class.java)
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

    /*override fun updateUi(location: Location, context: Context) {

        Log.e("gsfdgfsgfgdsfg", "updateUi: "+location.latitude+"-------"+ location.longitude)
        SharedPrefsManager.newInstance(context).putFloat(Preference.PREF_LAT, location.latitude.toFloat())
        SharedPrefsManager.newInstance(context).putFloat(Preference.PREF_LNG, location.longitude.toFloat())
    }*/
}