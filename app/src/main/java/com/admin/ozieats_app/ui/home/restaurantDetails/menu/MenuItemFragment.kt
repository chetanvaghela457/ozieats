package com.admin.ozieats_app.ui.home.restaurantDetails.menu

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.admin.ozieats_app.R
import com.admin.ozieats_app.databinding.FragmentMenuItemBinding
import com.admin.ozieats_app.model.MenuItemModel
import com.admin.ozieats_app.utils.getRestaurantFromPreference
import com.admin.ozieats_app.utils.gone
import com.admin.ozieats_app.utils.visible
import kotlinx.android.synthetic.main.fragment_menu_item.*


class MenuItemFragment : Fragment() {

    lateinit var binding: FragmentMenuItemBinding
    lateinit var menuItemViewModel: MenuItemViewModel
    var menuItemArray = ArrayList<MenuItemModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_menu_item,
                container,
                false
            )

        menuItemViewModel = ViewModelProvider(
            this,
            MenuItemViewModel.MenuItemModelFactory(
                requireContext()
            )
        ).get(MenuItemViewModel::class.java)

        binding.menuItemListener = menuItemViewModel

        menuItemArray = getRestaurantFromPreference(requireContext()).food_list!!

        if (menuItemArray.size > 0) {
            binding.emptyLayout.gone()
            binding.menuTitleRecycler.visible()
            binding.visibilityConstraint.visible()
            val adapter = MenuItemAdapter(requireContext(), menuItemArray)
            binding.menuTitleRecycler.adapter = adapter
            binding.menuTitleRecycler.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            binding.menuTitleRecycler.setHasFixedSize(true)
            binding.menuTitleRecycler.isNestedScrollingEnabled = false
        } else {
            binding.emptyLayout.visible()
            binding.visibilityConstraint.gone()
            binding.menuTitleRecycler.gone()
        }

        binding.editTextItemSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {

                    filter(s.toString())

                } else {
                    val adapter = MenuItemAdapter(requireContext(), menuItemArray)
                    binding.menuTitleRecycler.adapter = adapter
                    binding.menuTitleRecycler.layoutManager =
                        LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                    binding.menuTitleRecycler.setHasFixedSize(true)
                    binding.menuTitleRecycler.isNestedScrollingEnabled = false
                }
            }

            override fun afterTextChanged(editable: Editable?) {

            }
        })

//        if (menuItemArray!=null) {
//            val adapter = MenuItemAdapter(requireContext(), menuItemArray)
//            menuTitleRecycler.adapter = adapter
//            menuTitleRecycler.layoutManager =
//                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
//            menuTitleRecycler.setHasFixedSize(true)
//            menuTitleRecycler.isNestedScrollingEnabled = false
//        }

//        fetchData()


        return binding.root
    }

    private fun filter(
        text: String
    ) {
        var menuItemModel = ArrayList<MenuItemModel>()
        for (item in menuItemArray) {

            if (item.categoryName.toLowerCase().contains(text.toLowerCase())) {

                menuItemModel.add(item)

            } else {
                for (menuitem in item.menuItemChildModel!!) {
                    if (menuitem.itemName.toLowerCase().contains(text)) {

                        menuItemModel.add(item)
                        break
                    }
                }
            }
        }
        if (menuItemModel.size > 0) {
            binding.emptyLayout.gone()
            binding.visibilityConstraint.visible()
            binding.menuTitleRecycler.visible()
            val adapter = MenuItemAdapter(requireContext(), menuItemModel)
            binding.menuTitleRecycler.adapter = adapter
            binding.menuTitleRecycler.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            binding.menuTitleRecycler.setHasFixedSize(true)
            binding.menuTitleRecycler.isNestedScrollingEnabled = false
        } else {
            binding.emptyLayout.visible()
            binding.visibilityConstraint.visible()
            binding.menuTitleRecycler.gone()
        }
    }

    companion object {
        fun newInstance(): MenuItemFragment = MenuItemFragment()
    }

    private fun fetchData() {
        menuItemViewModel.getAllMenuItems().observe(viewLifecycleOwner, Observer {
            val adapter = MenuItemAdapter(requireContext(), it)
            menuTitleRecycler.adapter = adapter
            menuTitleRecycler.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            menuTitleRecycler.setHasFixedSize(true)
            menuTitleRecycler.isNestedScrollingEnabled = false
        })
    }
}