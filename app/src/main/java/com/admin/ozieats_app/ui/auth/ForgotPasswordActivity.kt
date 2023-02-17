package com.admin.ozieats_app.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.admin.ozieats_app.R
import com.admin.ozieats_app.databinding.ActivityForgotPasswordBinding
import com.admin.ozieats_app.model.User
import com.admin.ozieats_app.ui.home.item.NetworkConnectionListener
import com.admin.ozieats_app.utils.gone
import com.admin.ozieats_app.utils.isVisible
import com.admin.ozieats_app.utils.visible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_forgot_password.constraintLayoutMain
import kotlinx.android.synthetic.main.activity_forgot_password.noInternetLayout
import kotlinx.android.synthetic.main.activity_restaurant_details.*
import kotlinx.android.synthetic.main.no_internet_layout.view.*

class ForgotPasswordActivity : AppCompatActivity(), NetworkConnectionListener {

    lateinit var binding: ActivityForgotPasswordBinding
    lateinit var authViewModel: AuthViewModel
    var isInternetConnectd = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_forgot_password)

        authViewModel =
            ViewModelProvider(this, AuthViewModel.AuthViewModelFactory(this)).get(
                AuthViewModel::class.java
            )

        binding.forgotPasswordListener = authViewModel
        binding.userModel = User()

        binding.mainBackButton.setOnClickListener {
            onBackPressed()
        }

        val bottomSheet = BottomSheetBehavior.from(binding.scrollViewLogin)
        bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun changeNetwork(connected: Boolean) {

        isInternetConnectd = connected
        if (connected) {
            if (!constraintLayoutMain.isVisible()) {
                noInternetLayout.retryButton.setOnClickListener {

                    noInternetLayout.gone()
                    constraintLayoutMain.visible()
                    val intent = Intent(this, ForgotPasswordActivity::class.java)
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