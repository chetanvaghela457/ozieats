package com.admin.ozieats_app.ui.home.fragments.myorders

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.admin.ozieats_app.R
import com.admin.ozieats_app.databinding.FragmentMyOrdersBinding
import com.admin.ozieats_app.model.MyOrdersModel
import com.admin.ozieats_app.utils.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_my_orders.*

class MyOrdersFragment : Fragment(), FirebaseDataGet.OnOrderStatusChanged {

    lateinit var binding: FragmentMyOrdersBinding

    lateinit var myOrdersViewModel: MyOrdersViewModel
    var order_id = 0
    var orderStatus = 0
    var adapter: MyOrdersAdapter? = null
    var myOrderList: ArrayList<MyOrdersModel>? = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_my_orders,
                container,
                false
            )

        myOrdersViewModel = ViewModelProvider(
            this,
            MyOrdersViewModel.MyOrdersModelFactory(
                requireContext()
            )
        ).get(MyOrdersViewModel::class.java)

        binding.myOrderListener = myOrdersViewModel

        if (arguments != null) {
            val hideBack = requireArguments().getBoolean("backButtonHide", false)
            if (hideBack) {
                binding.imgBackArrow.gone()

            }
        }

        binding.imgBackArrow.setOnClickListener {

            activity?.onBackPressed()

        }
        fetchData()

        Log.e(
            "order_id_change",
            "onCreate: " + SharedPrefsManager.newInstance(requireContext()).getString(
                Preference.ORDER_ID
            ).toString()
        )
        var order_id =
            SharedPrefsManager.newInstance(requireContext()).getString(Preference.ORDER_ID)

        return binding.root
    }

    private fun fetchData() {

        myOrdersViewModel.getAllOrdersList().observe(viewLifecycleOwner, Observer {

            if (it.status == Result.Status.SUCCESS) {

                myOrderList = it.data
                SharedPrefsManager.newInstance(requireContext())
                    .putString(Preference.ALLORDERS, Gson().toJson(it.data))
                if (myOrderList != null) {
                    binding.myOrdersRecycler.visible()
                    binding.emptyLayout.gone()
                    adapter = MyOrdersAdapter(requireContext(), myOrderList!!)
                    myOrdersRecycler.adapter = adapter
                    runAnimationAgain(requireContext(),myOrdersRecycler)
                    adapter!!.notifyDataSetChanged()
                }
            } else {
                binding.myOrdersRecycler.gone()
                binding.emptyLayout.visible()
            }
        })


    }

    companion object {
        fun newInstance(): MyOrdersFragment = MyOrdersFragment()
    }

    fun changedStatus(orderID: Int, orderStatus: Int) {

        Log.e("Called", "called.....size " + myOrderList!!.size)

        for (orderData in myOrderList!!) {
            Log.e("Called", "changeOrderStatus: ***** " + orderData.request_id + "-----" + orderID)
            if (orderData.request_id == orderID) {

                Log.e("CalledInterface", "changeOrderStatus:++++++++++")
                orderData.status = orderStatus
                adapter = MyOrdersAdapter(requireContext(), myOrderList!!)
                myOrdersRecycler.adapter = adapter
                runAnimationAgain(requireContext(),myOrdersRecycler)
                adapter!!.notifyDataSetChanged()
                break
            }
        }
    }

    override fun changeOrderStatus(orderID: Int, orderStatus: Int) {

        Log.e("CalledInterface", "changeOrderStatus: ============== ")

        if (myOrderList != null) {
            for (orderData in myOrderList!!) {
                orderData.status = orderStatus
                if (orderData.request_id == orderID) {
                    adapter = MyOrdersAdapter(requireContext(), myOrderList!!)
                    myOrdersRecycler.adapter = adapter
                    runAnimationAgain(requireContext(),myOrdersRecycler)
                    adapter!!.notifyDataSetChanged()
                }
            }

        }
    }

    override fun onResume() {
        super.onResume()

        myOrderList = getAllOrdersPreference(requireContext())
        if (adapter != null) {
            if (myOrderList != null) {
                adapter!!.setData(myOrderList!!)
            }

        }
    }


}