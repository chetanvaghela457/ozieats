package com.admin.ozieats_app.ui.support

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.admin.ozieats_app.R
import com.admin.ozieats_app.utils.gone
import kotlinx.android.synthetic.main.toolbar_layout.*

class FrequentlyAskedQuestionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frequently_asked_question)


        txtToolbarTitle.text = getString(R.string.faqs)
        imgShoppingCart.gone()
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }
}