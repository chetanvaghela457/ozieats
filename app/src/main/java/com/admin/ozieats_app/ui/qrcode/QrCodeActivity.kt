package com.admin.ozieats_app.ui.qrcode

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.admin.ozieats_app.R
import com.admin.ozieats_app.data.OrderRepository
import com.admin.ozieats_app.ui.home.HomeActivity
import com.admin.ozieats_app.utils.*
import com.google.zxing.Result
import kotlinx.android.synthetic.main.activity_qr_code.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class QrCodeActivity : AppCompatActivity(), ZXingScannerView.ResultHandler,
    EasyPermissions.PermissionCallbacks ,LocationCallback{

    private var mScannerView: ZXingScannerView? = null
    private var flashState: Boolean = false
    var orderRepository = OrderRepository(this)
    var request_id = 0
    var orderTime=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code)

        Log.e("dsgdfrgdfgdf", "onCreate: ")
        checkCameraPermission()
        initView()
    }

    fun initView() {
        mScannerView = ZXingScannerView(this)
        frmContent.addView(mScannerView)

        request_id = SharedPrefsManager.newInstance(this).getInt(Preference.ORDER_REQUEST_ID, 0)

        btnBack.setOnClickListener {
            onBackPressed()
        }

        btnLight.setOnClickListener {
            if (flashState) {
                btnLight.setBackgroundResource(R.drawable.flash_active)
//                showMessage(R.string.flashlight_turned_off)
                mScannerView?.flash = false
                flashState = false
            } else {
                btnLight.setBackgroundResource(R.drawable.flash_inactive)
//                showMessage(R.string.flashlight_turned_on)
                mScannerView?.flash = true
                flashState = true
            }
        }
    }

    fun checkCameraPermission() {

        EasyPermissions.requestPermissions(
            this,
            "Camera Permission",
            Permissions.CAMERA,
            Manifest.permission.CAMERA
        )
    }

    override fun onResume() {
        super.onResume()
        mScannerView?.setResultHandler(this)
        mScannerView?.startCamera()
    }

    override fun onPause() {
        super.onPause()
        mScannerView?.stopCamera()
    }

    override fun handleResult(result: Result?) {

        if (result != null) {

            val timeArray= getOrderTimePreference(this)
            for (time in timeArray)
            {
                if (request_id==time.request_id)
                {
                    orderTime=time.time

                    Log.e("slhgsfjhk", "handleResult: "+getUserFromPreference(this).id.toString()+getDeviceToken(this) )
                    if (result.toString() == getUserFromPreference(this).id.toString()+getDeviceToken(this)+orderTime) {

                        val loader = Loader(this, android.R.style.Theme_Translucent_NoTitleBar)
                        loader.show()
                        loader.setCancelable(false)
                        loader.setCanceledOnTouchOutside(false)
                        orderRepository.pushNotifications(
                            getUserFromPreference(this).id,
                            request_id,
                            3,
                            "Order no. "+getMyOrderPreference(this).orderId+" is Delivered Successfully."
                        ).observeForever {
                            loader.cancel()

                            timeArray.remove(time)
                            orderPickUpSuccessfully(this)
                        }
                    } else {
                        orderDontMatch(this)
                    }
                }
            }

        }


    }

    private fun orderPickUpSuccessfully(mContext: Context) {

        val dialog = Dialog(mContext)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_order_successfully_pickup_layout)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val ivClose = dialog.findViewById<ImageView>(R.id.ic_close_dialog)
        val order_no=dialog.findViewById<TextView>(R.id.tv_order_number)

        order_no.text= getMyOrderPreference(this).orderId

        ivClose.setOnClickListener {
            dialog.dismiss()

            var intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        val window = dialog.window
        window!!.setGravity(Gravity.CENTER)
        window.setLayout(
            (0.9 * DisplayMetricsHandler.getScreenWidth()).toInt(),
            Toolbar.LayoutParams.WRAP_CONTENT
        )

        if (!dialog.isShowing) dialog.show()

    }

    private fun orderDontMatch(mContext: Context) {

        val dialog = Dialog(mContext)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_order_dont_match_layout)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val ivClose = dialog.findViewById<ImageView>(R.id.ic_close_dialog)

        ivClose.setOnClickListener {
            dialog.dismiss()
        }

        val window = dialog.window
        window!!.setGravity(Gravity.CENTER)
        window.setLayout(
            (0.9 * DisplayMetricsHandler.getScreenWidth()).toInt(),
            Toolbar.LayoutParams.WRAP_CONTENT
        )

        if (!dialog.isShowing) dialog.show()

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        initView()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)

    }

    override fun updateUi(location: Location, context: Context) {
        SharedPrefsManager.newInstance(context).putFloat(Preference.PREF_LAT, location.latitude.toFloat())
        SharedPrefsManager.newInstance(context).putFloat(Preference.PREF_LNG, location.longitude.toFloat())
    }
}