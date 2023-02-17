package com.admin.ozieats_app.ui.home.fragments.menu

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.admin.ozieats_app.R
import com.admin.ozieats_app.databinding.FragmentMenuBinding
import com.admin.ozieats_app.utils.Permissions
import com.admin.ozieats_app.utils.getUserFromPreference
import com.admin.ozieats_app.utils.gone
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.ByteArrayOutputStream


class MenuFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    lateinit var binding: FragmentMenuBinding
    lateinit var menuViewModel: MenuViewModel
    private lateinit var bottomSheet: BottomSheetBehavior<NestedScrollView>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_menu,
                container,
                false
            )

        menuViewModel = ViewModelProvider(
            this,
            MenuViewModel.MenuModelFactory(
                this
            )
        ).get(MenuViewModel::class.java)

        binding.menuListener = menuViewModel
        binding.userModel = getUserFromPreference(requireContext())
        binding.userName.text = getUserFromPreference(requireContext()).username


        Log.e("profile_image_url", "onCreateView: " + getUserFromPreference(requireContext()).profile_image )

        Glide.with(requireContext()).asBitmap()
            .load(getUserFromPreference(requireContext()).profile_image).into(binding.userPhoto)

        if (getUserFromPreference(requireContext()).login_type!=0)
        {
            binding.phoneRelative.gone()
            binding.changePasswordRelative.gone()
        }

        bottomSheet = BottomSheetBehavior.from(binding.scrollViewSelectImage)
        bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN

        binding.selectCancel.setOnClickListener {

            bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.selectFromCamera.setOnClickListener {

            bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN

            if (EasyPermissions.hasPermissions(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) && EasyPermissions.hasPermissions(
                    requireContext(),
                    Manifest.permission.CAMERA
                )
            ) {
                menuViewModel.selectFromCamera()
            } else {
                EasyPermissions.requestPermissions(
                    this,
                    "StoragePermission",
                    Permissions.CAMERA,
                    Manifest.permission.CAMERA
                )
            }

        }

        binding.selectFromGallery.setOnClickListener {

            bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN

            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(
                    intent,
                    "Select Image from here..."
                ),
                Permissions.IMAGE_PICK_CODE
            )
        }

        return binding.root
    }

    companion object {
        fun newInstance(): MenuFragment = MenuFragment()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

        if (requestCode == Permissions.IMAGE_PICK_CODE) {
            menuViewModel.showNewNameDialog()
        } else if (requestCode == Permissions.IMAGE_CAMERA_CODE) {
            menuViewModel.selectFromCamera()
        }

    }

    fun openDialog()
    {
        bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

      //  Log.e("CheckRequestCode", "onPermissionsGranted: $requestCode")

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        Log.e("CheckRequestCode", "onPermissionsGranted: $requestCode")


        if (requestCode == Permissions.IMAGE_CAMERA_CODE && resultCode == Activity.RESULT_OK) {

            if (data != null && data.extras != null) {
                val photo: Bitmap? = data.extras?.get("data") as Bitmap?

                val byteArrayOutputStream = ByteArrayOutputStream()
                if (photo != null) {
                    photo.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                }
                val byteArray: ByteArray = byteArrayOutputStream.toByteArray()

                val encoded: String = Base64.encodeToString(byteArray, Base64.DEFAULT)

                Log.e("kjutdl", "onActivityResult: "+encoded )

                if (photo != null) {
                    menuViewModel.changeProfileMethod(encoded,binding.userPhoto,photo)
                }

                binding.userPhoto.setImageBitmap(photo)


            }
        }
        if (requestCode == Permissions.IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {

            Log.e("Check_URI", "onActivityResult: "+data!!.data )

//            val selectedImageUri = data.data

           // val photo: Bitmap? = data.extras?.get("data") as Bitmap?

            val imageUri: Uri = data.data!!
            val bitmap =
                MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageUri)

            try {
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
                val image = stream.toByteArray()
                val img_str = Base64.encodeToString(image, 0)

                Log.e("kjutdl", "onActivityResult: "+img_str )

                binding.userPhoto.setImageBitmap(bitmap)

                menuViewModel.changeProfileMethod(img_str,binding.userPhoto,bitmap)

            }catch (e:Exception)
            {
                Log.e("kjutdl", "onActivityResult: "+e.message )
            }
        }
    }


}