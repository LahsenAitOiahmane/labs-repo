package com.example.starsgallery.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.starsgallery.R;

public class SplashActivity extends AppCompatActivity {

    private ImageView splashLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splashLogo = findViewById(R.id.logo);

        if (splashLogo != null) {
            // Modern dynamic animations
            splashLogo.animate()
                    .rotationBy(720f)
                    .scaleX(1.2f)
                    .scaleY(1.2f)
                    .alpha(1f)
                    .setDuration(1500)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .withEndAction(() -> {
                        if (splashLogo != null) {
                            splashLogo.animate()
                                    .scaleX(0.8f)
                                    .scaleY(0.8f)
                                    .translationY(2000f)
                                    .setDuration(1000)
                                    .start();
                        }
                    })
                    .start();
        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (!isFinishing()) {
                Intent intent = new Intent(SplashActivity.this, ListActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }, 3000);
    }
}
