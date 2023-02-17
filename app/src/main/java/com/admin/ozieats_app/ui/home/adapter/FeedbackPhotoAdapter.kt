package com.admin.ozieats_app.ui.home.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.admin.ozieats_app.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_feedback_sc_layout.view.*

class FeedbackPhotoAdapter(
    private val mContext: Context,
    private val mScreenShotsList: ArrayList<Uri>,
    var onPhotoRemove: (position: Int) -> Unit
) : RecyclerView.Adapter<FeedbackPhotoAdapter.MyViewHolder>() {


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(mContext)
                .inflate(
                    R.layout.item_feedback_sc_layout,
                    parent,
                    false
                )
        )
    }

    override fun getItemCount(): Int {
        return mScreenShotsList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(mContext).load(mScreenShotsList[holder.adapterPosition])
            .into(holder.itemView.iv_screenShot)

        holder.itemView.iv_close.setOnClickListener {
            onPhotoRemove(holder.adapterPosition)
        }
    }
}