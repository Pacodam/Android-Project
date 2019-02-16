package com.stucom.franmorenoalc.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;

import java.util.Locale;

public class UserScoreView extends AppCompatImageView implements View.OnClickListener {

    private int score;
    private final Paint paint;

    public UserScoreView(Context context) { this(context, null, 0); }
    public UserScoreView(Context context, @Nullable AttributeSet attrs) {this(context, attrs, 0); }
    public UserScoreView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setOnClickListener(this);
        // Painters
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(0.5f);
        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/font_segments.ttf");
        paint.setTypeface(font);
        paint.setTextSize(13);
    }

    public void setScore(int score) {
        this.score = score;
        this.invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();
        int gapX = (w > h) ? (w-h)/2 : 0;
        int gapY = (h > w) ? (h-w)/2 : 0;
        int size = Math.min(w,h);
        canvas.translate(gapX, gapY);
        canvas.scale(size / 100.0f, size / 100.0f);
        paint.setColor(Color.argb(48, 255, 255, 255));
        canvas.drawText("888888", 17, 86, paint);
        paint.setColor(Color.RED);
        String s = String.format(Locale.getDefault(), "%06d", score);
        canvas.drawText(s, 17, 86, paint);
    }

    @Override
    public void onClick(View v) {
        // Change Background Resource to a random "avatarXX", (01-12)
        int n = (int)(Math.random()*12 + 1);
        String resourceName = String.format(Locale.getDefault(),"avatar%02d", n);
        int resource = getResources().getIdentifier(resourceName, "mipmap", getContext().getPackageName());
        this.setBackgroundResource(resource);
    }
}
