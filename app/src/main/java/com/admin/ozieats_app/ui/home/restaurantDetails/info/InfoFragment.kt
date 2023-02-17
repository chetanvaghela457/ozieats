package com.admin.ozieats_app.ui.home.restaurantDetails.info

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.admin.ozieats_app.R
import com.admin.ozieats_app.databinding.FragmentInfoBinding
import com.admin.ozieats_app.utils.getRestaurantFromPreference
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.text.SimpleDateFormat
import java.util.*

class InfoFragment : Fragment(), OnMapReadyCallback {

    lateinit var binding: FragmentInfoBinding
    lateinit var infoViewModel: InfoViewModel
    private lateinit var mGoogleMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_info,
                container,
                false
            )

        infoViewModel = ViewModelProvider(
            this,
            InfoViewModel.InfoViewModelFactory(
                requireContext()
            )
        ).get(InfoViewModel::class.java)

        binding.infoListener = infoViewModel

        binding.textViewAbout.text =
            "Welcome to " + getRestaurantFromPreference(requireContext()).restaurant_name +
                    " Located at " + getRestaurantFromPreference(
                requireContext()
            ).delivery_address + " you can reach us on " + getRestaurantFromPreference(requireContext()).phone

        binding.mondayTime.text= getRestaurantFromPreference(requireContext()).opening_time +" - "+getRestaurantFromPreference(requireContext()).closing_time
        binding.tuesdayTime.text= getRestaurantFromPreference(requireContext()).opening_time +" - "+getRestaurantFromPreference(requireContext()).closing_time
        binding.wednesdayTime.text= getRestaurantFromPreference(requireContext()).opening_time +" - "+getRestaurantFromPreference(requireContext()).closing_time
        binding.thursdayTime.text= getRestaurantFromPreference(requireContext()).opening_time +" - "+getRestaurantFromPreference(requireContext()).closing_time
        binding.fridayTime.text= getRestaurantFromPreference(requireContext()).opening_time +" - "+getRestaurantFromPreference(requireContext()).closing_time
        binding.saturdayTime.text= getRestaurantFromPreference(requireContext()).weekend_opening_time +" - "+getRestaurantFromPreference(requireContext()).weekend_closing_time
        binding.sundayTime.text= getRestaurantFromPreference(requireContext()).weekend_opening_time +" - "+getRestaurantFromPreference(requireContext()).weekend_closing_time

        currentDay(binding)

        MapsInitializer.initialize(requireContext())
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.restaurantAddressMap) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return binding.root
    }

    companion object {
        fun newInstance(): InfoFragment = InfoFragment()
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        if (googleMap != null) {
            mGoogleMap = googleMap

            addLocationOnMap()
        }
    }

    fun addLocationOnMap() {
        val location = LatLng(
            getRestaurantFromPreference(requireContext()).lat,
            getRestaurantFromPreference(requireContext()).lng
        )
        mGoogleMap.addMarker(
            MarkerOptions().position(location)
                .title(getRestaurantFromPreference(requireContext()).restaurant_name)
        )
        val cameraPosition = CameraPosition.Builder()
            .target(location)
            .zoom(15f)
            .build()
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    fun currentDay(binding: FragmentInfoBinding)
    {
        val sdf = SimpleDateFormat("EEEE")
        val d = Date()
        val dayOfTheWeek: String = sdf.format(d)

        println("dfdsfhg"+dayOfTheWeek)

        if (dayOfTheWeek.equals("Monday"))
        {
            binding.mondayText.setTypeface(binding.mondayText.typeface,Typeface.BOLD)
            binding.mondayTime.setTypeface(binding.mondayTime.typeface,Typeface.BOLD)
        }else if (dayOfTheWeek.equals("Tuesday"))
        {
            binding.tuesdayText.setTypeface(binding.tuesdayText.typeface,Typeface.BOLD)
            binding.tuesdayTime.setTypeface(binding.tuesdayTime.typeface,Typeface.BOLD)
        }else if (dayOfTheWeek == "Wednesday")
        {
            binding.wednesdayText.setTypeface(binding.wednesdayText.typeface,Typeface.BOLD)
            binding.wednesdayTime.setTypeface(binding.wednesdayTime.typeface,Typeface.BOLD)
        }else if (dayOfTheWeek.equals("Thursday"))
        {
            binding.thursdayText.setTypeface(binding.thursdayText.typeface,Typeface.BOLD)
            binding.thursdayTime.setTypeface(binding.thursdayTime.typeface,Typeface.BOLD)
        }else if (dayOfTheWeek.equals("Friday"))
        {
            binding.fridayText.setTypeface(binding.fridayText.typeface,Typeface.BOLD)
            binding.fridayTime.setTypeface(binding.fridayTime.typeface,Typeface.BOLD)
        }else if (dayOfTheWeek.equals("Saturday"))
        {
            binding.saturdayText.setTypeface(binding.saturdayText.typeface,Typeface.BOLD)
            binding.saturdayTime.setTypeface(binding.saturdayTime.typeface,Typeface.BOLD)
        }else if (dayOfTheWeek.equals("Sunday"))
        {
            binding.sundayText.setTypeface(binding.sundayText.typeface,Typeface.BOLD)
            binding.sundayTime.setTypeface(binding.sundayTime.typeface,Typeface.BOLD)
        }
    }
}