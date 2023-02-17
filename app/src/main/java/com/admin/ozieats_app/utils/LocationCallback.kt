package com.admin.ozieats_app.utils

import android.content.Context
import android.location.Location

interface LocationCallback {
    fun updateUi(location: Location, context: Context)
}