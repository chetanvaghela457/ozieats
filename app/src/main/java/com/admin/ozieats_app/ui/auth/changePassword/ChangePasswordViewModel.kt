package com.admin.ozieats_app.ui.auth.changePassword

import android.content.Context
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.admin.ozieats_app.R
import com.admin.ozieats_app.data.UserRepository
import com.admin.ozieats_app.ui.profile.ChangePasswordModel
import com.admin.ozieats_app.utils.*

class ChangePasswordViewModel(private var context: Context) : ViewModel() {

    val errorOldPassword = ObservableField<String>()
    val errorNewPassword = ObservableField<String>()
    val errorConfirmPassword = ObservableField<String>()

    val progress = ObservableField<Boolean>()
    private val userRepository = UserRepository(context)


    fun onSubmitButtonClick(changePasswordModel: ChangePasswordModel) {
        if (changePasswordModel.oldPassword.isEmpty() || changePasswordModel.oldPassword.length < 8) {
            errorOldPassword.set("Please Enter Valid Password")
            Toast.makeText(context,"Please Enter Valid Password",Toast.LENGTH_LONG).show()
        } else if (changePasswordModel.newPassword.isEmpty() || changePasswordModel.newPassword.length < 8) {
            errorNewPassword.set("Please Enter Valid NewPassword")
            Toast.makeText(context,"Please Enter Valid NewPassword",Toast.LENGTH_LONG).show()
        } else if (changePasswordModel.confirmPassword.isEmpty() || changePasswordModel.confirmPassword.length < 8 || !changePasswordModel.confirmPassword.equals(
                changePasswordModel.newPassword
            )
        ) {
            errorConfirmPassword.set("Please Enter Valid NewPassword")
            Toast.makeText(context,"Please Enter Valid NewPassword",Toast.LENGTH_LONG).show()
        } else {
            val loader = Loader(context, android.R.style.Theme_Translucent_NoTitleBar)
        loader.show()
        loader.setCancelable(false)
        loader.setCanceledOnTouchOutside(false)
            changePasswordModel.userId = getUserFromPreference(context).id.toString()
            userRepository.changePassword(changePasswordModel).observeForever {
                loader.cancel()
                if (it.status == Result.Status.SUCCESS) {
                    logout(context)
                } else {
                    showAlert(context, it.message.toString())
                }
            }
        }
    }

    class ChangePasswordViewModelFactory(private val context: Context) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ChangePasswordViewModel(context) as T
        }
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
}