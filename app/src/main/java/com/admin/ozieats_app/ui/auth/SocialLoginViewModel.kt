package com.admin.ozieats_app.ui.auth

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.admin.ozieats_app.data.UserRepository
import com.admin.ozieats_app.model.User
import com.admin.ozieats_app.ui.location.MapActivity
import com.admin.ozieats_app.ui.location.UserLocationActivity
import com.admin.ozieats_app.utils.*
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.gson.Gson
import org.json.JSONException
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


class SocialLoginViewModel(private var context: Context) : ViewModel() {

    var userRepository = UserRepository(context)
    val progress = ObservableField<Boolean>()

    fun onEmailLoginButtonClick(view: View) {
        val intent = Intent(context, LoginActivity::class.java)
        context.startActivity(intent)
    }

    class SocialLoginModelFactory(private val context: Context) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SocialLoginViewModel(context) as T
        }
    }

    @AfterPermissionGranted(Permissions.LOCATION_PERMISSION)
    fun getUserProfile(currentAccessToken: AccessToken) {
        val request = GraphRequest.newMeRequest(
            currentAccessToken
        ) { `object`, response ->
            try {
                println("ksflfks" + `object`)
                val first_name = `object`.getString("first_name")
                val last_name = `object`.getString("last_name")
                val email = `object`.getString("email")
                val id = `object`.getString("id")
                val image_url = "https://graph.facebook.com/$id/picture?type=normal"

                val user = User()

                user.username = first_name
                user.email = email
                user.login_type = 2
                user.deviceType = "android"
                user.device_token = getDeviceToken(context)
                user.profile_image = image_url
                user.phone="000000000"

                val loader = Loader(context, android.R.style.Theme_Translucent_NoTitleBar)
                loader.show()
                loader.setCancelable(false)
                loader.setCanceledOnTouchOutside(false)
                val loginResponse = userRepository.userRegister(user)

                loginResponse.observeForever {
                   loader.cancel()
                    if (it.status == Result.Status.SUCCESS) {
                        SharedPrefsManager.newInstance(context)
                            .putBoolean(Preference.IS_LOGGEDIN, true)
                        SharedPrefsManager.newInstance(context).putString(Preference.PREF_USER, Gson().toJson(it.data))
                        println("fsdkfyjsghskj" + it.status)
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
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        val parameters = Bundle()
        parameters.putString("fields", "first_name,last_name,email,id")
        request.parameters = parameters
        request.executeAsync()
    }

    @AfterPermissionGranted(Permissions.LOCATION_PERMISSION)
    fun onLoggedIn(result: GoogleSignInAccount?) {

        try {
            Log.e("Check_error_msg", "onLoggedIn: Check_error_msg"  )

            val acct: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(context)

            val personName = result?.displayName
            val personPhotoUrl = result?.photoUrl.toString()
            val email = result?.email

            println("NameUser: $personName, email: $email, Image: $personPhotoUrl")

            val user = User()

            if (personName != null) {
                user.username = personName
            }
            if (email != null) {
                user.email = email
            }
            user.login_type = 1
            user.device_token= getDeviceToken(context)
            user.deviceType="android"
            user.phone="00000000"
            user.profile_image=personPhotoUrl


            val loader = Loader(context, android.R.style.Theme_Translucent_NoTitleBar)
            loader.show()
            loader.setCancelable(false)
            loader.setCanceledOnTouchOutside(false)
            userRepository.userRegister(user).observeForever {
                loader.cancel()
                println("flsksl" + it.status)
                if (it.status == Result.Status.SUCCESS) {
                    SharedPrefsManager.newInstance(context)
                        .putBoolean(Preference.IS_LOGGEDIN, true)

                    SharedPrefsManager.newInstance(context).putString(Preference.PREF_USER, Gson().toJson(it.data))
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
        catch (e : ApiException)
        {
            Log.e("Check_error_msg", "onLoggedIn: Check_error_msg =======>>>>>> " + e.statusCode + " ==> " + e.message )
        }

       /* if (result.isSuccess)
        {

//            val acct: GoogleSignInAccount? = result.signInAccount


//            updateUI(true)
        } else {
            // Signed out, show unauthenticated UI.
//            updateUI(false)

        }*/
    }
}