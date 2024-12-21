package com.example.new_iwdms;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

public class CaptchaView extends View {

    private static final int CAPTCHA_LENGTH = 5;
    private String captchaText;
    private Paint paint;
    private Bitmap bitmap;
    private Canvas canvas;

    public CaptchaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setTextSize(60);
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        generateCaptchaText();
    }

    private void generateCaptchaText() {
        captchaText = generateRandomString(CAPTCHA_LENGTH);
        invalidate(); // Redraw the view with new CAPTCHA
    }

    private String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            this.canvas = new Canvas(bitmap);
        }
        canvas.drawColor(Color.WHITE);
        paint.setTextSize(60);
        paint.setColor(Color.BLACK);
        canvas.drawText(captchaText, 50, 100, paint);
        invalidate();
    }

    public String getCaptchaText() {
        return captchaText;
    }

    public void refreshCaptcha() {
        generateCaptchaText();
    }
}
