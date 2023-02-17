package com.admin.ozieats_app.ui.home.fragments.myorders

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.admin.ozieats_app.R
import com.admin.ozieats_app.databinding.MyordersItemLayoutBinding
import com.admin.ozieats_app.model.MyOrdersModel
import com.admin.ozieats_app.ui.orderprogress.OrderProgressActivity
import com.admin.ozieats_app.ui.orderprogress.OrderSummaryActivity
import com.admin.ozieats_app.utils.Preference
import com.admin.ozieats_app.utils.RecyclerTouchListener
import com.admin.ozieats_app.utils.RecyclerTouchListener.ClickListener
import com.admin.ozieats_app.utils.SharedPrefsManager
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import kotlinx.android.synthetic.main.myorders_item_layout.view.*


class MyOrdersAdapter(
    private var context: Context,
    private var myOrders: ArrayList<MyOrdersModel>
) : RecyclerView.Adapter<MyOrdersAdapter.MyOrdersViewHolder>() {

    private var layoutInflater: LayoutInflater? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyOrdersViewHolder {

        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.context)
        }
        val binding: MyordersItemLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.myorders_item_layout,
            parent,
            false
        )


        return MyOrdersViewHolder(context, binding)
    }

    override fun getItemCount(): Int {
        return myOrders.size
    }

    override fun onBindViewHolder(holder: MyOrdersViewHolder, position: Int) {
        holder.itemviewMyOrders.myOrderModel = myOrders[position]

        setOrderStatus(holder.itemviewMyOrders.textViewOrderStatus, myOrders[position].request_id)

        val adapter = OrdersItemAdapter(context, myOrders[position].orderItemModel!!)
        holder.itemviewMyOrders.ordersItemRecycler.adapter = adapter
        Glide.with(context).asBitmap().load(myOrders[position].restaurantImage)
            .into(holder.itemView.restaurantLogo)

        holder.itemviewMyOrders.ordersItemRecycler.addOnItemTouchListener(
            RecyclerTouchListener(
                context,
                holder.itemviewMyOrders.ordersItemRecycler,
                ClickListener { _, _ ->
                    holder.itemviewMyOrders.mainCardView.performClick()
                })
        )

        holder.itemviewMyOrders.mainCardView.setOnClickListener {
            when (holder.itemviewMyOrders.textViewOrderStatus.text) {

                /* "Order Pending For Accept" -> {
                     myOrders[position].status=0
                 }*/

                "Order Received by restaurant" -> {
                    myOrders[position].status = 0
                    val intent = Intent(context, OrderProgressActivity::class.java)
                    SharedPrefsManager.newInstance(context)
                        .putBoolean(Preference.ISFROMDIRECTORDER, false)
                    context.startActivity(intent)
                    SharedPrefsManager.newInstance(context).putString(
                        Preference.MY_ORDER,
                        Gson().toJson(myOrders[holder.adapterPosition])
                    )
                    SharedPrefsManager.newInstance(context).putInt(
                        Preference.ORDER_REQUEST_ID,
                        myOrders[holder.adapterPosition].request_id
                    )
                    SharedPrefsManager.newInstance(context)
                        .putBoolean(Preference.ISFAVOURITE, false)
                }
                "Reached at restaurant" -> {

                    myOrders[position].status = 1
                    val intent = Intent(context, OrderProgressActivity::class.java)
                    context.startActivity(intent)
                    SharedPrefsManager.newInstance(context).putString(
                        Preference.MY_ORDER,
                        Gson().toJson(myOrders[holder.adapterPosition])
                    )
                    SharedPrefsManager.newInstance(context).putInt(
                        Preference.ORDER_REQUEST_ID,
                        myOrders[holder.adapterPosition].request_id
                    )
                    SharedPrefsManager.newInstance(context)
                        .putBoolean(Preference.ISFAVOURITE, false)
                }
                "Reached restaurant - waiting outside" -> {

                    myOrders[position].status = 2
                    val intent = Intent(context, OrderProgressActivity::class.java)
                    context.startActivity(intent)
                    SharedPrefsManager.newInstance(context).putString(
                        Preference.MY_ORDER,
                        Gson().toJson(myOrders[holder.adapterPosition])
                    )
                    SharedPrefsManager.newInstance(context).putInt(
                        Preference.ORDER_REQUEST_ID,
                        myOrders[holder.adapterPosition].request_id
                    )
                    SharedPrefsManager.newInstance(context)
                        .putBoolean(Preference.ISFAVOURITE, false)
                }
                else -> {
                    myOrders[position].status = 3
                    val intent = Intent(context, OrderSummaryActivity::class.java)
                    intent.putExtra("Position", position)
                    intent.putExtra("IsFav", myOrders[position].favourite)
                    context.startActivity(intent)
                    SharedPrefsManager.newInstance(context).putString(
                        Preference.MY_ORDER,
                        Gson().toJson(myOrders[holder.adapterPosition])
                    )
                    SharedPrefsManager.newInstance(context)
                        .putBoolean(Preference.ISFAVOURITE, false)
                }
            }
        }
    }

    fun setData(myOrders: ArrayList<MyOrdersModel>) {
        this.myOrders = myOrders
        notifyDataSetChanged()
    }

    class MyOrdersViewHolder(
        val context: Context,
        myOrdersLayouts: MyordersItemLayoutBinding
    ) :
        RecyclerView.ViewHolder(myOrdersLayouts.root) {
        val itemviewMyOrders: MyordersItemLayoutBinding = myOrdersLayouts
    }

    private fun setOrderStatus(textView: TextView, orderId: Int) {
        val mRef = FirebaseDatabase.getInstance().reference
        mRef.child("current_request").child(
            orderId.toString()
        ).child("status")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val orderStatus = snapshot.getValue(Long::class.java)
                        Log.e("dsgksjhkjs", "onDataChange: " + orderStatus)

                        when {
                            orderStatus!!.toInt() == 0 -> {
                                textView.text = "Order Received by restaurant"
                                textView.setTextColor(Color.parseColor("#B7C453"))
                            }
                            orderStatus.toInt() == 1 -> {
                                textView.text = "Reached at restaurant"
                                textView.setTextColor(Color.parseColor("#FF9501"))
                            }
                            orderStatus.toInt() == 2 -> {
                                textView.text = "Reached restaurant - waiting outside"
                                textView.setTextColor(Color.parseColor("#FF9501"))
                            }
                            orderStatus.toInt() == 3 -> {
                                textView.text = "Order PickedUp"
                                textView.setTextColor(Color.parseColor("#00A44A"))
                            }
                            orderStatus.toInt() == 4 -> {
                                textView.text = "Order Received by restaurant"
                                textView.setTextColor(Color.parseColor("#B7C453"))
                            }
                        }
                    }
                }
            })
    }
}