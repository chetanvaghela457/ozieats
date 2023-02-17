package com.admin.ozieats_app.ui.home.fragments.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.admin.ozieats_app.R
import com.admin.ozieats_app.databinding.ItemSearchRangeLayoutBinding
import com.admin.ozieats_app.ui.location.LocationModel
import com.admin.ozieats_app.ui.location.LocationViewModel
import com.admin.ozieats_app.ui.location.RangeModel
import kotlinx.android.synthetic.main.item_search_range_layout.view.*


class HomeRangeAdapter(
    private var context: Context,
    private var ranges: ArrayList<RangeModel>,
    private var locationViewModel: LocationViewModel,
    private val viewLifecycleOwner: LifecycleOwner,
    private val homeViewModel: HomeViewModel,
    private val locationModel: LocationModel,
    var onRangeClick: (range: Int) -> Unit
) : RecyclerView.Adapter<HomeRangeAdapter.RangeViewHolder>() {

    private var layoutInflater: LayoutInflater? = null
    private var mSelectedItem = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RangeViewHolder {

        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.context)
        }
        val binding: ItemSearchRangeLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_search_range_layout,
            parent,
            false
        )
        binding.locationViewModel = locationViewModel
        return RangeViewHolder(context, binding)
    }

    override fun getItemCount(): Int {
        return ranges.size
    }


    override fun onBindViewHolder(holder: RangeViewHolder, position: Int) {

        holder.itemviewRange.radioButton.text = ranges[holder.adapterPosition].range_String

        if (ranges[holder.adapterPosition].range == locationModel.radius) {
            mSelectedItem = holder.adapterPosition
        }

        holder.itemView.radioButton.setOnClickListener {
            mSelectedItem = holder.adapterPosition
            onRangeClick(ranges[holder.adapterPosition].range)
            notifyDataSetChanged()
        }
        holder.itemviewRange.radioButton.isChecked = holder.adapterPosition == mSelectedItem
    }

    class RangeViewHolder(
        val context: Context,
        rangeLayouts: ItemSearchRangeLayoutBinding
    ) :
        RecyclerView.ViewHolder(rangeLayouts.root) {
        val itemviewRange: ItemSearchRangeLayoutBinding = rangeLayouts
    }
}