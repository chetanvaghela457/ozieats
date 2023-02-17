package com.admin.ozieats_app.ui.referFriend

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.admin.ozieats_app.R
import com.admin.ozieats_app.databinding.ActivityReferFriendBinding
import com.admin.ozieats_app.model.ToolbarModel
import com.admin.ozieats_app.model.ToolbarViewModel
import com.admin.ozieats_app.utils.getUserFromPreference
import com.admin.ozieats_app.utils.toolbarSetting

class ReferFriendActivity : AppCompatActivity() {

    lateinit var binding: ActivityReferFriendBinding
    lateinit var referFriendViewModel: ReferFriendViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_refer_friend)

        val toolbarViewModel = ViewModelProvider(
            this,
            ToolbarViewModel.ToolbarViewModelFactory(this)
        ).get(ToolbarViewModel::class.java)
        toolbarSetting(
            toolbarViewModel,
            ToolbarModel(getString(R.string.refer_friend), false, true),
            this,
            binding.referFrdToolbar
        )

        referFriendViewModel =
            ViewModelProvider(
                this,
                ReferFriendViewModel.ReferFriendModelFactory(this)
            ).get(
                ReferFriendViewModel::class.java
            )

        binding.referListener = referFriendViewModel

        println("dgkshjg"+getUserFromPreference(this).referral_code)
        binding.yourRefferelCode.text = getUserFromPreference(this).referral_code
    }
}