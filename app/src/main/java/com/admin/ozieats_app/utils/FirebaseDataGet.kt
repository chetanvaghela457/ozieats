package com.admin.ozieats_app.utils

import android.util.Log
import com.google.firebase.database.*

class FirebaseDataGet {


    fun dataFromFirebase(orderID: Int, getOrderStatus:OnOrderStatusChanged)
    {

        var firebaseDatabase = FirebaseDatabase.getInstance()
        val reference: DatabaseReference = firebaseDatabase.reference
        reference.child("current_request").child(
            orderID.toString()).child("status").addValueEventListener(object :
            ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists())
                {
                    val orderStatus = snapshot.getValue(Long::class.java)
                    Log.e("dsgksjhkjs", "onDataChange: "+orderStatus)

                    if (orderStatus!=null) {
                        getOrderStatus.changeOrderStatus(orderID,orderStatus.toInt())
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    interface OnOrderStatusChanged
    {
        fun changeOrderStatus(orderID: Int,orderStatus: Int)
    }


}