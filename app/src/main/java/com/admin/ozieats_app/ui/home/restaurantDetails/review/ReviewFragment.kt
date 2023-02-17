package com.admin.ozieats_app.ui.home.restaurantDetails.review

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.admin.ozieats_app.R
import com.admin.ozieats_app.data.ReviewRepository
import com.admin.ozieats_app.databinding.FragmentReviewBinding
import com.admin.ozieats_app.model.RestaurantModel
import com.admin.ozieats_app.model.ReviewModel
import com.admin.ozieats_app.ui.home.restaurantDetails.RestaurantDetailsActivity
import com.admin.ozieats_app.utils.*
import kotlinx.android.synthetic.main.fragment_review.*


class ReviewFragment : Fragment(), ReviewRepository.OnNewReviewAdded {

    lateinit var binding: FragmentReviewBinding
    lateinit var reviewViewModel: ReviewViewModel
    lateinit var adapter: ReviewAdapter
    lateinit var onNewReviewListener : ReviewRepository.OnNewReviewAdded

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        onNewReviewListener = this


        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_review,
                container,
                false
            )

        reviewViewModel = ViewModelProvider(
            this,
            ReviewViewModel.ReviewViewModelFactory(
                requireContext(),this
            )
        ).get(ReviewViewModel::class.java)

        binding.reviewListner = reviewViewModel
        binding.reviewModel= ReviewModel()

        fetchData()

        binding.reviewModel!!.rating=1f
        binding.reviewRatings.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            ratingBar.rating = rating

            binding.reviewModel!!.rating=rating
        }

        return binding.root
    }

    companion object {
        fun newInstance(): ReviewFragment = ReviewFragment()
    }

    private fun fetchData() {
        reviewViewModel.getAllReviewsList().observe(viewLifecycleOwner, Observer {

            setAverageReview(it)

            val isOrdered = getCheckReviewFromPreference(requireContext())

            if (isOrdered.restaurant_id == getRestaurantFromPreference(requireContext()).restaurant_id)
            {
                if (isOrdered.status==1)
                {
                    binding.textViewWriteReview.visible()
                }else
                {
                    binding.textViewWriteReview.gone()
                }
            }

            adapter = ReviewAdapter(requireContext(), it,this)
            recyclerRestaurantReview.adapter = adapter
            recyclerRestaurantReview.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            recyclerRestaurantReview.setHasFixedSize(true)
            recyclerRestaurantReview.isNestedScrollingEnabled = false
        })

    }

    override fun onReviewAdd(reviewList: ArrayList<ReviewModel>) {

        var restaurantList=ArrayList<RestaurantModel>()
        Log.e("onReviewAdd", "onReviewAdd: ")
        adapter.setReviewData(reviewList)
        setAverageReview(reviewList)

        val ratin = reviewList.map { reviewModel: ReviewModel -> reviewModel.rating }.sum()

        (activity as RestaurantDetailsActivity).updateRestaurantReview(reviewList.size, ratin/reviewList.size)

       /* for(i in getTotalRestaurantPreference(requireContext()))
        {
            if (i.restaurant_id== getRestaurantFromPreference(requireContext()).restaurant_id)
            {
                i.totalRating=(reviewList.size)
                i.totalUserRating=(ratin/reviewList.size)
            }
            restaurantList.add(i)

            SharedPrefsManager.newInstance(requireContext()).putString(
                Preference.RESTAURANT_DATA,
                Gson().toJson(restaurantList))
        }*/
    }

    override fun userReview() {
        binding.textViewWriteReview.gone()
    }

    private fun setAverageReview(it: ArrayList<ReviewModel>)
    {

        val ratin = it.map { reviewModel: ReviewModel -> reviewModel.rating }.sum()

        if (it.size>0)
        {
            binding.totalRatings.text= (ratin/it.size).toString() +" / 5 Based on " +it.size.toString()+" ratings"
        }else
        {
            binding.totalRatings.text=  "0 / 5 Based on 0 ratings"
        }

        binding.mainRatingBar.rating=(ratin/it.size)

        for (data in it)
        {
            if (data.user_id == getUserFromPreference(requireContext()).id)
            {
                binding.textViewWriteReview.gone()
            }else
            {
                binding.textViewWriteReview.visible()
            }
        }
    }
}
