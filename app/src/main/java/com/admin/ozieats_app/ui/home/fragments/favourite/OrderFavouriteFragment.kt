package com.admin.ozieats_app.ui.home.fragments.favourite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.admin.ozieats_app.R
import com.admin.ozieats_app.databinding.FragmentOrderFavouriteBinding
import com.admin.ozieats_app.model.MyOrdersModel
import com.admin.ozieats_app.utils.*

class OrderFavouriteFragment : Fragment() {

    lateinit var binding: FragmentOrderFavouriteBinding
    lateinit var favouriteOrderViewModel: FavouriteOrderViewModel
    lateinit var adapter: FavouriteOrderAdapter
    var favoriteOrders = ArrayList<MyOrdersModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_order_favourite,
                container,
                false
            )

        favouriteOrderViewModel = ViewModelProvider(
            this,
            FavouriteOrderViewModel.FavouriteOrderModelFactory(
                requireContext()
            )
        ).get(FavouriteOrderViewModel::class.java)

        binding.favouriteOrderListener = favouriteOrderViewModel

        fetchData()

        return binding.root
    }

    private fun fetchData() {
        favouriteOrderViewModel.getAllFavouriteList().observe(viewLifecycleOwner, Observer {

            if (it.status == Result.Status.SUCCESS) {
                if (it.data!!.size > 0) {

                    binding.recyclerFavouriteOrder.visible()
                    binding.noItemFound.gone()
                    favoriteOrders = it.data
                    adapter =
                        FavouriteOrderAdapter(
                            requireContext(),
                            favoriteOrders,
                            favouriteOrderViewModel,
                            onFavouriteButtonClick = { myOrderModel, isFav ->
                                favouriteButtonClick(myOrderModel, isFav)
                            }
                        )
                    binding.recyclerFavouriteOrder.adapter = adapter
                    runAnimationAgain(requireContext(),binding.recyclerFavouriteOrder)
                    adapter.notifyDataSetChanged()
                }
            } else {
                binding.recyclerFavouriteOrder.gone()
                binding.noItemFound.visible()
            }
        })
    }

    fun favouriteButtonClick(myOrderModel: MyOrdersModel, isFav: Int) {

        val loader = Loader(context, android.R.style.Theme_Translucent_NoTitleBar)
        loader.show()
        loader.setCancelable(false)
        loader.setCanceledOnTouchOutside(false)
        favouriteOrderViewModel.addToFavourite(
            getUserFromPreference(requireContext()).id,
            myOrderModel.request_id
        ).observeForever {

            loader.cancel()
            if (it.status == Result.Status.SUCCESS) {

                favoriteOrders.remove(myOrderModel)
                adapter.notifyDataSetChanged()
                if (favoriteOrders.size > 0) {
                    binding.recyclerFavouriteOrder.visible()
                    binding.noItemFound.gone()

                } else {
                    binding.recyclerFavouriteOrder.gone()
                    binding.noItemFound.visible()
                }

            } else {
                showAlert(requireContext(), it.message.toString())
            }

        }

    }
}