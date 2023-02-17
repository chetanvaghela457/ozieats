package com.admin.ozieats_app.utils;

import android.app.Dialog;
import android.content.Context;

import com.admin.ozieats_app.R;
import com.wang.avi.AVLoadingIndicatorView;

public class Loader extends Dialog {

    private AVLoadingIndicatorView loadingIndicatorView;

    public Loader(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public Loader(Context context, int theme) {
        super(context, theme);
        // TODO Auto-generated constructor stub
        setContentView(R.layout.progress_dialog);

        loadingIndicatorView=findViewById(R.id.avi);
    }

    public Loader(Context context, boolean cancelable,
                  OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        // TODO Auto-generated constructor stub
    }

    public void hideProgress()
    {
        if (loadingIndicatorView!=null)
        {
            loadingIndicatorView.hide();
        }
    }
}