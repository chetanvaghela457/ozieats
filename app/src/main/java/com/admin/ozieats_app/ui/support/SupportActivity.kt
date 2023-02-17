package com.admin.ozieats_app.ui.support

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.admin.ozieats_app.R
import com.admin.ozieats_app.databinding.ActivitySupportBinding
import com.admin.ozieats_app.model.ToolbarModel
import com.admin.ozieats_app.model.ToolbarViewModel
import com.admin.ozieats_app.utils.toolbarSetting
import kotlinx.android.synthetic.main.activity_support.*

class SupportActivity : AppCompatActivity() {

    lateinit var binding: ActivitySupportBinding
    lateinit var supportViewModel: SupportViewModel
    var versionName:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_support)

        val toolbarViewModel = ViewModelProvider(
            this,
            ToolbarViewModel.ToolbarViewModelFactory(this)
        ).get(ToolbarViewModel::class.java)
        toolbarSetting(
            toolbarViewModel,
            ToolbarModel(getString(R.string.support), false, true),
            this,
            binding.supportToolbar
        )

        supportViewModel =
            ViewModelProvider(
                this,
                SupportViewModel.SupportModelFactory(this)
            ).get(
                SupportViewModel::class.java
            )

        binding.supportListener = supportViewModel

        var info: PackageInfo? = null
        try {
            info = packageManager.getPackageInfo(
                packageName, 0
            )
            versionName = info.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        iniClickListener()
    }

//    private fun initViews() {
//        txtToolbarTitle.text = getString(R.string.support)
//        imgShoppingCart.inVisible() //Hide Cart Icon
//    }

    private fun iniClickListener() {

        //Ask Questions
        cl_faqContainer.setOnClickListener {

            var intent=Intent(this,FrequentlyAskedQuestionActivity::class.java)
            startActivity(intent)

        }

        //Email Support
        cl_esContainer.setOnClickListener {

            // Contact Us
            var intent = Intent(
                Intent.ACTION_SENDTO,
                Uri.parse("mailto:" + resources.getString(R.string.contact_gmail))
            )
            intent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name)
            intent.putExtra(
                Intent.EXTRA_TEXT,
                """
                    MODEL: ${Build.MODEL}
                    SDK: ${Build.VERSION.SDK_INT}
                    VERSION: $versionName
                    PKG:$packageName
                    """.trimIndent()
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }

        }
    }
}