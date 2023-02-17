package com.admin.ozieats_app.ui.home.restaurantDetails.review

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.admin.ozieats_app.data.ReviewRepository
import com.admin.ozieats_app.model.CheckForReviewModel
import com.admin.ozieats_app.model.ReviewModel
import com.admin.ozieats_app.utils.*
import com.google.gson.Gson


class ReviewViewModel(private var context: Context, val  onNewReviewListener : ReviewRepository.OnNewReviewAdded) : ViewModel() {

    private var reviewsModel = MutableLiveData<ArrayList<ReviewModel>>()
    private var reviewRepository = ReviewRepository(context)
    val progress = ObservableField<Boolean>()
    var reviewAdded: Boolean? = null

    fun getAllReviewsList(): LiveData<ArrayList<ReviewModel>> {

        val loader = Loader(context, android.R.style.Theme_Translucent_NoTitleBar)
        loader.show()
        loader.setCancelable(false)
        loader.setCanceledOnTouchOutside(false)
//        Log.e("getAllReviewsList", "getAllReviewsList: "+getRestaurantFromPreference(context).restaurant_id )
        reviewRepository.getRestaurantReview(getRestaurantFromPreference(context).restaurant_id)
            .observeForever {
                loader.cancel()
                Log.e("getAllReviewsList", "getAllReviewsList: "+ it.data.toString() )
                if (it.status == Result.Status.SUCCESS) {
//                    reviewsModel.postValue(it.data)

//                    if (it.data == getReviewFromPreference(context))
//                    {
                        reviewsModel.postValue(getReviewFromPreference(context))
//                    }



                } else {
                    showAlert(context, it.message.toString())
                }
            }

        Log.e("getAllReviewsList", "getAllReviewsList:1 ")

        reviewRepository.getCheckReview(getUserFromPreference(context).id,getRestaurantFromPreference(context).restaurant_id).observeForever {

            Log.e("getAllReviewsList", "getAllReviewsList: "+ it.data.toString() )
            if (it.status == Result.Status.SUCCESS) {

                val checkForReview=CheckForReviewModel()
                checkForReview.restaurant_id= getRestaurantFromPreference(context).restaurant_id
                checkForReview.status=it.data!!.toInt()

                SharedPrefsManager.newInstance(context).putString(Preference.CHECKIFORDERED,Gson().toJson(checkForReview))

            }


        }
        return reviewsModel
    }

    fun writeReviewButtonClick(writeRating: ConstraintLayout, btnReviewView: ConstraintLayout) {
        writeRating.visibility = View.VISIBLE
        btnReviewView.visibility = View.GONE

    }

    fun submitReviewButtonClick(
        writeRating: ConstraintLayout,
        btnReviewView: ConstraintLayout,
        reviewModel: ReviewModel,
        messageText: EditText
    ) {
        reviewModel.restaurant_id = getRestaurantFromPreference(context).restaurant_id
        reviewModel.user_id = getUserFromPreference(context).id
        reviewModel.message = messageText.text.toString()

        println("jgfkglfgj" + reviewModel.restaurant_id + "---" + reviewModel.rating + "---" + reviewModel.message)

        if (reviewModel.rating.toInt().equals(0)) {
            showAlert(context, "Please Enter rating")
        } else if (reviewModel.message.equals(null)) {
            showAlert(context, "Please Enter Message")
        } else {
            val loader = Loader(context, android.R.style.Theme_Translucent_NoTitleBar)
        loader.show()
        loader.setCancelable(false)
        loader.setCanceledOnTouchOutside(false)
            reviewRepository.addRestaurantReview(reviewModel).observeForever {

                loader.cancel()
                if (it.status == Result.Status.SUCCESS) {

                    showAlert(context, it.data.toString())
                    writeRating.visibility = View.GONE
                    btnReviewView.visibility = View.VISIBLE

                    onNewReviewListener.onReviewAdd(getReviewFromPreference(context))

//                    val ratin = getReviewFromPreference(context).map { reviewModel: ReviewModel -> reviewModel.rating }.sum()
//                    for (data in getReviewFromPreference(context))
//                    {
//                        var restauratnt_data=getRestaurantFromPreference(context)
//                        restauratnt_data.totalRating=getReviewFromPreference(context).size
//                        restauratnt_data.totalUserRating=ratin
//
//                            onNewReviewListener.onReviewChange()
//                    }




                } else {
                    showAlert(context, it.message.toString())
                }
            }
        }

    }




    class ReviewViewModelFactory(
        private val context: Context,
        private val onNewReviewListener: ReviewFragment
    ) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ReviewViewModel(
                context,onNewReviewListener
            ) as T
        }
    }


}