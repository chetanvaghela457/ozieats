package com.admin.ozieats_app.ui.location

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.admin.ozieats_app.R
import com.admin.ozieats_app.databinding.ActivityUserLocationBinding
import com.admin.ozieats_app.ui.home.item.NetworkConnectionListener
import com.admin.ozieats_app.utils.BaseActivity
import com.admin.ozieats_app.utils.gone
import com.admin.ozieats_app.utils.isVisible
import com.admin.ozieats_app.utils.visible
import kotlinx.android.synthetic.main.activity_restaurant_details.*
import kotlinx.android.synthetic.main.activity_user_location.*
import kotlinx.android.synthetic.main.activity_user_location.constraintLayoutMain
import kotlinx.android.synthetic.main.activity_user_location.noInternetLayout
import kotlinx.android.synthetic.main.no_internet_layout.view.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class UserLocationActivity : BaseActivity(), NetworkConnectionListener,
    EasyPermissions.PermissionCallbacks {

    lateinit var binding: ActivityUserLocationBinding
    lateinit var locationViewModel: LocationViewModel
    var isInternetConnectd = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_user_location)

        locationViewModel =
            ViewModelProvider(this, LocationViewModel.LocationModelFactory(this, this)).get(
                LocationViewModel::class.java
            )

        binding.locationListener = locationViewModel
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

        val intent = Intent(this, MapActivity::class.java)
        //val intent=Intent(this,HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)

    }

    override fun changeNetwork(connected: Boolean) {

        isInternetConnectd = connected
        if (connected) {
            if (!constraintLayoutMain.isVisible()) {
                noInternetLayout.retryButton.setOnClickListener {

                    noInternetLayout.gone()
                    constraintLayoutMain.visible()
                    val intent = Intent(this, UserLocationActivity::class.java)
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
}