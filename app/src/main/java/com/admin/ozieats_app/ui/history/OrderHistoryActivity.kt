package com.admin.ozieats_app.ui.history

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.admin.ozieats_app.R
import com.admin.ozieats_app.ui.home.fragments.myorders.MyOrdersFragment
import com.admin.ozieats_app.ui.home.item.NetworkConnectionListener
import com.admin.ozieats_app.utils.BaseActivity
import com.admin.ozieats_app.utils.gone
import com.admin.ozieats_app.utils.isVisible
import com.admin.ozieats_app.utils.visible
import kotlinx.android.synthetic.main.activity_order_history.*
import kotlinx.android.synthetic.main.activity_order_history.constraintLayoutMain
import kotlinx.android.synthetic.main.activity_order_history.noInternetLayout
import kotlinx.android.synthetic.main.activity_restaurant_details.*
import kotlinx.android.synthetic.main.no_internet_layout.view.*

class OrderHistoryActivity : BaseActivity(), NetworkConnectionListener {

    var isInternetConnectd = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)


        openFragment(MyOrdersFragment())
    }

    fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.historycontainer, fragment)
//        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun changeNetwork(connected: Boolean) {

        isInternetConnectd = connected
        if (connected) {
            if (!constraintLayoutMain.isVisible()) {
                noInternetLayout.retryButton.setOnClickListener {

                    noInternetLayout.gone()
                    constraintLayoutMain.visible()
                    val intent = Intent(this, OrderHistoryActivity::class.java)
                    startActivity(intent)
                }
            }
        } else {
            noInternetLayout.visible()
            constraintLayoutMain.gone()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isInternetConnectd) {
            if (!constraintLayoutMain.isVisible()) {
                noInternetLayout.gone()
                constraintLayoutMain.visible()
            }
        }
    }
}