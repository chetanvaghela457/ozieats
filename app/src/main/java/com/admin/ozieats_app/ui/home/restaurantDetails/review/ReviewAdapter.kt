package com.admin.ozieats_app.ui.home.restaurantDetails.review

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.admin.ozieats_app.R
import com.admin.ozieats_app.data.ReviewRepository
import com.admin.ozieats_app.databinding.ReviewItemLayoutBinding
import com.admin.ozieats_app.model.ReviewModel
import com.admin.ozieats_app.utils.getUserFromPreference
import com.bumptech.glide.Glide


class ReviewAdapter(
    private var context: Context,
    private var reviews: ArrayList<ReviewModel>,
    private var reviewCheck: ReviewRepository.OnNewReviewAdded
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    private var layoutInflater: LayoutInflater? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {

        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.context)
        }
        val binding: ReviewItemLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.review_item_layout,
            parent,
            false
        )
        return ReviewViewHolder(
            context,
            binding
        )
    }

    override fun getItemCount(): Int {
        return reviews.size
    }

    fun setReviewData(reviewList: ArrayList<ReviewModel>) {
        this.reviews = reviewList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.itemViewReview.reviewModel = reviews[position]


        holder.itemViewReview.reviewRatingBar.rating = reviews[position].rating
        Glide.with(context).asBitmap().load(reviews[position].user_image)
            .into(holder.itemViewReview.circleUserPhoto)

//        var isOrdered= getCheckReviewFromPreference(requireContext())

        if (reviews[position].user_id == getUserFromPreference(context).id)
        {
            reviewCheck.userReview()
        }

        /*if (reviews[position].restaurant_id == getRestaurantFromPreference(context).restaurant_id)
        {

            if (isOrdered.status==0)
            {
                binding.textViewWriteReview.gone()
            }else
            {
                binding.textViewWriteReview.visible()
            }
        }*/
    }

    class ReviewViewHolder(
        val context: Context,
        reviewLayouts: ReviewItemLayoutBinding
    ) :
        RecyclerView.ViewHolder(reviewLayouts.root) {
        val itemViewReview: ReviewItemLayoutBinding = reviewLayouts
    }
}