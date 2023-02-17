package com.admin.ozieats_app.ui.auth.otp

import `in`.aabhasjindal.otptextview.OTPListener
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.admin.ozieats_app.R
import com.admin.ozieats_app.databinding.ActivityOtpEnterBinding
import com.admin.ozieats_app.model.User
import com.admin.ozieats_app.ui.auth.AuthViewModel
import com.admin.ozieats_app.ui.auth.ForgetPassword.ForgetEnterPasswordActivity
import com.admin.ozieats_app.ui.home.item.NetworkConnectionListener
import com.admin.ozieats_app.utils.*
import kotlinx.android.synthetic.main.activity_otp_enter.*
import kotlinx.android.synthetic.main.activity_otp_enter.constraintLayoutMain
import kotlinx.android.synthetic.main.activity_otp_enter.noInternetLayout
import kotlinx.android.synthetic.main.activity_restaurant_details.*
import kotlinx.android.synthetic.main.no_internet_layout.view.*


class OtpEnterActivity : AppCompatActivity(), NetworkConnectionListener {

    lateinit var binding: ActivityOtpEnterBinding
    lateinit var authViewModel: AuthViewModel
    var isInternetConnectd = true
    var enteredOTP: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_otp_enter)

        authViewModel =
            ViewModelProvider(this, AuthViewModel.AuthViewModelFactory(this)).get(
                AuthViewModel::class.java
            )

        binding.otpListener = authViewModel

        val user = User()
        user.phone =
            SharedPrefsManager.newInstance(this).getString(Preference.PHONE).toString()
        binding.userModel = user

        binding.mainBackButton.setOnClickListener {
            onBackPressed()
        }

        Log.e(
            "OTPCHECK",
            "onCreate: " + SharedPrefsManager.newInstance(this@OtpEnterActivity)
                .getInt(Preference.OTP, 0)
        )



        binding.otpView.otpListener = object : OTPListener {
            override fun onInteractionListener() {
                // fired when user types something in the Otpbox
            }

            override fun onOTPComplete(otp: String) {
                // fired when user has entered the OTP fully.
                //enteredOTP=otp

                binding.verifyButton.setOnClickListener {

                    val mainOtp = SharedPrefsManager.newInstance(this@OtpEnterActivity)
                        .getInt(Preference.OTP, 0)

                    if (mainOtp.toString() == otp) {
                        Log.e("dakjhgkajdh", "onCreate: " + otp)
                        val intent =
                            Intent(this@OtpEnterActivity, ForgetEnterPasswordActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        showAlert(this@OtpEnterActivity, "OTP does not match")
                    }

                }
//                Toast.makeText(this@OtpEnterActivity, "The OTP is $otp", Toast.LENGTH_SHORT).show()
            }
        }


    }

    override fun changeNetwork(connected: Boolean) {

        isInternetConnectd = connected
        if (connected) {
            if (!constraintLayoutMain.isVisible()) {
                noInternetLayout.retryButton.setOnClickListener {

                    noInternetLayout.gone()
                    constraintLayoutMain.visible()
                    val intent = Intent(this, OtpEnterActivity::class.java)
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