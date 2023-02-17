package com.admin.ozieats_app.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.admin.ozieats_app.R;

public class EmojiRatingBar extends AppCompatRatingBar {

    private int[] iconArrayActive =  {
            R.drawable.emoji_1_active,
            R.drawable.emoji_2_active,
            R.drawable.emoji_3_active,
            R.drawable.emoji_4_active,
            R.drawable.emoji_5_active
    };

    private int[] iconArrayInactive =  {
            R.drawable.emoji_1_inactive,
            R.drawable.emoji_2_inactive,
            R.drawable.emoji_3_inactive,
            R.drawable.emoji_4_inactive,
            R.drawable.emoji_5_inactive
    };

    private TextView msgview = null;

    public EmojiRatingBar(Context context) {
        super(context);
        init();
    }

    public EmojiRatingBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EmojiRatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.setMax(5);
        this.setNumStars(5);
        this.setStepSize(1.0f);
        this.setRating(1.0f);
    }

    private Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        int stars = getNumStars();
        float rating = getRating();
        float x = 0;

        Bitmap bitmap;
        Resources res = getResources();
        Paint paint = new Paint();

        int W = getWidth();
        int H = getHeight();
        int icon_size = (W/stars)-32;//21 //(H < W)?(H):(W); //72
        if (icon_size > H-16) {
            icon_size = H-16;
        }
        int emoji_y_pos = (H/2)-icon_size/2;

        int delta = ((H > W)?(H):(W))/(stars);
        int offset = (W-(icon_size+(stars-1)*delta))/2;

        for(int i = 0; i < stars; i++) {
            if ((int) rating-1 == i) {
                bitmap = getBitmapFromVectorDrawable(getContext(), iconArrayActive[i]);
            } else {
                bitmap = getBitmapFromVectorDrawable(getContext(), iconArrayInactive[i]);
            }
            x = offset+(i*delta);
            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, icon_size, icon_size, true);
            canvas.drawBitmap(scaled, x, emoji_y_pos, paint);
            canvas.save();
        }
    }
}
