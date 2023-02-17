package com.admin.ozieats_app.data

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.admin.ozieats_app.api.ApiService
import com.admin.ozieats_app.model.ProfileImageModel
import com.admin.ozieats_app.model.User
import com.admin.ozieats_app.ui.profile.ChangePasswordModel
import com.admin.ozieats_app.utils.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class UserRepository(val context: Context) {

    private val sharedPrefsManager = SharedPrefsManager.newInstance(context)
    fun userLogin(user: User): MutableLiveData<Result<User>> {
        val registerResponse = MutableLiveData<Result<User>>()
        val myCompositeDisposable = CompositeDisposable()
        myCompositeDisposable.add(
            ApiService.invoke(context).loginUser(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val status = it.asJsonObject.get(Key.STATUS)
//                        if (status.asInt == Status.SUCCESS.code) {
                    val token = it.asJsonObject.get(Key.TOKEN)
                    val data = it.asJsonObject.getAsJsonObject(Key.DATA)

                    if (data != null) {
                        val supplierType = object : TypeToken<User>() {}.type
                        val supplierData: User =
                            Gson().fromJson(data, supplierType)
                        println(":rkrtrtjr" + data)

                        /*if (token != null) {
                            sharedPrefsManager.putString(Preference.PREF_TOKEN, token.asString)
                        }*/
                        registerResponse.postValue(Result.success(supplierData))


                    } else {
                        registerResponse.postValue(Result.error(it.asJsonObject.get(Key.MESSAGE).asString))
                    }
                }, {
                    registerResponse.postValue(Result.error(it.message.toString()))
                })
        )
        return registerResponse
    }

    fun userRegister(user: User): MutableLiveData<Result<User>> {

        println("jhfkjdhfdkj" + user.username + "--" + user.email + "---" + user.password + "--" + user.login_type + "--" + user.deviceType + "--" + user.device_token + "---" + user.phone)
        val registerResponse = MutableLiveData<Result<User>>()
        val myCompositeDisposable = CompositeDisposable()
        myCompositeDisposable.add(
            ApiService.invoke(context).registerUser(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val status = it.asJsonObject.get(Key.STATUS)
                    val data = it.asJsonObject.getAsJsonObject(Key.DATA)

                    if (data != null) {
                        val supplierType = object : TypeToken<User>() {}.type
                        val supplierData: User =
                            Gson().fromJson(data, supplierType)
                        registerResponse.postValue(Result.success(supplierData))
                    } else {
                        registerResponse.postValue(Result.error(it.asJsonObject.get(Key.MESSAGE).asString))
                    }
                    println("fkjdfhjsdhf" + status + "-----" + data)
                }, {
                    registerResponse.postValue(Result.error(it.message.toString()))
                })
        )
        return registerResponse
    }

    fun changePassword(changePasswordModel: ChangePasswordModel): MutableLiveData<Result<String>> {
        val changePasswordResponse = MutableLiveData<Result<String>>()
        val myCompositeDisposable = CompositeDisposable()
        myCompositeDisposable.add(
            ApiService.invoke(context).changePassword(changePasswordModel)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val status = it.asJsonObject.get(Key.STATUS)
                    if (status != null) {
                        if (status.asInt == Status.SUCCESS.code) {
                            changePasswordResponse.postValue(Result.success("Success"))
                        } else {
                            changePasswordResponse.postValue(Result.error(it.asJsonObject.get(Key.MESSAGE).asString))
                        }
                    }
                }, {
                    changePasswordResponse.postValue(Result.error(it.message.toString()))
                })
        )
        return changePasswordResponse
    }

    fun changeProfileImage(profileImageModel: ProfileImageModel): MutableLiveData<Result<String>> {
        val changePasswordResponse = MutableLiveData<Result<String>>()
        val myCompositeDisposable = CompositeDisposable()
        myCompositeDisposable.add(
            ApiService.invoke(context).changeProfileImage(profileImageModel)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val status = it.asJsonObject.get(Key.STATUS)
                    val message = it.asJsonObject.get(Key.MESSAGE)
                    val profile_image = it.asJsonObject.get(Key.PROFILE_IMAGE)
                    if (profile_image != null) {
                        changePasswordResponse.postValue(Result.success(profile_image.toString()))
                    } else {
                        changePasswordResponse.postValue(Result.error(it.asJsonObject.get(Key.MESSAGE).asString))
                    }
                }, {
                    changePasswordResponse.postValue(Result.error(it.message.toString()))
                })
        )
        return changePasswordResponse
    }

    fun forgetPassword(user: User): MutableLiveData<Result<String>> {
        val changePasswordResponse = MutableLiveData<Result<String>>()
        val myCompositeDisposable = CompositeDisposable()
        myCompositeDisposable.add(
            ApiService.invoke(context).forgetPassword(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val status = it.asJsonObject.get(Key.STATUS)
                    val otp = it.asJsonObject.get(Key.OTP)
                    val message = it.asJsonObject.get(Key.MESSAGE)
                    if (otp != null) {
                        changePasswordResponse.postValue(Result.success(message.toString()))
                        sharedPrefsManager.putInt(Preference.OTP, otp.asInt)
                    } else {
                        changePasswordResponse.postValue(Result.error(it.asJsonObject.get(Key.MESSAGE).asString))
                    }
                }, {

                    changePasswordResponse.postValue(Result.error(it.message.toString()))
                })
        )
        return changePasswordResponse
    }

    fun updatePassword(user: User): MutableLiveData<Result<String>> {
        val changePasswordResponse = MutableLiveData<Result<String>>()
        val myCompositeDisposable = CompositeDisposable()
        myCompositeDisposable.add(
            ApiService.invoke(context).updatePassword(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val status = it.asJsonObject.get(Key.STATUS)
                    if (status.asBoolean) {
                        changePasswordResponse.postValue(Result.success("Password Updated Successfully"))
                    } else {
                        changePasswordResponse.postValue(Result.error(it.asJsonObject.get(Key.MESSAGE).asString))
                    }
                }, {

                    changePasswordResponse.postValue(Result.error(it.message.toString()))
                })
        )
        return changePasswordResponse
    }
}