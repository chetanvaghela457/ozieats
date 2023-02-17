package com.admin.ozieats_app.ui.home.fragments.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.admin.ozieats_app.R
import kotlinx.android.synthetic.main.recent_search_item_layout.view.*

class RecentSearchAdapter(private val mContext : Context,
            private val recentSearchList : ArrayList<String>,
            private val onSearchClick : (searchString : String) -> Unit
) : RecyclerView.Adapter<RecentSearchAdapter.MyViewHolder>() {

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recent_search_item_layout, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return recentSearchList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.itemView.tv_searched_item.text = recentSearchList[holder.adapterPosition]

        holder.itemView.setOnClickListener {
            onSearchClick(recentSearchList[holder.adapterPosition])
        }

    }
}