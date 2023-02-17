package com.admin.ozieats_app.ui.orderprogress

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.admin.ozieats_app.R
import com.admin.ozieats_app.ui.qrcode.QrCodeActivity
import com.admin.ozieats_app.utils.getMyOrderPreference
import kotlinx.android.synthetic.main.fragment_pickup_progress.view.*

class PickupProgressFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_pickup_progress, container, false)

        view.tv_scanQr.setOnClickListener {

            var intent = Intent(requireContext(), QrCodeActivity::class.java)
            startActivity(intent)

        }
        var myData = getMyOrderPreference(requireContext())
        view.textViewRestaurantName.text = "Go To " + myData.restaurant_name
        view.textViewRestaurantAddress.text = myData.restaurant_address
        view.tvOrderId.text = "#" + myData.orderId

        view.tvViewDetail.setOnClickListener {

            var intent = Intent(requireContext(), OrderSummaryActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    companion object {
        fun newInstance(): PickupProgressFragment = PickupProgressFragment()
    }


}