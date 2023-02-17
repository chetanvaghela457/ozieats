package com.admin.ozieats_app.ui.home.fragments.menu

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.admin.ozieats_app.R
import com.admin.ozieats_app.data.UserRepository
import com.admin.ozieats_app.model.ProfileImageModel
import com.admin.ozieats_app.ui.auth.changePassword.ChangePasswordActivity
import com.admin.ozieats_app.ui.history.OrderHistoryActivity
import com.admin.ozieats_app.ui.referFriend.ReferFriendActivity
import com.admin.ozieats_app.ui.sendFeedBack.SendFeedbackActivity
import com.admin.ozieats_app.ui.support.SupportActivity
import com.admin.ozieats_app.utils.*
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.image_select_dialog_layout.view.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class MenuViewModel(private var fragment: MenuFragment) : ViewModel() {

    var context= fragment.requireContext()
    var userRepository=UserRepository(context)
    companion object {
        const val PROFILE_IMAGE_CODE = 2
    }

    fun referAFriendButtonClick(view:View)
    {
        val intent=Intent(context,
            ReferFriendActivity::class.java)
        context.startActivity(intent)
    }

    fun historyButtonClick(view:View)
    {
        val intent=Intent(context, OrderHistoryActivity::class.java)
        context.startActivity(intent)
    }

    fun onChangePasswordButtonClick(view:View)
    {
        val intent=Intent(context,ChangePasswordActivity::class.java)
        context.startActivity(intent)
    }

    fun onSendFeedbackButtonClick(view: View)
    {
        val intent=Intent(context,
            SendFeedbackActivity::class.java)
        context.startActivity(intent)
    }

    fun onSupportButtonClick(view: View){
        val intent=Intent(context,
            SupportActivity::class.java)
        context.startActivity(intent)
    }

    fun onLogoutButtonClicked(view: View)
    {
        logOutAlert(context,"Are you sure want to logout?")
    }

    fun onImageSelectButtonClick(view: View) {
        uploadScreenShots()
    }

    @AfterPermissionGranted(Permissions.READ_STORAGE)
    private fun uploadScreenShots() {
        if (EasyPermissions.hasPermissions(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE)
            )
         {
            fragment.openDialog()
        } else {
            EasyPermissions.requestPermissions(
                fragment,
                "StoragePermission",
                Permissions.READ_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    @AfterPermissionGranted(Permissions.CAMERA)
    fun showNewNameDialog() {
        val dialogBuilder = AlertDialog.Builder(context)
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.image_select_dialog_layout, null)
        dialogBuilder.setView(dialogView)
        val b = dialogBuilder.create()

        dialogView.selectFromGallery.setOnClickListener {

            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            fragment.startActivityForResult(
                Intent.createChooser(
                    intent,
                    "Select Image from here..."
                ),
                Permissions.IMAGE_PICK_CODE
            )
          //  fragment.startActivityForResult(intent, Permissions.IMAGE_PICK_CODE)

            b.dismiss()
        }

        dialogView.selectFromCamera.setOnClickListener {

            if (EasyPermissions.hasPermissions(
                    context,
                    Manifest.permission.CAMERA
                ) && EasyPermissions.hasPermissions(
                    context,
                    Manifest.permission.CAMERA
                )
            ) {
                selectFromCamera()
            } else {
                EasyPermissions.requestPermissions(
                    fragment,
                    "StoragePermission",
                    Permissions.CAMERA,
                    Manifest.permission.CAMERA
                )
            }

            b.dismiss()
        }
        b.show()
    }

    fun selectFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        fragment.startActivityForResult(intent, Permissions.IMAGE_CAMERA_CODE)
    }

    fun changeProfileMethod(
        prodfile_image: String,
        userPhoto: CircleImageView,
        bitmap: Bitmap
    )
    {
        val loader = Loader(context, android.R.style.Theme_Translucent_NoTitleBar)
        loader.show()
        loader.setCancelable(false)
        loader.setCanceledOnTouchOutside(false)

        var profileImageModel=ProfileImageModel()
        profileImageModel.profile_image=prodfile_image
        profileImageModel.id= getUserFromPreference(context).id

        userRepository.changeProfileImage(profileImageModel).observeForever {


            Log.e("Image_URL", "changeProfileMethod: " + it.data )

            loader.cancel()
            if (it.status==Result.Status.SUCCESS)
            {

                Glide.with(context).asBitmap().load(bitmap).into(userPhoto)
                getUserFromPreference(context).profile_image

            }else
            {
                showAlert(context,it.message.toString())
            }


        }
    }



    class MenuModelFactory(private val fragment: MenuFragment) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MenuViewModel(fragment) as T
        }
    }
}