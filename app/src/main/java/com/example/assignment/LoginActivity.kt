package com.example.assignment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.assignment.service.AuthService
import com.example.assignment.util.SessionManager

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var ivTogglePassword: ImageView
    private lateinit var tvError: TextView
    private lateinit var progressBar: ProgressBar

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            val bottomPadding = maxOf(systemBars.bottom, ime.bottom)
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomPadding)
            insets
        }

        initViews()
        setupListeners()
        playEntryAnimations()
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        // Block back button — user cannot go back to splash
        finishAffinity()
    }

    override fun onResume() {
        super.onResume()
        // Reset fields & error when returning from Register
        etUsername.text.clear()
        etPassword.text.clear()
        hideError()
    }

    private fun initViews() {
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        ivTogglePassword = findViewById(R.id.ivTogglePassword)
        tvError = findViewById(R.id.tvError)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupListeners() {
        // Login Button Click
        btnLogin.setOnClickListener {
            performLogin()
        }

        // Toggle Password Visibility
        ivTogglePassword.setOnClickListener {
            togglePasswordVisibility()
        }

        // Clear error when user starts typing
        etUsername.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) hideError()
        }
        etPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) hideError()
        }

        // Forgot Password — decorative
        findViewById<TextView>(R.id.tvForgotPassword).setOnClickListener {
            Toast.makeText(this, "Fitur belum tersedia", Toast.LENGTH_SHORT).show()
        }

        // Social Login — decorative
        findViewById<LinearLayout>(R.id.btnGoogle).setOnClickListener {
            Toast.makeText(this, "Fitur belum tersedia", Toast.LENGTH_SHORT).show()
        }
        findViewById<LinearLayout>(R.id.btnFacebook).setOnClickListener {
            Toast.makeText(this, "Fitur belum tersedia", Toast.LENGTH_SHORT).show()
        }

        // Sign Up → RegisterActivity
        findViewById<TextView>(R.id.tvSignUp).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun performLogin() {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

        // Validate — empty check
        if (username.isEmpty() || password.isEmpty()) {
            showError(getString(R.string.error_fields_empty))
            shakeView(findViewById(R.id.cardLogin))
            return
        }

        // Show loading
        setLoading(true)

        // DB authentication on background thread
        Thread {
            val user = AuthService.authenticate(username, password)
            runOnUiThread {
                setLoading(false)
                if (user != null) {
                    onLoginSuccess(user)
                } else {
                    showError(getString(R.string.error_login_failed))
                    shakeView(findViewById(R.id.cardLogin))
                }
            }
        }.start()
    }

    private fun onLoginSuccess(user: com.example.assignment.model.User) {
        hideError()

        // Save session
        SessionManager.login(user)

        // Success animation on button
        val scaleX = ObjectAnimator.ofFloat(btnLogin, "scaleX", 1f, 0.95f, 1.05f, 1f)
        val scaleY = ObjectAnimator.ofFloat(btnLogin, "scaleY", 1f, 0.95f, 1.05f, 1f)
        val animSet = AnimatorSet()
        animSet.playTogether(scaleX, scaleY)
        animSet.duration = 400
        animSet.start()

        Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show()

        // Navigate to Dashboard
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setLoading(loading: Boolean) {
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        btnLogin.isEnabled = !loading
        btnLogin.alpha = if (loading) 0.6f else 1f
        etUsername.isEnabled = !loading
        etPassword.isEnabled = !loading
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible

        if (isPasswordVisible) {
            etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            ivTogglePassword.setImageResource(R.drawable.ic_eye)
        } else {
            etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            ivTogglePassword.setImageResource(R.drawable.ic_eye_off)
        }

        // Keep cursor at the end
        etPassword.setSelection(etPassword.text.length)
    }

    private fun showError(message: String) {
        tvError.text = message
        tvError.visibility = View.VISIBLE

        // Fade in animation
        tvError.alpha = 0f
        tvError.animate()
            .alpha(1f)
            .setDuration(300)
            .start()
    }

    private fun hideError() {
        if (tvError.visibility == View.VISIBLE) {
            tvError.animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction {
                    tvError.visibility = View.GONE
                }
                .start()
        }
    }

    private fun shakeView(view: View) {
        val shake = ObjectAnimator.ofFloat(view, "translationX", 0f, -12f, 12f, -8f, 8f, -4f, 4f, 0f)
        shake.duration = 450
        shake.start()
    }

    private fun playEntryAnimations() {
        val logo = findViewById<ImageView>(R.id.ivLogo)
        val title = findViewById<TextView>(R.id.tvTitle)
        val subtitle = findViewById<TextView>(R.id.tvSubtitle)
        val card = findViewById<LinearLayout>(R.id.cardLogin)
        val divider = findViewById<LinearLayout>(R.id.dividerContainer)
        val social = findViewById<LinearLayout>(R.id.socialContainer)
        val signUp = findViewById<LinearLayout>(R.id.signUpContainer)

        val views = listOf(logo, title, subtitle, card, divider, social, signUp)

        views.forEachIndexed { index, view ->
            view.alpha = 0f
            view.translationY = 40f
            view.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(600)
                .setStartDelay((index * 100).toLong())
                .setInterpolator(OvershootInterpolator(1.2f))
                .start()
        }
    }
}
