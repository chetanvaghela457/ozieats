package com.admin.ozieats_app.ui.auth.changePassword

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.admin.ozieats_app.R
import com.admin.ozieats_app.databinding.ActivityChangePasswordBinding
import com.admin.ozieats_app.ui.home.item.NetworkConnectionListener
import com.admin.ozieats_app.ui.profile.ChangePasswordModel
import com.admin.ozieats_app.utils.gone
import com.admin.ozieats_app.utils.isVisible
import com.admin.ozieats_app.utils.visible
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.android.synthetic.main.activity_change_password.constraintLayoutMain
import kotlinx.android.synthetic.main.activity_change_password.noInternetLayout
import kotlinx.android.synthetic.main.activity_restaurant_details.*
import kotlinx.android.synthetic.main.no_internet_layout.view.*

class ChangePasswordActivity : AppCompatActivity(), NetworkConnectionListener {

    lateinit var changePasswordViewModel: ChangePasswordViewModel
    var isInternetConnectd = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityChangePasswordBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_change_password)

        changePasswordViewModel = ViewModelProvider(
            this,
            ChangePasswordViewModel.ChangePasswordViewModelFactory(this)
        ).get(ChangePasswordViewModel::class.java)

        binding.changePassword = ChangePasswordModel()
        binding.changePasswordListener = changePasswordViewModel

        binding.mainBackButton.setOnClickListener {
            onBackPressed()
        }
    }

    override fun changeNetwork(connected: Boolean) {

        isInternetConnectd = connected
        if (connected) {
            if (!constraintLayoutMain.isVisible()) {
                noInternetLayout.retryButton.setOnClickListener {

                    noInternetLayout.gone()
                    constraintLayoutMain.visible()
                    var intent = Intent(this, ChangePasswordActivity::class.java)
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