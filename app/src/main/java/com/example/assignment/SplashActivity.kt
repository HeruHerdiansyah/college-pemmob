package com.example.assignment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val ivLogo = findViewById<ImageView>(R.id.ivLogo)
        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val tvSubtitle = findViewById<TextView>(R.id.tvSubtitle)
        val loadingDots = findViewById<LinearLayout>(R.id.loadingDots)
        val dot1 = findViewById<View>(R.id.dot1)
        val dot2 = findViewById<View>(R.id.dot2)
        val dot3 = findViewById<View>(R.id.dot3)

        // Logo animation: scale + fade in
        val logoScaleX = ObjectAnimator.ofFloat(ivLogo, "scaleX", 0.3f, 1f)
        val logoScaleY = ObjectAnimator.ofFloat(ivLogo, "scaleY", 0.3f, 1f)
        val logoAlpha = ObjectAnimator.ofFloat(ivLogo, "alpha", 0f, 1f)
        val logoSet = AnimatorSet().apply {
            playTogether(logoScaleX, logoScaleY, logoAlpha)
            duration = 800
            interpolator = AccelerateDecelerateInterpolator()
        }

        // Title slide up + fade in
        val titleTransY = ObjectAnimator.ofFloat(tvTitle, "translationY", 30f, 0f)
        val titleAlpha = ObjectAnimator.ofFloat(tvTitle, "alpha", 0f, 1f)
        val titleSet = AnimatorSet().apply {
            playTogether(titleTransY, titleAlpha)
            duration = 600
            startDelay = 400
        }

        // Subtitle fade in
        val subtitleAlpha = ObjectAnimator.ofFloat(tvSubtitle, "alpha", 0f, 1f)
        subtitleAlpha.duration = 500
        subtitleAlpha.startDelay = 800

        // Loading dots fade in
        val dotsAlpha = ObjectAnimator.ofFloat(loadingDots, "alpha", 0f, 1f)
        dotsAlpha.duration = 400
        dotsAlpha.startDelay = 1200

        // Play all animations
        val mainSet = AnimatorSet()
        mainSet.playTogether(logoSet, titleSet, subtitleAlpha, dotsAlpha)
        mainSet.start()

        // Animate dots pulsing
        animateDots(dot1, dot2, dot3)

        // Navigate to LoginActivity after 2.5 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, 2500)
    }

    private fun animateDots(dot1: View, dot2: View, dot3: View) {
        val dots = listOf(dot1, dot2, dot3)
        dots.forEachIndexed { index, dot ->
            val pulse = ObjectAnimator.ofFloat(dot, "alpha", 0.3f, 1f).apply {
                duration = 500
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
                startDelay = (index * 200).toLong()
            }
            pulse.start()
        }
    }
}
