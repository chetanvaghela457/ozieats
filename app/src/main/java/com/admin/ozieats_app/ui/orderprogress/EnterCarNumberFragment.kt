package com.admin.ozieats_app.ui.orderprogress

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.admin.ozieats_app.R
import com.admin.ozieats_app.data.OrderRepository
import com.admin.ozieats_app.utils.*
import kotlinx.android.synthetic.main.fragment_enter_car_number.view.*

class EnterCarNumberFragment : Fragment() {

    lateinit var orderRepository: OrderRepository
    var request_id = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_enter_car_number, container, false)

        orderRepository = OrderRepository(requireActivity())

        request_id =
            SharedPrefsManager.newInstance(requireContext()).getInt(Preference.ORDER_REQUEST_ID, 0)

        view.tvSubmitCarNumber.setOnClickListener {

            if (view.editTextEnterCarNumber.text.isEmpty()) {

                showAlert(requireContext(), "Please Enter Your Car Number")

            } else {
                updateFirebase()
            }

        }

        val myData = getMyOrderPreference(requireContext())
        view.textViewRestaurantName.text = "Go To " + myData.restaurant_name
        view.textViewRestaurantAddress.text = myData.restaurant_address
        view.tvOrderId.text = "#" + myData.orderId

        view.tvViewDetail.setOnClickListener {

            val intent = Intent(requireContext(), OrderSummaryActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun updateFirebase() {
        val loader = Loader(context, android.R.style.Theme_Translucent_NoTitleBar)
        loader.show()
        loader.setCancelable(false)
        loader.setCanceledOnTouchOutside(false)
        orderRepository.pushNotifications(
            getUserFromPreference(requireContext()).id,
            request_id,
            2,
            getUserFromPreference(requireContext()).username + " is Here to pickup order and waiting in car "+view?.editTextEnterCarNumber?.text.toString()
        ).observeForever {

            if (it.status == Result.Status.SUCCESS) {

                if (view?.editTextEnterCarNumber?.text != null) {
                    orderRepository.addCarNumber(
                        request_id,
                        view?.editTextEnterCarNumber?.text.toString()
                    ).observeForever {

                        if (it.status == Result.Status.SUCCESS) {
                            loader.cancel()
                        }
                    }
                    Log.e("sgsjhsgjks", "updateFirebase: ")

                    SharedPrefsManager.newInstance(requireContext())
                        .putBoolean(Preference.ORDER_PROGRESS, true)
                    val exploreFragment = PickupProgressFragment.newInstance()
                    openFragment(exploreFragment)
                }
            }else
            {
                showAlert(requireContext(),it.message.toString())
            }
        }
    }

    fun openFragment(fragment: Fragment) {
        val transaction =
            (context as OrderProgressActivity).supportFragmentManager.beginTransaction()
        transaction.replace(R.id.progressContainer, fragment)
//        transaction.addToBackStack(null)
        transaction.commit()
    }

    companion object {
        fun newInstance(): EnterCarNumberFragment = EnterCarNumberFragment()
    }
}