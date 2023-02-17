package com.admin.ozieats_app.utils

import android.content.Context
import android.os.SystemClock
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.admin.ozieats_app.R
import com.admin.ozieats_app.databinding.ToolbarLayoutBinding
import com.admin.ozieats_app.model.ToolbarModel
import com.admin.ozieats_app.model.ToolbarViewModel
import libs.mjn.prettydialog.PrettyDialog
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun toolbarSetting(
    toolbarViewModel: ToolbarViewModel,
    toolbarModel: ToolbarModel,
    viewLifecycleOwner: LifecycleOwner,
    toolbar: ToolbarLayoutBinding
) {
    toolbarViewModel.setToolbar(toolbarModel.title, toolbarModel.iconVisible, toolbarModel.backIcon)
    toolbarViewModel.getToolBarModel().observe(viewLifecycleOwner, Observer {
        toolbar.toolBar = it
    })
    toolbar.listener = toolbarViewModel
}

fun runAnimationAgain(context: Context, recyclerView: RecyclerView) {
    val controller: LayoutAnimationController =
        AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_up_to_down)
    recyclerView.setLayoutAnimation(controller)
    recyclerView.scheduleLayoutAnimation()
}

fun showAlert(context: Context, message: String) {
    val dialog = PrettyDialog(context)
    dialog
        .setTitleColor(R.color.colorPrimary)
        .setMessage(message)
        .setMessageColor(R.color.pdlg_color_black)
        .setAnimationEnabled(false)
        .setIcon(R.drawable.pdlg_icon_close, R.color.colorPrimary) {
            dialog.dismiss()
            Toast.makeText(context, "Close selected", Toast.LENGTH_SHORT).show()
        }
        .addButton(
            "Okay",
            R.color.pdlg_color_white,
            R.color.colorPrimary
        ) {
            dialog.dismiss()
        }

    dialog.show()
    dialog.setCancelable(false)
}

fun noInternetAvailableAlert(context: Context, message: String) {
    val dialog = PrettyDialog(context)
    dialog
        .setTitle("Failed")
        .setTitleColor(R.color.colorPrimary)
        .setMessage(message)
        .setMessageColor(R.color.pdlg_color_black)
        .setAnimationEnabled(false)
        .setIcon(R.drawable.pdlg_icon_close, R.color.colorPrimary) {
            dialog.dismiss()
            Toast.makeText(context, "Close selected", Toast.LENGTH_SHORT).show()
        }
        .addButton(
            "Okay",
            R.color.pdlg_color_white,
            R.color.colorPrimary
        ) {
            dialog.dismiss()
        }

    dialog.show()
    dialog.setCancelable(false)
}

fun logOutAlert(context: Context, message: String) {
    val dialog = PrettyDialog(context)
    dialog
        .setTitle("Alert")
        .setTitleColor(R.color.colorPrimary)
        .setMessage(message)
        .setMessageColor(R.color.pdlg_color_black)
        .setAnimationEnabled(false)
        .setIcon(R.drawable.pdlg_icon_info, R.color.colorPrimary) {
        }
        .addButton(
            "Logout",
            R.color.pdlg_color_white,
            R.color.colorPrimary
        ) {
            logout(context)
            dialog.dismiss()
        }
        .addButton(
            "Cancel",
            R.color.pdlg_color_white,
            R.color.colorPrimary
        ) {
            dialog.dismiss()
        }

    dialog.show()
    dialog.setCancelable(false)
}

fun showSuccessAlert(context: Context, message: String) {

    val dialog = PrettyDialog(context)
    dialog
//        .setTitle("Custom PrettyDialog")
        .setTitleColor(R.color.colorPrimary)
        .setMessage(message)
        .setMessageColor(R.color.pdlg_color_black)
//        .setTypeface(Typeface.createFromAsset(context.resources.assets, "myfont.ttf"))
        .setAnimationEnabled(true)
        .setIcon(R.drawable.pdlg_icon_success, R.color.colorPrimary) {
            dialog.dismiss()
        }
        .addButton(
            "Okay",
            R.color.pdlg_color_white,
            R.color.colorPrimary
        ) {
            dialog.dismiss()
        }

    dialog.show()
    dialog.setCancelable(false)
}

fun getCurrentTime(): String? {
    //date output format
    val dateFormat: DateFormat = SimpleDateFormat("ddMMyyyyHHmmss")
    val cal: Calendar = Calendar.getInstance()
    return dateFormat.format(cal.time)
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.inVisible() {
    visibility = View.INVISIBLE
}

fun View.isVisible(): Boolean {
    return visibility == View.VISIBLE
}

var mLastClickTime = 0L

fun isOpenRecently(): Boolean {
    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
        return true
    }
    mLastClickTime = SystemClock.elapsedRealtime()
    return false
}