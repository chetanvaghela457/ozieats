package com.admin.ozieats_app.ui.home.fragments.favourite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.admin.ozieats_app.R
import com.admin.ozieats_app.data.LocationRepository
import com.admin.ozieats_app.databinding.FragmentPlaceFavouriteBinding
import com.admin.ozieats_app.model.RestaurantModel
import com.admin.ozieats_app.ui.home.fragments.home.HomeViewModel
import com.admin.ozieats_app.utils.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_place_favourite.*


class PlaceFavouriteFragment : Fragment(), LocationRepository.CartItemTotalGet {

    lateinit var binding: FragmentPlaceFavouriteBinding
    lateinit var favouritePlacesViewModel: FavouritePlacesViewModel
    lateinit var homeViewModel: HomeViewModel
    lateinit var adapter: FavouriteRestaurantAdapter
    val progress = ObservableField<Boolean>()
    var restaurants = ArrayList<RestaurantModel>()
    lateinit var onReviewGet: LocationRepository.CartItemTotalGet


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_place_favourite,
                container,
                false
            )

        favouritePlacesViewModel = ViewModelProvider(
            this,
            FavouritePlacesViewModel.FavouritePlacesModelFactory(
                requireContext()
            )
        ).get(FavouritePlacesViewModel::class.java)

        onReviewGet = this

        binding.placeListener = favouritePlacesViewModel

        homeViewModel =
            ViewModelProvider(this, HomeViewModel.HomeViewModelFactory(requireContext(), this)).get(
                HomeViewModel::class.java
            )

        fetchData(homeViewModel)

        return binding.root
    }

    private fun fetchData(homeViewModel: HomeViewModel) {
        favouritePlacesViewModel.getAllFavouritePlaceList().observe(viewLifecycleOwner, Observer {

            if (it.status == Result.Status.SUCCESS) {

                if (it.data!!.size > 0) {
                    binding.placeFavouriteRecycler.visible()
                    binding.emptyLayout.gone()
                    restaurants = it.data
                    adapter = FavouriteRestaurantAdapter(
                        requireContext(),
                        restaurants,
                        homeViewModel,
                        favouritePlacesViewModel,
                        onFavouriteButtonClick = { restaurantModel, position ->
                            favouriteButtonClick(restaurantModel, position)
                        }
                    )
                    placeFavouriteRecycler.adapter = adapter
                    runAnimationAgain(requireContext(),binding.placeFavouriteRecycler)
                    adapter.notifyDataSetChanged()
                } else {
                    binding.placeFavouriteRecycler.gone()
                    binding.emptyLayout.visible()
                }

            }

        })
    }

    fun favouriteButtonClick(restaurantModel: RestaurantModel, position: Int) {
        val loader = Loader(context, android.R.style.Theme_Translucent_NoTitleBar)
        loader.show()
        loader.setCancelable(false)
        loader.setCanceledOnTouchOutside(false)

        homeViewModel.addOrRemoveToFavourite(restaurantModel.restaurant_id).observeForever {

            loader.cancel()
            if (it.status == Result.Status.SUCCESS) {

                restaurants.remove(restaurantModel)
                adapter.notifyDataSetChanged()

                val restaurantList = ArrayList<RestaurantModel>()
                for (i in getTotalRestaurantPreference(requireContext())) {
                    if (i.restaurant_id == restaurantModel.restaurant_id) {
                        i.favourites = 0

                    }
                    restaurantList.add(i)
                }
                SharedPrefsManager.newInstance(requireContext())
                    .putString(Preference.RESTAURANT_DATA, Gson().toJson(restaurantList))

                if (restaurants.size > 0) {
                    binding.placeFavouriteRecycler.visible()
                    binding.emptyLayout.gone()
                } else {
                    binding.placeFavouriteRecycler.gone()
                    binding.emptyLayout.visible()
                }


            } else {
                showAlert(requireContext(), it.message.toString())
            }
        }
    }

    override fun getItemTotal(itemTotal: Int) {
        TODO("Not yet implemented")
    }

}