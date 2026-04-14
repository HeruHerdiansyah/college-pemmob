package com.example.assignment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.splashMain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        playAnimations()

        // Auto-navigate to LoginActivity after 2.5 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            @Suppress("DEPRECATION")
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, 2500)
    }

    private fun playAnimations() {
        val logo = findViewById<ImageView>(R.id.ivSplashLogo)
        val appName = findViewById<TextView>(R.id.tvSplashAppName)
        val tagline = findViewById<TextView>(R.id.tvSplashTagline)
        val loading = findViewById<View>(R.id.loadingIndicator)

        // Initially invisible
        listOf(logo, appName, tagline, loading).forEach {
            it.alpha = 0f
            it.scaleX = 0.5f
            it.scaleY = 0.5f
        }

        // Logo animation — scale + fade
        val logoScaleX = ObjectAnimator.ofFloat(logo, "scaleX", 0.3f, 1f)
        val logoScaleY = ObjectAnimator.ofFloat(logo, "scaleY", 0.3f, 1f)
        val logoAlpha = ObjectAnimator.ofFloat(logo, "alpha", 0f, 1f)
        val logoSet = AnimatorSet().apply {
            playTogether(logoScaleX, logoScaleY, logoAlpha)
            duration = 800
            interpolator = OvershootInterpolator(1.5f)
        }

        // App name animation
        val nameAlpha = ObjectAnimator.ofFloat(appName, "alpha", 0f, 1f)
        val nameScaleX = ObjectAnimator.ofFloat(appName, "scaleX", 0.5f, 1f)
        val nameScaleY = ObjectAnimator.ofFloat(appName, "scaleY", 0.5f, 1f)
        val nameTransY = ObjectAnimator.ofFloat(appName, "translationY", 30f, 0f)
        val nameSet = AnimatorSet().apply {
            playTogether(nameAlpha, nameScaleX, nameScaleY, nameTransY)
            duration = 600
            interpolator = DecelerateInterpolator()
            startDelay = 400
        }

        // Tagline animation
        val tagAlpha = ObjectAnimator.ofFloat(tagline, "alpha", 0f, 1f)
        val tagScaleX = ObjectAnimator.ofFloat(tagline, "scaleX", 0.8f, 1f)
        val tagScaleY = ObjectAnimator.ofFloat(tagline, "scaleY", 0.8f, 1f)
        val tagSet = AnimatorSet().apply {
            playTogether(tagAlpha, tagScaleX, tagScaleY)
            duration = 500
            startDelay = 800
        }

        // Loading indicator
        val loadAlpha = ObjectAnimator.ofFloat(loading, "alpha", 0f, 1f)
        val loadScaleX = ObjectAnimator.ofFloat(loading, "scaleX", 0.5f, 1f)
        val loadScaleY = ObjectAnimator.ofFloat(loading, "scaleY", 0.5f, 1f)
        val loadSet = AnimatorSet().apply {
            playTogether(loadAlpha, loadScaleX, loadScaleY)
            duration = 400
            startDelay = 1200
        }

        val masterSet = AnimatorSet()
        masterSet.playTogether(logoSet, nameSet, tagSet, loadSet)
        masterSet.start()
    }
}
