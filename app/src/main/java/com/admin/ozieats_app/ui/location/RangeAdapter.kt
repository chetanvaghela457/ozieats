package com.admin.ozieats_app.ui.location

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.admin.ozieats_app.R
import com.admin.ozieats_app.databinding.ItemSearchRangeLayoutBinding
import com.admin.ozieats_app.ui.home.HomeActivity
import com.admin.ozieats_app.utils.Preference
import com.admin.ozieats_app.utils.SharedPrefsManager
import com.admin.ozieats_app.utils.getUserFromPreference
import com.google.gson.Gson


class RangeAdapter(
    private var context: Context,
    private var ranges: ArrayList<RangeModel>,
    private var locationViewModel: LocationViewModel,
    private val viewLifecycleOwner: LifecycleOwner

) : RecyclerView.Adapter<RangeAdapter.RangeViewHolder>() {

    private var layoutInflater: LayoutInflater? = null
    private var lastCheckedRB: RadioGroup? = null
    private var locationModel: LocationModel? = null
    var mSelectedItem = -1

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

        holder.itemviewRange.radioButton.text = ranges[position].range_String
        holder.itemviewRange.radioButton.isChecked = position == mSelectedItem

        holder.itemviewRange.radioButton.setOnClickListener {

            mSelectedItem = position

            val lat = SharedPrefsManager.newInstance(context).getFloat(Preference.PREF_LAT, 0.0f)
            val lng = SharedPrefsManager.newInstance(context).getFloat(Preference.PREF_LNG, 0.0f)

            val locationModel = LocationModel()
            locationModel.lat = lat.toDouble()
            locationModel.lng = lng.toDouble()
            locationModel.radius = ranges[position].range
            locationModel.user_id = getUserFromPreference(context).id

            println("dfsdjfjdkf" + locationModel.lat + "---" + locationModel.lng + "---" + locationModel.radius)

            locationViewModel.locationSelect(locationModel).observe(viewLifecycleOwner, Observer {

                if (it.data != null) {
                    if (it.data.size > 0) {
                        SharedPrefsManager.newInstance(context)
                            .putString(Preference.PREF_LOCATION, Gson().toJson(locationModel))

                        SharedPrefsManager.newInstance(context)
                            .putString(Preference.All_RESTAURANTS, Gson().toJson(it.data))

                        SharedPrefsManager.newInstance(context)
                            .putString(Preference.RESTAURANT_DATA, Gson().toJson(it.data))
                    }

                    val intent = Intent(context, HomeActivity::class.java)
                    context.startActivity(intent)
                    (context as Activity).finish()
                } else {
                    SharedPrefsManager.newInstance(context)
                        .putString(Preference.PREF_LOCATION, Gson().toJson(locationModel))
                    SharedPrefsManager.newInstance(context)
                        .putString(Preference.RESTAURANT_DATA, "")
                    val intent = Intent(context, HomeActivity::class.java)
                    context.startActivity(intent)
                    (context as Activity).finish()
                }

                SharedPrefsManager.newInstance(context).putBoolean(Preference.HOME_DATA, false)
            })

            notifyDataSetChanged()
        }

    }

    class RangeViewHolder(
        val context: Context,
        rangeLayouts: ItemSearchRangeLayoutBinding
    ) :
        RecyclerView.ViewHolder(rangeLayouts.root) {
        val itemviewRange: ItemSearchRangeLayoutBinding = rangeLayouts


    }
}