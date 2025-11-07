package com.example.studenthandbook;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private View logoBigContainer;
    private View glowBig;
    private ImageView logoBig;
    private View contentContainer;
    private ProgressBar progressBar;
    private TextView footerText;

    private static final long LOGO_HOLD_DURATION = 800;
    private static final long LOGO_MORPH_DURATION = 1200;
    private static final long LOADING_DURATION = 3500; // Slower loading

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize views
        logoBigContainer = findViewById(R.id.logoBigContainer);
        glowBig = findViewById(R.id.glowBig);
        logoBig = findViewById(R.id.logoBig);
        contentContainer = findViewById(R.id.contentContainer);
        progressBar = findViewById(R.id.progressBar);
        footerText = findViewById(R.id.footerText);

        // Start the animation sequence
        startLogoAnimation();
    }

    private void startLogoAnimation() {
        // Phase 1: Big logo with glow appears (no fade, just show it)
        logoBigContainer.setAlpha(1f);

        // Hold the big logo for a moment before morphing
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                morphLogoAndGlow();
            }
        }, LOGO_HOLD_DURATION);
    }

    private void morphLogoAndGlow() {
        // Phase 2: Pure morph - Logo AND glow both shrink and move (NO FADING)
        // Calculate exact scale factor: from 300dp logo to 140dp logo
        float logoTargetScale = 140f / 300f; // 0.4667
        // Glow: from 360dp to 180dp
        float glowTargetScale = 180f / 360f; // 0.5

        // Calculate exact position offset for morphing logo
        // Make morphing logo land more upward, but final layout stays at original position
        float density = getResources().getDisplayMetrics().density;
        float morphTargetY = -148 * density; // Morphing logo lands higher up

        float targetY = morphTargetY;

        // Morph the entire container (logo + glow together)
        ObjectAnimator containerShrinkX = ObjectAnimator.ofFloat(logoBigContainer, "scaleX", 1f, logoTargetScale);
        ObjectAnimator containerShrinkY = ObjectAnimator.ofFloat(logoBigContainer, "scaleY", 1f, logoTargetScale);
        ObjectAnimator containerMoveY = ObjectAnimator.ofFloat(logoBigContainer, "translationY", 0f, targetY);

        // Pure morph animation - just scale and move, NO fading
        AnimatorSet morphSet = new AnimatorSet();
        morphSet.playTogether(containerShrinkX, containerShrinkY, containerMoveY);
        morphSet.setDuration(LOGO_MORPH_DURATION);
        morphSet.setInterpolator(new AccelerateDecelerateInterpolator());

        morphSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // After morph completes, instantly swap to final layout (NO FADE)
                swapToFinalLayout();
            }
        });

        morphSet.start();
    }

    private void swapToFinalLayout() {
        // Instant swap - NO FADING
        logoBigContainer.setVisibility(View.GONE);
        contentContainer.setAlpha(1f);
        footerText.setAlpha(1f);

        // Start loading progress
        startLoadingProgress();
    }

    private void startLoadingProgress() {
        // Animate the progress bar
        ValueAnimator progressAnimator = ValueAnimator.ofInt(0, 100);
        progressAnimator.setDuration(LOADING_DURATION);
        progressAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int progress = (int) animation.getAnimatedValue();
                progressBar.setProgress(progress);
            }
        });

        progressAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // Navigate to MainActivity
                navigateToMainActivity();
            }
        });

        progressAnimator.start();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        // Add smooth transition
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        // Disable back button on splash screen
        // Do nothing
    }
}
