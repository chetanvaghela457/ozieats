package com.admin.ozieats_app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.admin.ozieats_app.R
import com.admin.ozieats_app.databinding.ActivitySocialLoginBinding
import com.admin.ozieats_app.ui.home.item.NetworkConnectionListener
import com.admin.ozieats_app.utils.gone
import com.admin.ozieats_app.utils.isVisible
import com.admin.ozieats_app.utils.visible
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_restaurant_details.*
import kotlinx.android.synthetic.main.activity_social_login.*
import kotlinx.android.synthetic.main.activity_social_login.constraintLayoutMain
import kotlinx.android.synthetic.main.activity_social_login.noInternetLayout
import kotlinx.android.synthetic.main.no_internet_layout.view.*


class SocialLoginActivity : AppCompatActivity(), NetworkConnectionListener,
    GoogleApiClient.OnConnectionFailedListener {

    lateinit var binding: ActivitySocialLoginBinding
    lateinit var socialLoginViewModel: SocialLoginViewModel
    lateinit var callbackManager: CallbackManager
    lateinit var mGoogleSignInClient: GoogleSignInClient
    private var googleApiClient: GoogleApiClient? = null
    var isInternetConnectd = true

    companion object {
        private const val RC_SIGN_IN = 1007
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_social_login)

        socialLoginViewModel =
            ViewModelProvider(this, SocialLoginViewModel.SocialLoginModelFactory(this)).get(
                SocialLoginViewModel::class.java
            )

        binding.socialLoginListener = socialLoginViewModel

        callbackManager = CallbackManager.Factory.create()

        FacebookSdk.sdkInitialize(this.applicationContext)

        /* val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
             .requestIdToken("1077662513709-3iog8n3thipcfvvm1e0f59fervgj2103.apps.googleusercontent.com")
             .requestEmail()
             .build()

         googleApiClient = GoogleApiClient.Builder(this)
             .enableAutoManage(this, this)
             .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
             .build()*/


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            // .requestIdToken("809513160300-4fvb3t2gjab375p0163l8epedpo08voh.apps.googleusercontent.com")
            .requestIdToken("1077662513709-qg2bemp19gfnskaitna9sf0jakrm4t9r.apps.googleusercontent.com")
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.textViewFacebookSignIn.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(
                this,
                listOf("email", "public_profile")
            )
        }

        binding.textViewGoogleSignIn.setOnClickListener {

            /*val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
            startActivityForResult(signInIntent, RC_SIGN_IN)*/

            val signInIntent: Intent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)

        }

        LoginManager.getInstance().registerCallback(callbackManager, object :
            FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {
                // App code
                val accessToken = AccessToken.getCurrentAccessToken()

                val isLoggedIn = accessToken != null && !accessToken.isExpired
                println("kdkfk" + isLoggedIn)
                if (isLoggedIn) {
                    socialLoginViewModel.getUserProfile(AccessToken.getCurrentAccessToken())
                }
            }

            override fun onCancel() {
                // App code
            }

            override fun onError(exception: FacebookException) {
                // App code
            }
        })
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SIGN_IN) {

            try {
                val result = GoogleSignIn.getSignedInAccountFromIntent(data!!)
                if (result != null) {
                    Log.e("Check_error_msg", "onActivityResult: result not null " )
                    socialLoginViewModel.onLoggedIn(result)
                }
                else{
                    Log.e("Check_error_msg", "onActivityResult: result null " )
                }
            } catch (e: ApiException) {
                // The ApiException status code indicates the detailed failure reason.

                Log.e("Check_error_msg", "onActivityResult: " + e.message + "==> " + e.statusCode)
            }

        } else {
            Log.e("Check_error_msg", "onActivityResult: else "  )
            callbackManager.onActivityResult(requestCode, resultCode, data)
            super.onActivityResult(requestCode, resultCode, data)
        }

    }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            try {

                /* val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
                 handleSignInResult(result!!)*/

                val task =
                    GoogleSignIn.getSignedInAccountFromIntent(data)
                handleSignInResult(task)

            } catch (e: ApiException) {
                Log.e("GOOGLE_RESULT", "onActivityResult: " + e.message)
                // The ApiException status code indicates the detailed failure reason.
            }

        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data)

        }

    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account =
                completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.

            socialLoginViewModel.onLoggedIn(account)
//            updateUI(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e("GOOGLE_RESULT", "onActivityResult  : statusCode is " + e.statusCode)
            updateUI(null)
        }
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        if (account != null) {
            Log.e("GOOGLE_RESULT", "User Email is " + account.email)
        } else {
            Log.e("GOOGLE_RESULT", "User Email is null")
        }
    }

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this)
    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    override fun changeNetwork(connected: Boolean) {

        isInternetConnectd = connected
        if (connected) {
            if (!constraintLayoutMain.isVisible()) {
                noInternetLayout.retryButton.setOnClickListener {

                    noInternetLayout.gone()
                    constraintLayoutMain.visible()
                    val intent = Intent(this, SocialLoginActivity::class.java)
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