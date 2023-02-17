package com.admin.ozieats_app.ui.splash

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import com.admin.ozieats_app.R
import com.admin.ozieats_app.ui.auth.SocialLoginActivity
import com.admin.ozieats_app.ui.home.HomeActivity
import com.admin.ozieats_app.ui.location.UserLocationActivity
import com.admin.ozieats_app.utils.Permissions
import com.admin.ozieats_app.utils.Preference
import com.admin.ozieats_app.utils.SharedPrefsManager
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_splash.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels

        Glide.with(this).asBitmap().load(R.drawable.logo_ozieats).override((width*50)/100,(height*25)/100).into(logo);

        onButtonClick()
    }


    @AfterPermissionGranted(Permissions.LOCATION_PERMISSION)
    fun onButtonClick() {
        if (SharedPrefsManager.newInstance(this).getBoolean(Preference.IS_LOGGEDIN, false)) {
            Handler().postDelayed({
                if (EasyPermissions.hasPermissions(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) && EasyPermissions.hasPermissions(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                ) {
                    SharedPrefsManager.newInstance(this).putBoolean(Preference.HOME_DATA, true)
                    val i = Intent(this@SplashActivity, HomeActivity::class.java)
                    startActivity(i)
                    finish()

                } else {
                    val intent = Intent(this, UserLocationActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            }, 1000)


        } else {
            getStarted.setOnClickListener {

                val intent = Intent(this, SocialLoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }


    }
}