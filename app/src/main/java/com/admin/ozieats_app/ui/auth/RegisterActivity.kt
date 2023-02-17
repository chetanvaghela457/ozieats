package com.admin.ozieats_app.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.admin.ozieats_app.R
import com.admin.ozieats_app.databinding.ActivityRegisterBinding
import com.admin.ozieats_app.model.User
import com.admin.ozieats_app.ui.home.item.NetworkConnectionListener
import com.admin.ozieats_app.utils.gone
import com.admin.ozieats_app.utils.isVisible
import com.admin.ozieats_app.utils.visible
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.constraintLayoutMain
import kotlinx.android.synthetic.main.activity_register.noInternetLayout
import kotlinx.android.synthetic.main.activity_restaurant_details.*
import kotlinx.android.synthetic.main.no_internet_layout.view.*

class RegisterActivity : AppCompatActivity(), NetworkConnectionListener {

    lateinit var binding: ActivityRegisterBinding
    lateinit var authViewModel: AuthViewModel
    var isInternetConnectd = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_register)

        authViewModel =
            ViewModelProvider(this, AuthViewModel.AuthViewModelFactory(this)).get(
                AuthViewModel::class.java
            )

        binding.registerListener = authViewModel
        binding.userModel = User()

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
                    val intent = Intent(this, RegisterActivity::class.java)
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