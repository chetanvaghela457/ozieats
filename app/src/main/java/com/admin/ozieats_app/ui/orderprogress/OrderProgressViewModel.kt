package com.admin.ozieats_app.ui.orderprogress

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.admin.ozieats_app.R

class OrderProgressViewModel(private var context: Context) : ViewModel() {

    fun openFragment(fragment: Fragment) {
        val transaction =
            (context as OrderProgressActivity).supportFragmentManager.beginTransaction()
        transaction.replace(R.id.progressContainer, fragment)
//        transaction.addToBackStack(null)
        transaction.commit()
    }

    class OrderProgressModelFactory(private val context: Context) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return OrderProgressViewModel(context) as T
        }
    }
}