package com.admin.ozieats_app.ui.auth

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.admin.ozieats_app.R
import com.admin.ozieats_app.data.UserRepository
import com.admin.ozieats_app.model.User
import com.admin.ozieats_app.ui.auth.ForgetPassword.ForgetEnterPasswordActivity
import com.admin.ozieats_app.ui.auth.otp.OtpEnterActivity
import com.admin.ozieats_app.ui.location.MapActivity
import com.admin.ozieats_app.ui.location.UserLocationActivity
import com.admin.ozieats_app.utils.*
import com.google.gson.Gson
import libs.mjn.prettydialog.PrettyDialog
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class AuthViewModel(private var context: Context) : ViewModel() {

    private var userRepository: UserRepository = UserRepository(context)
    val progress = ObservableField<Boolean>()

    @AfterPermissionGranted(Permissions.LOCATION_PERMISSION)
    fun onLoginButtonClick(user: User) {

        if (user.phone.isEmpty()) {

            showAlert(context, "Please Enter valid phone")
        } else if (user.password.isEmpty() || !isValidPassword(user.password)) {
            showAlert(context, "Please Enter valid password")
        } else {
            user.deviceType = "android"
            user.login_type = 0
            user.device_token = getDeviceToken(context)

            val loader = Loader(context, android.R.style.Theme_Translucent_NoTitleBar)
            loader.show()
            loader.setCancelable(false)
            loader.setCanceledOnTouchOutside(false)
            userRepository.userLogin(user).observeForever {
                loader.cancel()
                if (it.status == Result.Status.SUCCESS) {

                    SharedPrefsManager.newInstance(context).putBoolean(Preference.IS_LOGGEDIN, true)

                    SharedPrefsManager.newInstance(context)
                        .putString(Preference.PREF_USER, Gson().toJson(it.data))
                    if (EasyPermissions.hasPermissions(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) && EasyPermissions.hasPermissions(
                            context,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    ) {
                        val intent = Intent(context, MapActivity::class.java)
                        context.startActivity(intent)
                        (context as Activity).finish()
                    } else {
                        val intent = Intent(context, UserLocationActivity::class.java)
                        context.startActivity(intent)
                        (context as Activity).finish()
                    }
                } else {
                    showAlert(context, it.message.toString())
                }
            }
        }
    }

    fun onRegisterButtonClick(user: User) {

        if (user.username.isEmpty()) {
            showAlert(context, "Please Enter valid username")

        } else if (user.email.isEmpty()) {

            showAlert(context, "Please Enter valid email")

        } else if (user.password.isEmpty()) {
            showAlert(context, "Please Enter valid password")
        } else if (user.phone.isEmpty()) {
            showAlert(context, "Please Enter valid phone")
        } else {
            user.deviceType = "android"
            user.login_type = 0
            user.device_token = getDeviceToken(context)
            user.profile_image = "http://www.freeiconspng.com/uploads/account-profile-icon-1.png"

            val loader = Loader(context, android.R.style.Theme_Translucent_NoTitleBar)
            loader.show()
            loader.setCancelable(false)
            loader.setCanceledOnTouchOutside(false)
            userRepository.userRegister(user).observeForever {
                loader.cancel()
                if (it.status == Result.Status.SUCCESS) {
                    val dialog = PrettyDialog(context)
                    dialog
//        .setTitle("Custom PrettyDialog")
                        .setTitleColor(R.color.colorPrimary)
                        .setMessage("Congratulations signup is successful. Please Login")
                        .setMessageColor(R.color.pdlg_color_black)
//        .setTypeface(Typeface.createFromAsset(context.resources.assets, "myfont.ttf"))
                        .setAnimationEnabled(true)
                        .setIcon(R.drawable.pdlg_icon_success, R.color.colorPrimary) {
                            dialog.dismiss()
                        }
                        .addButton(
                            "Okay",
                            R.color.pdlg_color_white,
                            R.color.colorPrimary
                        ) {
                            dialog.dismiss()
                            val intent = Intent(context, LoginActivity::class.java)
                            context.startActivity(intent)
                            (context as Activity).finish()
                        }

                    dialog.show()
                    dialog.setCancelable(false)
                } else {
                    showAlert(context, it.message.toString())
                }
            }
        }
    }

    fun onForgetSendOtpClick(user: User) {

        if (user.phone.isEmpty()) {
            showAlert(context, "Please enter valid phone no")
        } else {
            val loader = Loader(context, android.R.style.Theme_Translucent_NoTitleBar)
            loader.show()
            loader.setCancelable(false)
            loader.setCanceledOnTouchOutside(false)
            userRepository.forgetPassword(user).observeForever {

                loader.cancel()
                if (it.status == Result.Status.SUCCESS) {

                    val dialog = PrettyDialog(context)
                    dialog
//        .setTitle("Custom PrettyDialog")
                        .setTitleColor(R.color.colorPrimary)
                        .setMessage(it.data)
                        .setMessageColor(R.color.pdlg_color_black)
//        .setTypeface(Typeface.createFromAsset(context.resources.assets, "myfont.ttf"))
                        .setAnimationEnabled(true)
                        .setIcon(R.drawable.pdlg_icon_success, R.color.colorPrimary) {
                            dialog.dismiss()
                        }
                        .addButton(
                            "Okay",
                            R.color.pdlg_color_white,
                            R.color.colorPrimary
                        ) {
                            dialog.dismiss()

                            SharedPrefsManager.newInstance(context)
                                .putString(Preference.PHONE, user.phone)
                            val intent = Intent(context, OtpEnterActivity::class.java)
                            context.startActivity(intent)
                            (context as Activity).finish()
                        }

                    dialog.show()
                    dialog.setCancelable(false)

                } else {
                    showAlert(context, it.message.toString())
                }

            }
        }

    }

    fun resendButtonClick(user: User)
    {
        val loader = Loader(context, android.R.style.Theme_Translucent_NoTitleBar)
        loader.show()
        loader.setCancelable(false)
        loader.setCanceledOnTouchOutside(false)
        userRepository.forgetPassword(user).observeForever {

            loader.cancel()
            if (it.status == Result.Status.SUCCESS) {

                val dialog = PrettyDialog(context)
                dialog
//        .setTitle("Custom PrettyDialog")
                    .setTitleColor(R.color.colorPrimary)
                    .setMessage(it.data)
                    .setMessageColor(R.color.pdlg_color_black)
//        .setTypeface(Typeface.createFromAsset(context.resources.assets, "myfont.ttf"))
                    .setAnimationEnabled(true)
                    .setIcon(R.drawable.pdlg_icon_success, R.color.colorPrimary) {
                        dialog.dismiss()
                    }
                    .addButton(
                        "Okay",
                        R.color.pdlg_color_white,
                        R.color.colorPrimary
                    ) {
                        dialog.dismiss()
                    }

                dialog.show()
                dialog.setCancelable(false)

            } else {
                showAlert(context, it.message.toString())
            }

        }
    }

    fun onVerifyButtonClick(view: View) {
        val intent = Intent(context, ForgetEnterPasswordActivity::class.java)
        context.startActivity(intent)
        (context as Activity).finish()
    }

    fun onForgetPasswordClick(view: View) {

        val intent = Intent(context, ForgotPasswordActivity::class.java)
        context.startActivity(intent)
    }

    fun alreadyHaveAccountButton(view: View) {
        var intent = Intent(context, LoginActivity::class.java)
        context.startActivity(intent)
        (context as Activity).finish()
    }

    fun doNotHaveAccountButton(view: View) {
        var intent = Intent(context, RegisterActivity::class.java)
        context.startActivity(intent)
        (context as Activity).finish()
    }

    fun showHidePass(passwordLogin: EditText, butttonPasswordVisible: ImageView) {

        if (passwordLogin.transformationMethod == PasswordTransformationMethod.getInstance()) {
            butttonPasswordVisible.setImageResource(R.drawable.password_hide)

            //Show Password
            passwordLogin.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            butttonPasswordVisible.setImageResource(R.drawable.password_visible)

            //Hide Password
            passwordLogin.transformationMethod = PasswordTransformationMethod.getInstance()
        }
    }

    fun onForgetPasswordEnter(user: User) {
        if (user.password.isEmpty()) {
            showAlert(context, "Please Enter valid password")
        } else if (!user.password.equals(user.confirm_password)) {
            showAlert(context, "Password did not match")
        } else {

            user.phone =
                SharedPrefsManager.newInstance(context).getString(Preference.PHONE).toString()
            val loader = Loader(context, android.R.style.Theme_Translucent_NoTitleBar)
            loader.show()
            loader.setCancelable(false)
            loader.setCanceledOnTouchOutside(false)
            userRepository.updatePassword(user).observeForever {
                loader.cancel()
                if (it.status == Result.Status.SUCCESS) {
                    val dialog = PrettyDialog(context)
                    dialog
//        .setTitle("Custom PrettyDialog")
                        .setTitleColor(R.color.colorPrimary)
                        .setMessage(it.data)
                        .setMessageColor(R.color.pdlg_color_black)
//        .setTypeface(Typeface.createFromAsset(context.resources.assets, "myfont.ttf"))
                        .setAnimationEnabled(true)
                        .setIcon(R.drawable.pdlg_icon_success, R.color.colorPrimary) {
                            dialog.dismiss()
                        }
                        .addButton(
                            "Okay",
                            R.color.pdlg_color_white,
                            R.color.colorPrimary
                        ) {
                            dialog.dismiss()
                            val intent = Intent(context, LoginActivity::class.java)
                            context.startActivity(intent)
                            (context as Activity).finish()
                        }

                    dialog.show()
                    dialog.setCancelable(false)
                } else {
                    showAlert(context, it.message.toString())
                }

            }
        }

    }


    class AuthViewModelFactory(private val context: Context) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return AuthViewModel(context) as T
        }
    }
}