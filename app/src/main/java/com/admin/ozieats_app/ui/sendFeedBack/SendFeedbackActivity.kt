package com.admin.ozieats_app.ui.sendFeedBack

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.admin.ozieats_app.R
import com.admin.ozieats_app.databinding.ActivitySendFeedbackBinding
import com.admin.ozieats_app.model.ToolbarModel
import com.admin.ozieats_app.model.ToolbarViewModel
import com.admin.ozieats_app.ui.home.adapter.FeedbackPhotoAdapter
import com.admin.ozieats_app.utils.Permissions
import com.admin.ozieats_app.utils.inVisible
import com.admin.ozieats_app.utils.toolbarSetting
import kotlinx.android.synthetic.main.activity_send_feedback.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.*

class SendFeedbackActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private lateinit var mActivity: Activity
    private lateinit var mScreenShotsList: ArrayList<Uri>

    companion object {
        const val GALLERY_IMAGE_CODE = 2
    }

    lateinit var binding: ActivitySendFeedbackBinding
    lateinit var feedbackViewModel: FeedbackViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_send_feedback)

        val toolbarViewModel = ViewModelProvider(
            this,
            ToolbarViewModel.ToolbarViewModelFactory(this)
        ).get(ToolbarViewModel::class.java)
        toolbarSetting(
            toolbarViewModel,
            ToolbarModel(getString(R.string.send_feedback), false, true),
            this,
            binding.feedbackToolbar
        )

        feedbackViewModel =
            ViewModelProvider(
                this,
                FeedbackViewModel.FeedbackModelFactory(this)
            ).get(
                FeedbackViewModel::class.java
            )

        binding.feedBackListener = feedbackViewModel

        mActivity = this
        initViews()
        initClickListener()
    }

    private fun initViews() {
        txtToolbarTitle.text = getString(R.string.send_feedback)
        imgShoppingCart.inVisible() //Hide Cart Icon
        rv_scList.layoutManager =
            LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false)
        mScreenShotsList = ArrayList()
    }

    private fun initClickListener() {

        //Upload ScreenShot
        ll_uploadScreenShot.setOnClickListener {
            uploadScreenShots()
        }

        //Submit button
        tv_submit.setOnClickListener {
            //Todo Send Feedback
        }

        //Dismiss Button
        tv_dismiss.setOnClickListener {
            onBackPressed()
        }
    }

    @AfterPermissionGranted(Permissions.READ_STORAGE)
    private fun uploadScreenShots() {
        if (EasyPermissions.hasPermissions(
                mActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            takeImageFromGallery()
        } else {
            EasyPermissions.requestPermissions(
                mActivity,
                "StoragePermission",
                Permissions.READ_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    private fun takeImageFromGallery() {
        // Defining Implicit Intent to mobile gallery
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(
                intent,
                "Select Image from here..."
            ),
            GALLERY_IMAGE_CODE
        )
    }

    private fun setAdapter() {
        val adapter = FeedbackPhotoAdapter(mActivity, mScreenShotsList, onPhotoRemove = {
            removeScreenShot(it)
        })
        rv_scList.adapter = adapter
    }

    private fun removeScreenShot(position: Int) {
        mScreenShotsList.removeAt(position)
        setAdapter()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_IMAGE_CODE && resultCode == RESULT_OK) {
            if (data!!.data != null) {
                mScreenShotsList.add(data.data!!)
                setAdapter()
                //  Glide.with(mActivity).load(imageUri).into(iv_selectedImage)
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        takeImageFromGallery()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)

    }
}